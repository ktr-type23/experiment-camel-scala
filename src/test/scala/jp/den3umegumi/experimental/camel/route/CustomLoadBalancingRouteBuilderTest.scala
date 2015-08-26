package jp.den3umegumi.experimental.camel.route

import org.apache.camel.component.mock.MockEndpoint._
import org.apache.camel.impl.{DefaultCamelContext, JndiRegistry}
import org.apache.camel.processor.loadbalancer.LoadBalancerSupport
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.{AsyncCallback, Exchange}
import org.junit.Test

class CustomLoadBalancingRouteBuilderTest extends CamelTestSupport with RouteBuilderSupport {

  val loadBalancingName = "customLoadBalance"

  override protected def createRegistry: JndiRegistry = {
    val reg = super.createRegistry
    // CustomLoadBalancingをセット
    reg.bind(loadBalancingName, new MyCustomLoadBalancing)
    reg
  }

  override def createRouteBuilder() = new ScalaRouteBuilder(new DefaultCamelContext()) {
    "direct:custom-load-balance" ==> {
      loadbalance.custom(loadBalancingName) {
        -->("mock:a")
        -->("mock:b")
        -->("mock:c")
      }
    }
  }

  @Test
  def test_customLoadBalancer {
    val mockA = getMockEndpoint("mock:a")
    val mockB = getMockEndpoint("mock:b")
    val mockC = getMockEndpoint("mock:c")

    val b1 = "one"
    mockA.expectedBodiesReceived(b1)
    // mock:b mock:cは呼ばれない
    expectsMessageCount(0, mockB, mockC)
    template.sendBodyAndHeader("direct:custom-load-balance", b1, "foo", "bar");
    assertMockEndpointsSatisfied
    mockA.reset

    val b2 = "two"
    mockB.expectedBodiesReceived(b2)
    // mock:a mock:cは呼ばれない
    expectsMessageCount(0, mockA, mockC)
    template.sendBodyAndHeader("direct:custom-load-balance", b2, "foo", "bar");
    assertMockEndpointsSatisfied
    mockB.reset

    val b3 = "three"
    mockC.expectedBodiesReceived(b3)
    // mock:a mock:bは呼ばれない
    expectsMessageCount(0, mockA, mockB)
    template.sendBodyAndHeader("direct:custom-load-balance", b3, "foo", "bar");
    assertMockEndpointsSatisfied
    mockC.reset

  }
}

class MyCustomLoadBalancing extends LoadBalancerSupport {

  override def process(e: Exchange, callback: AsyncCallback): Boolean = {
    Option(e.getIn.getBody(classOf[String])) match {
      case Some("one") => getProcessors.get(0).process(e)
      case Some("two") => getProcessors.get(1).process(e)
      case _ => getProcessors.get(2).process(e)
    }
    callback.done(true)
    true
  }
}

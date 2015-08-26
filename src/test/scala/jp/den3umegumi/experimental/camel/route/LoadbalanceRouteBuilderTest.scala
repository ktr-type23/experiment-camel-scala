package jp.den3umegumi.experimental.camel.route

import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.component.mock.MockEndpoint.expectsMessageCount
import org.apache.camel.impl.JndiRegistry
import org.apache.camel.scala.Preamble
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

class LoadbalanceRouteBuilderTest extends CamelTestSupport with RouteBuilderSupport  with Preamble {

  override protected def createRegistry: JndiRegistry = {
    val reg = super.createRegistry
    reg.bind("customLoadBalance", new LoadBalancingRule)
    reg
  }

  override def createRouteBuilder = new LoadbalanceRouteBuilder

  @Test
  def test_custom = {
    val mockA = getMockEndpoint("mock:a")
    val mockB = getMockEndpoint("mock:b")
    val mockC = getMockEndpoint("mock:c")

    val b1 = "one"
    mockA.expectedBodiesReceived(b1)
    // mock:b mock:cは呼ばれない
    expectsMessageCount(0, mockB, mockC)
    template.sendBodyAndHeader("direct:loadbalance-custom", b1, "foo", "bar");
    assertMockEndpointsSatisfied
    mockA.reset

    val b2 = "two"
    mockB.expectedBodiesReceived(b2)
    // mock:a mock:cは呼ばれない
    expectsMessageCount(0, mockA, mockC)
    template.sendBodyAndHeader("direct:loadbalance-custom", b2, "foo", "bar");
    assertMockEndpointsSatisfied
    mockB.reset

    val b3 = "three"
    mockC.expectedBodiesReceived(b3)
    // mock:a mock:bは呼ばれない
    expectsMessageCount(0, mockA, mockB)
    template.sendBodyAndHeader("direct:loadbalance-custom", b3, "foo", "bar");
    assertMockEndpointsSatisfied
    mockC.reset
  }

  @Test
  def test_roundrobin = {

    val x: MockEndpoint = getMockEndpoint("mock:a")
    val y: MockEndpoint = getMockEndpoint("mock:b")
    val z: MockEndpoint = getMockEndpoint("mock:c")

    val b1 = "<one/>"
    x.expectedBodiesReceived(b1)
    expectsMessageCount(0, y, z);
    sendRoundRobinMessage("bar", b1)
    assertMockEndpointsSatisfied
    x.reset

    val b2 = "<two/>"
    y.expectedBodiesReceived(b2)
    expectsMessageCount(0, x, z);
    sendRoundRobinMessage("bar", b2)
    assertMockEndpointsSatisfied
    y.reset

    val b3 = "<three/>"
    z.expectedBodiesReceived(b3)
    expectsMessageCount(0, x, y);
    sendRoundRobinMessage("bar", b3)
    assertMockEndpointsSatisfied
    z.reset
  }

  def sendRoundRobinMessage(headerValue: String, body: String) {
    template.sendBodyAndHeader("direct:loadbalance-roundrobin", body, "foo", headerValue);
  }
}

package jp.den3umegumi.experimental.camel.route

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.processor.loadbalancer.LoadBalancerSupport
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.camel.{AsyncCallback, CamelContext, Exchange, LoggingLevel}

class LoadbalanceRouteBuilder(context: CamelContext = new DefaultCamelContext) extends ScalaRouteBuilder(context: CamelContext) {

  "direct:loadbalance-roundrobin" ==> {
    loadbalance.roundrobin {
      -->("direct:worker-a", "mock:a")
      -->("direct:worker-b", "mock:b")
      -->("direct:worker-c", "mock:c")
    }
  }

  "direct:loadbalance-custom" ==> {
    loadbalance.custom("customLoadBalance") {
      -->("direct:worker-a", "mock:a")
      -->("direct:worker-b", "mock:b")
      -->("direct:worker-c", "mock:c")
    }
  }

  "direct:worker-a" ==> {
    log(LoggingLevel.INFO, "foo")
  }
  "direct:worker-b" ==> {
    log(LoggingLevel.INFO, "bar")
  }
  "direct:worker-c" ==> {
    log(LoggingLevel.INFO, "baz")
  }
}

class LoadBalancingRule extends LoadBalancerSupport {

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

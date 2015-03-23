package jp.den3umegumi.experimental.camel.route

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.camel.{CamelContext, Exchange, Processor}

class HelloCamelRouteBuilder(context: CamelContext = new DefaultCamelContext) extends ScalaRouteBuilder(context: CamelContext) {

  "timer:timerName?period=3000" ==> {
    process(new Processor {
      override def process(e: Exchange): Unit = {
        println("hello")
      }
    })
  }
}

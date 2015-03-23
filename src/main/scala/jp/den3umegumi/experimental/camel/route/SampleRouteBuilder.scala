package jp.den3umegumi.experimental.camel.route

import org.apache.camel.scala.dsl.builder.RouteBuilder

class SampleRouteBuilder extends RouteBuilder {

  "timer:foo-timer?repeatCount=5&period=1000" ==> {
    log("foo timer start.")
    -->("direct:hello-world")
    log("foo timer end.")
  }

  "direct:hello-world" ==> {
    log(org.apache.camel.LoggingLevel.DEBUG, "bar direct start.")
  }
}

package jp.den3umegumi.experimental.camel.launch

import jp.den3umegumi.experimental.camel.route.HelloCamelRouteBuilder
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.slf4j.LoggerFactory

object RouteBuilderLauncher extends App with RouteBuilderSupport {

  private val logger = LoggerFactory.getLogger(getClass)

  val main = new Main
  main.enableHangupSupport;
  main.addRouteBuilder(new HelloCamelRouteBuilder)

  logger.debug("camel routing start")
  main.run
}

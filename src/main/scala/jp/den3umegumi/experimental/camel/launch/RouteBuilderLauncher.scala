package jp.den3umegumi.experimental.camel.launch

import jp.den3umegumi.experimental.camel.route._
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.slf4j.LoggerFactory

object RouteBuilderLauncher extends App with RouteBuilderSupport {

  private val logger = LoggerFactory.getLogger(getClass)

  val main = new Main
  main.enableHangupSupport;
  main.bind("customLoadBalance", new LoadBalancingRule)
  main.addRouteBuilder(new LoadbalanceRouteBuilder)

  logger.debug("camel routing start")
  main.run
}

package jp.den3umegumi.experimental.camel.bean

import org.apache.camel.Properties

class DynamicRouter {

  def slip(body: String, @Properties properties: scala.collection.mutable.Map[String, Any]): String = {
    val current = properties.get("invoked") match {
      case Some(v) => v.toString.toInt
      case None => 0
    }
    val invoked = current + 1
    properties.put("invoked", invoked)
    if (invoked == 1) {
      "mock:a"
    } else if (invoked == 2) {
      "mock:b"
    } else if (invoked == 3) {
      "mock:c"
    } else {
      null
    }
  }
}

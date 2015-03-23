package jp.den3umegumi.experimental.camel.route

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.{CamelContext, Exchange}
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder

class BasicScalaDSLRouteBuilder(context: CamelContext = new DefaultCamelContext) extends ScalaRouteBuilder(context: CamelContext) {

  private val isFooBarSuccess = (e: Exchange) => {
    e.in("Condition").asInstanceOf[String] match {
      case "foo" | "bar" => true
      case _ => false
    }
  }

  private val occurException = (e: Exchange) => {
    Option(e.in("Cause").asInstanceOf[String]) match {
      case Some("IllegalState") => throw new IllegalStateException("IllegalStateException")
      case Some(v) => throw new IllegalArgumentException("Exception")
      case None =>
    }
  }

  /**
   * 単純なif文
   */
  from("direct:filter") {
    filter(body(_) == "foo") {
      -->("mock:foo")
    }
    to("mock:result")
  }

  /**
   * if - elseif - else
   */
  "direct:if-else-body" ==> {
    choice {
      when(body(_) == "foo") {
        -->("mock:foo")
      }
      when(body(_) == "bar") {
        -->("mock:bar")
        stop
      }
      otherwise {
        -->("mock:otherwise")
      }
    }
    -->("mock:result")
  }

  /**
   * if - elseif - else
   * headerの値でも同様に比較出来る
   */
  "direct:if-else-header" ==> {
    choice {
      when(_.in("Condition") == "foo") {
        -->("mock:foo")
      }
      when(_.in("Condition") == "bar") {
        -->("mock:bar")
      } otherwise {
        -->("mock:otherwise")
      }
    }
  }

  /**
   * if - elseif - else
   * Boolean値は直接比較出来る
   */
  "direct:if-else-boolean" ==> {
    choice {
      when(_.in("BooleanCondition")) {
        -->("mock:true")
      } otherwise {
        -->("mock:false")
      }
    }
  }

  /**
   * if - elseif - else
   * Objectの有無でも判断出来る
   * nullが入っていた場合はtrueになる
   */
  "direct:if-else-object" ==> {
    choice {
      when(_.in("ConditionObject")) {
        -->("mock:found")
      } otherwise {
        -->("mock:not-found")
      }
    }
  }

  /**
   * if - elseif - else
   * 関数リテラルがBooleanを返却する場合は、そのまま比較出来る
   */
  "direct:if-else-process" ==> {
    choice {
      when(isFooBarSuccess) {
        -->("mock:true")
      } otherwise {
        -->("mock:false")
      }
    }
  }

  // ------------------------------------------------------
  // Loop
  // ------------------------------------------------------
  /**
   * bodyにIntなどの数値が入っていればその回数ループする
   */
  "direct:loop-body" ==> {
    loop(body(_)) {
      -->("mock:loop")
    }
  }

  /**
   * bodyにIntなどの数値が入っていればその回数ループする
   * Headerでも同様に記述出来る
   */
  "direct:loop-header" ==> {
    loop(_.in("LoopSize")) {
      -->("mock:loop")
    }
    log("Header Loop end.")
  }

  /**
   * seqのサイズ分Loopさせる
   */
  "direct:loop-seq-size" ==> {
    loop(_.in("LoopObject").asInstanceOf[Seq[Any]].size) {
      -->("mock:loop")
    }
  }

  /**
   * 二重Loopも問題なく出来る
   */
  "direct:double-loop" ==> {
    loop(_.in("FirstLoopCount")) {
      -->("mock:first-loop")
      loop(_.in("SecondLoopCount")) {
        -->("mock:second-loop")
      }
    }
  }

  "direct:attempt-handle" ==> {
    attempt {
      process(occurException)
      -->("mock:result")
    } handle (classOf[IllegalStateException]) apply {
      -->("mock:illegal-state-exception")
    } handle (classOf[Exception]) apply {
      -->("mock:exception")
    } ensure {
      -->("mock:finally")
    }
  }
}

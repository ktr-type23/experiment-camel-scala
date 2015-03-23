package jp.den3umegumi.experimental.camel.route

import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

import scala.collection.JavaConversions._
import scala.collection.mutable

class BasicScalaDSLRouteBuilderTest extends CamelTestSupport with RouteBuilderSupport {

  override def createRouteBuilder = new BasicScalaDSLRouteBuilder

  // ---------------------------------------------
  // "direct:filter" test
  // ---------------------------------------------
  @Test
  def filterConditionFilterCallFooBar = {
    getMockEndpoint("mock:foo").expectedMessageCount(1)
    getMockEndpoint("mock:result").expectedMessageCount(1)
    template.sendBody("direct:filter", "foo")
    assertMockEndpointsSatisfied
  }

  @Test
  def filterConditionChoiceCallBar = {
    getMockEndpoint("mock:foo").expectedMessageCount(0)
    getMockEndpoint("mock:result").expectedMessageCount(1)
    template.sendBody("direct:filter", "bar")
    assertMockEndpointsSatisfied
  }

  // ---------------------------------------------
  // "direct:if-else-body" test
  // ---------------------------------------------
  @Test
  def bodyConditionChoiceCallFoo = {
    getMockEndpoint("mock:foo").expectedMessageCount(1)
    getMockEndpoint("mock:bar").expectedMessageCount(0)
    getMockEndpoint("mock:otherwise").expectedMessageCount(0)
    getMockEndpoint("mock:result").expectedMessageCount(1)
    template.sendBody("direct:if-else-body", "foo")
    assertMockEndpointsSatisfied
  }

  @Test
  def bodyConditionChoiceCallBar = {
    getMockEndpoint("mock:foo").expectedMessageCount(0)
    getMockEndpoint("mock:bar").expectedMessageCount(1)
    getMockEndpoint("mock:otherwise").expectedMessageCount(0)
    getMockEndpoint("mock:result").expectedMessageCount(0)
    template.sendBody("direct:if-else-body", "bar")
    assertMockEndpointsSatisfied
  }

  @Test
  def bodyConditionChoiceCallOtherwise = {
    getMockEndpoint("mock:foo").expectedMessageCount(0)
    getMockEndpoint("mock:bar").expectedMessageCount(0)
    getMockEndpoint("mock:otherwise").expectedMessageCount(1)
    getMockEndpoint("mock:result").expectedMessageCount(1)
    template.sendBody("direct:if-else-body", "baz")
    assertMockEndpointsSatisfied
  }

  // ---------------------------------------------
  // "direct:if-else-header" test
  // ---------------------------------------------
  @Test
  def headerConditionChoiceCallFoo = {
    getMockEndpoint("mock:foo").expectedMessageCount(1)
    getMockEndpoint("mock:bar").expectedMessageCount(0)
    getMockEndpoint("mock:otherwise").expectedMessageCount(0)
    template.sendBodyAndHeader("direct:if-else-header", "baz", "Condition", "foo")
    assertMockEndpointsSatisfied
  }

  @Test
  def headerConditionChoiceCallBar = {
    getMockEndpoint("mock:foo").expectedMessageCount(0)
    getMockEndpoint("mock:bar").expectedMessageCount(1)
    getMockEndpoint("mock:otherwise").expectedMessageCount(0)
    template.sendBodyAndHeader("direct:if-else-header", "foo", "Condition", "bar")
    assertMockEndpointsSatisfied
  }

  @Test
  def headerConditionChoiceCallOtherwise = {
    getMockEndpoint("mock:foo").expectedMessageCount(0)
    getMockEndpoint("mock:bar").expectedMessageCount(0)
    getMockEndpoint("mock:otherwise").expectedMessageCount(1)
    template.sendBodyAndHeader("direct:if-else-header", "bar", "Condition", "baz")
    assertMockEndpointsSatisfied
  }

  // ---------------------------------------------
  // "direct:if-else-boolean" test
  // ---------------------------------------------
  @Test
  def booleanConditionChoiceCallTrue = {
    getMockEndpoint("mock:true").expectedMessageCount(1)
    getMockEndpoint("mock:false").expectedMessageCount(0)
    template.sendBodyAndHeader("direct:if-else-boolean", None, "BooleanCondition", true)
    assertMockEndpointsSatisfied
  }

  @Test
  def booleanConditionChoiceCallFalse = {
    getMockEndpoint("mock:true").expectedMessageCount(0)
    getMockEndpoint("mock:false").expectedMessageCount(1)
    template.sendBodyAndHeader("direct:if-else-boolean", None, "BooleanCondition", false)
    assertMockEndpointsSatisfied
  }

  // ---------------------------------------------
  // "direct:if-else-object" test
  // ---------------------------------------------
  @Test
  def objectConditionChoiceCallTrue = {
    getMockEndpoint("mock:found").expectedMessageCount(1)
    getMockEndpoint("mock:not-found").expectedMessageCount(0)
    template.sendBodyAndHeader("direct:if-else-object", None, "ConditionObject", "foo")
    assertMockEndpointsSatisfied
  }

  @Test
  def objectConditionChoiceCallFalse = {
    getMockEndpoint("mock:found").expectedMessageCount(0)
    getMockEndpoint("mock:not-found").expectedMessageCount(1)
    template.sendBody("direct:if-else-object", None)
    assertMockEndpointsSatisfied
  }

  // ---------------------------------------------
  // "direct:if-else-processor" test
  // ---------------------------------------------
  @Test
  def processorConditionChoiceCallTrue = {
    getMockEndpoint("mock:true").expectedMessageCount(1)
    getMockEndpoint("mock:false").expectedMessageCount(0)
    template.sendBodyAndHeader("direct:if-else-process", None, "Condition", "foo")
    assertMockEndpointsSatisfied
  }

  @Test
  def processorConditionChoiceCallFalse = {
    getMockEndpoint("mock:true").expectedMessageCount(0)
    getMockEndpoint("mock:false").expectedMessageCount(1)
    template.sendBodyAndHeader("direct:if-else-process", None, "Condition", "baz")
    assertMockEndpointsSatisfied
  }

  // ---------------------------------------------
  // "direct:loop-body" test
  // ---------------------------------------------
  @Test
  def bodyLoop = {
    val loopCount = 10
    getMockEndpoint("mock:loop").expectedMessageCount(loopCount)
    template.sendBody("direct:loop-body", loopCount)
    assertMockEndpointsSatisfied
  }

  @Test
  def seqSizeLoop = {
    val loopSeq = Seq(1, 2, 3, 4, 5)
    getMockEndpoint("mock:loop").expectedMessageCount(loopSeq.length)
    template.sendBodyAndHeader("direct:loop-seq-size", 10, "LoopObject", loopSeq)
    assertMockEndpointsSatisfied
  }

  @Test
  def doubleLoop = {
    val firstLoopCount = 3
    val secondLoopCount = 5
    var headers: mutable.HashMap[String, Integer] = mutable.HashMap[String, Integer](
      "FirstLoopCount" -> firstLoopCount,
      "SecondLoopCount" -> secondLoopCount)
    getMockEndpoint("mock:first-loop").expectedMessageCount(firstLoopCount)
    getMockEndpoint("mock:second-loop").expectedMessageCount(secondLoopCount * firstLoopCount)
    template.sendBodyAndHeaders("direct:double-loop", "", headers)
    assertMockEndpointsSatisfied
  }

  // ---------------------------------------------
  // "direct:attempt-handle" test
  // ---------------------------------------------
  @Test
  def illegalStateExceptionOccur: Unit = {
    getMockEndpoint("mock:result").expectedMessageCount(0)
    getMockEndpoint("mock:illegal-state-exception").expectedMessageCount(1)
    getMockEndpoint("mock:exception").expectedMessageCount(0)
    getMockEndpoint("mock:finally").expectedMessageCount(1)
    template.sendBodyAndHeader("direct:attempt-handle", None, "Cause", "IllegalState")
    assertMockEndpointsSatisfied
  }

  @Test
  def exceptionOccur: Unit = {
    getMockEndpoint("mock:result").expectedMessageCount(0)
    getMockEndpoint("mock:illegal-state-exception").expectedMessageCount(0)
    getMockEndpoint("mock:exception").expectedMessageCount(1)
    getMockEndpoint("mock:finally").expectedMessageCount(1)
    template.sendBodyAndHeader("direct:attempt-handle", None, "Cause", "Unknown")
    assertMockEndpointsSatisfied
  }

  @Test
  def exceptionNotOccur: Unit = {
    getMockEndpoint("mock:result").expectedMessageCount(1)
    getMockEndpoint("mock:illegal-state-exception").expectedMessageCount(0)
    getMockEndpoint("mock:exception").expectedMessageCount(0)
    getMockEndpoint("mock:finally").expectedMessageCount(1)
    template.sendBody("direct:attempt-handle", None)
    assertMockEndpointsSatisfied
  }
}

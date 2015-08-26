package jp.den3umegumi.experintal.java.camel.route;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;

import org.apache.camel.scala.dsl.builder.RouteBuilderSupport;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static org.apache.camel.component.mock.MockEndpoint.expectsMessageCount;

public class RoundRobinLoadBalanceTest extends CamelTestSupport {
    protected MockEndpoint x;
    protected MockEndpoint y;
    protected MockEndpoint z;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        x = getMockEndpoint("mock:x");
        y = getMockEndpoint("mock:y");
        z = getMockEndpoint("mock:z");
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                // START SNIPPET: example
                from("direct:start").loadBalance().
                        roundRobin().to("mock:x", "mock:y", "mock:z");
                // END SNIPPET: example
            }
        };
    }

    @Test
    public void testRoundRobin() throws Exception {
        String body = "<one/>";
        x.expectedBodiesReceived(body);
        expectsMessageCount(0, y, z);
        sendMessage("bar", body);
        assertMockEndpointsSatisfied();

        x.reset();

        body = "<two/>";
        y.expectedBodiesReceived(body);
        expectsMessageCount(0, x, z);
        sendMessage("bar", body);
        assertMockEndpointsSatisfied();

        y.reset();

        body = "<three/>";
        z.expectedBodiesReceived(body);
        expectsMessageCount(0, x, y);
        sendMessage("bar", body);
        assertMockEndpointsSatisfied();
    }

    protected void sendMessage(final Object headerValue, final Object body) throws Exception {
        template.sendBodyAndHeader("direct:start", body, "foo", headerValue);
    }
}
package agilabs;

import org.apache.camel.builder.RouteBuilder;

public class CamelRouteBuilder extends RouteBuilder {

    public void configure() {
        from("servlet:hello").setBody().constant("Hello from Camel Servlet");
    }
}
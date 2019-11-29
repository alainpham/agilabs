package itteddy;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.undertow.HttpHandlerRegistrationInfo;
import org.apache.camel.component.undertow.UndertowComponent;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.undertow.Handlers.resource;

public class AppRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AppRouteBuilder.class);


    public void configure() throws Exception{

        // adding beans

        restConfiguration()
		.component("undertow")
		.bindingMode(RestBindingMode.json)
		.contextPath("/")
		.port(getContext().resolvePropertyPlaceholders("{{server.port}}"))
		.apiContextPath("/api-docs")
		.apiProperty("cors", "true")
		.apiProperty("api.title", "test")
		.apiProperty("api.version", "1.0")
		.host(getContext().resolvePropertyPlaceholders("{{server.address}}"))
		.dataFormatProperty("prettyPrint", "true");

        // load camel xml routes
        InputStream routesIs = getClass().getResourceAsStream("/routes/camel-routes.xml");
        if (routesIs != null) {
            RoutesDefinition r = getContext().loadRoutesDefinition(routesIs);
            getContext().addRouteDefinitions(r.getRoutes());
        }

        // load camel rest file
        InputStream restsIs = getClass().getResourceAsStream("/rests/camel-rest.xml");
        if (restsIs != null) {
            RestsDefinition r = getContext().loadRestsDefinition(restsIs);
            getContext().addRestDefinitions(r.getRests());
            for (final RestDefinition restDefinition : r.getRests()) {
                List<RouteDefinition> routeDefinitions = restDefinition.asRouteDefinition(getContext());
                getContext().addRouteDefinitions(routeDefinitions);
            }
        }
    }

}

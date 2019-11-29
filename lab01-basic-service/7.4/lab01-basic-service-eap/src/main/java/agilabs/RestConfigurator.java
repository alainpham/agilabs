package agilabs;


import java.io.InputStream;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestConfigurator extends RouteBuilder {

	final Logger logger = LoggerFactory.getLogger(RestConfigurator.class);


	@Override
	public void configure() throws Exception {
		
		restConfiguration()
		.component("servlet")
		.bindingMode(RestBindingMode.auto)
		.contextPath("/lab01-basic-service-eap/camel")
		.port(getContext().resolvePropertyPlaceholders("{{sys:camelrest.port:8080}}"))
		.apiContextPath("/api-docs")
		.host(getContext().resolvePropertyPlaceholders("{{sys:camelrest.host:localhost}}"))
        .dataFormatProperty("prettyPrint", "true")
        .apiProperty("api.title", "lab01-basic-service-eap")
        .apiProperty("api.version", "1.0-SNAPSHOT")
        .apiProperty("cors", "true");
		
        InputStream is = getClass().getResourceAsStream("/camel-rest.xml");
        if (is != null) {
            RestsDefinition r = getContext().loadRestsDefinition(is);
            getContext().addRestDefinitions(r.getRests());
            for (final RestDefinition restDefinition : r.getRests()) {
                List<RouteDefinition> routeDefinitions = restDefinition.asRouteDefinition(getContext());
                getContext().addRouteDefinitions(routeDefinitions);
            }
        }
	}

}

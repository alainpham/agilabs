package agilabs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

@Path("/hello")
public class GreetingResource {

    // This is to test wiring the camel context and creating a producer template.
    @Inject
    CamelContext context;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return  context.createProducerTemplate().requestBody("direct:hello",null,String.class);
    }
}
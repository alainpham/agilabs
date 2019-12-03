package agilabs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/test")
public class Service {
    @Inject
    Producer producer;

    @GET
    @Path("/{msg}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam(value = "msg") String msg) {
        producer.produce(msg);
        return "ok";
    }
}
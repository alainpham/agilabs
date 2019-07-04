package agilabs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class Consumer {
    
    @Inject
    ProducerTemplate camelProducer;


    @Incoming("in-events")
    public void consume(String message) {              
        camelProducer.sendBody("direct:receive-events", message);
    }
}
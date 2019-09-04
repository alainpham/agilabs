package agilabs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

@ApplicationScoped
public class Producer {
    

    @Inject
    @Stream("out-events")
    Emitter<String> emitter;

    public void produce(String message) {
        emitter.send(message);  
        System.out.println("Sent " + message);     
    }
}
package agilabs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

@ApplicationScoped
@RegisterForReflection
public class Producer {
    
    @Inject
    @Stream("out-events")
    Emitter<String> emitter;

    public void produce(String message) {
        emitter.send(message);       
    }
}
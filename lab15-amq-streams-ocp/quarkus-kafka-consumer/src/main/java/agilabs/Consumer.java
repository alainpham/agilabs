package agilabs;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class Consumer {
    

    @Incoming("events")
    public void consume(String message) {              
        System.out.println("Received " + message);
    }
}
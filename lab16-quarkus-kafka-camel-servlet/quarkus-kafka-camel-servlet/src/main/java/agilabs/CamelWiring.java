package agilabs;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.camel.core.runtime.InitializedEvent;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

public class CamelWiring{

    Logger logger = LoggerFactory.getLogger(CamelWiring.class);

    @Inject
    Producer smallRyeProducer;

    @Inject
    CamelContext camelContext;

    public void registerSmallryeEmitter( @Observes InitializedEvent ev ) throws Exception {

        // if (emitter==null){
        //     logger.error("Wiring SmallRye Emitter to Camel Context failed !");
        //     throw new Exception("Wiring SmallRye Emitter to Camel Context failed !");
        // }

        camelContext.getRegistry().bind("smallrye-producer", smallRyeProducer);
        logger.info("Wiring SmallRye Emitter to camel context succeded.");
    }

    @Produces
    public ProducerTemplate buildCamelProducerTemplate() throws Exception {

        // if (camelContext==null){
        //     logger.error("Creating camel producer template failed!");
        //     throw new Exception("Creating camel producer template failed!");
        // }

        
        ProducerTemplate p = camelContext.createProducerTemplate();
        logger.info("Creating producer template succeded.");
        return p;
    }
}
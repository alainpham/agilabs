package agilabs;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static net.logstash.logback.argument.StructuredArguments.*;

/**
 * A sample transform
 */
@Component
public class EventLogger {
    private static final Logger logger = LoggerFactory.getLogger(EventLogger.class);

    public void logEvent(@Header(value = "breadcrumbId") String flowInstanceID, @Header(value ="state") String state, @Header(value = "elapsedMs") Integer executionTimeMs,@Body String msg) {
        logger.info(msg,
            value("flowInstanceID", flowInstanceID),
            value("state",state),
            value("executionTimeMs", executionTimeMs)
            );
	}

}

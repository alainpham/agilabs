package agilabs;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component(value="elapsedTimeProcessor")
public class ElapsedTimeProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Long elapsedMs = exchange.getIn().getHeader("endts", Long.class) - exchange.getIn().getHeader("startts", Long.class);
		exchange.getIn().setHeader("elapsedMs", elapsedMs);
	}

}

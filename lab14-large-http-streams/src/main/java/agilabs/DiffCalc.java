package agilabs;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component(value = "diffCalcProcessor")
public class DiffCalc implements Processor {

	@Override
	public void process(Exchange ex) throws Exception {
		Long start = ex.getIn().getHeader("start",Long.class);
		Long end = ex.getIn().getHeader("end",Long.class);
		Long size = ex.getIn().getHeader("Content-Length",Long.class);
		ex.getIn().setHeader("time", end-start);
		ex.getIn().setHeader("speed", size.doubleValue()/1024/1024/((end.doubleValue()-start.doubleValue())/1000));
	}

}

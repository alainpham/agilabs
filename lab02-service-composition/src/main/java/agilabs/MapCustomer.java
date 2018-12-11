package agilabs;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

import restsvc.model.Customer;

@Component(value = "mapCustomer")
public class MapCustomer implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		System.out.println(oldExchange.getIn().getBody());
		System.out.println(newExchange.getIn().getBody());
		
		remotesvc.model.Customer fromSvc = oldExchange.getIn().getBody(remotesvc.model.Customer.class);

		Customer fromDb = newExchange.getIn().getBody(Customer.class);
		
		//doing the mapping
		fromDb.setAge(fromSvc.getAge());
		fromDb.setAddress(fromSvc.getAddress());
		fromDb.setName(fromSvc.getName());
		
		return newExchange;
	}

}

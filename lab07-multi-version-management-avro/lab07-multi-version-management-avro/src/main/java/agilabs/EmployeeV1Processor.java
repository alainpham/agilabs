package agilabs;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import example.avro1.Employee;

@Component(value="employeeV1Processor")
public class EmployeeV1Processor implements Processor{
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		Employee e = new Employee(); 
		e.setAge(21);
		e.setName("John Doe");
		exchange.getIn().setBody(e);
	}
}

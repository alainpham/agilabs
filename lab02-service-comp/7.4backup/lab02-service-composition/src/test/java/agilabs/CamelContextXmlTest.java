package agilabs;


import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = Application.class,webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CamelContextXmlTest  {

	// TODO Create test message bodies that work for the route(s) being tested
	// Expected message bodies
	
	@Produce(uri = "http4:localhost:8090/camel/customer")	
	protected ProducerTemplate inputEndpoint;

	private String input = "wsebrook0@privacy.gov.au";
	
	
	@EndpointInject(uri = "mock:output")
	protected MockEndpoint outputEndpoint;
	
	@Autowired
	CamelContext context;

	@Test
	public void testCamelRoute() throws Exception {

		// Create routes from the output endpoints to our mock endpoints so we can assert expectations
		AdviceWithRouteBuilder interceptRestCall = new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				// mock the for testing
				interceptSendToEndpoint("http4:*").skipSendToOriginalEndpoint().setBody().constant("{\n" + 
						"  \"id\" : \"wsebrook0@privacy.gov.au\",\n" + 
						"  \"name\" : \"Wesley Sebrook\",\n" + 
						"  \"age\" : 36,\n" + 
						"  \"address\" : \"2 Portage Junction\"\n" + 
						"}");
				
			}
		};
		
		// Create routes from the output endpoints to our mock endpoints so we can assert expectations
		AdviceWithRouteBuilder interceptOutput = new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				// mock the for testing
				interceptSendToEndpoint("direct:logEndEvent").
					transform(simple("${body.getStatus()}")).
					log("${body}").
					to(outputEndpoint);
					
				
			} 
		};
		
		context.getRouteDefinition("callRestSvc").adviceWith(context, interceptRestCall);
		context.getRouteDefinition("getCustomerWithCBRoute").adviceWith(context, interceptOutput);
		
		// Define some expectations
		outputEndpoint.expectedBodiesReceived("diamond");
		//Run the test
		Object result =  inputEndpoint.requestBodyAndHeader(null, "id", input);
		System.out.println(result);
		// Validate our expectations
		outputEndpoint.assertIsSatisfied();
	}

}
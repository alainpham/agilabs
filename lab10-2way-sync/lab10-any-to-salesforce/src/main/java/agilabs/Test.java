package agilabs;

import org.apache.camel.CamelContext;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.api.dto.CreateSObjectResult;
import org.apache.camel.component.salesforce.api.dto.composite.SObjectBatch;
import org.apache.camel.component.salesforce.api.dto.composite.SObjectComposite;
import org.apache.camel.component.salesforce.internal.client.DefaultCompositeApiClient;
import org.apache.camel.salesforce.dto.Account;

public class Test {

	public static void main(String[] args) {
		Account a = null;
		SObjectComposite s = new SObjectComposite("38.0", true);
		DefaultCompositeApiClient d;
		CamelContext c;
		SalesforceComponent sf;
	}
}

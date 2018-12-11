package agilabs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.internal.SalesforceSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="prepareSfCall")
public class PrepareSfCall implements Processor{

    //Autowire camel context to get session
    @Autowired
    private CamelContext camelContext;

    @Override
    public void process(Exchange exchange) throws Exception {

        //#################################################
        //Get Salesfroce component to recuperate bearer token
        //#################################################
        SalesforceComponent sf = camelContext.getComponent("salesforce",SalesforceComponent.class);
        SalesforceSession session = sf.getSession();
        

        //#################################################
        //Prepare final call
        //#################################################
        Map<String,List<Object>> finalRequest = new HashMap<String,List<Object>>();
        
        finalRequest.put("compositeRequest", (List<Object>)exchange.getIn().getBody());
        
        exchange.getIn().setBody(finalRequest);

        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getIn().setHeader(Exchange.HTTP_URI, session.getInstanceUrl() + "/services/data/v38.0/composite");
        exchange.getIn().setHeader("Authorization", "Bearer " + session.getAccessToken());
    }
}
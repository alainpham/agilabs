package agilabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.component.salesforce.api.dto.composite.SObjectComposite;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.salesforce.dto.Account;
import org.springframework.stereotype.Component;

@Component(value = "aggregateUpserts")
public class AggregateUpserts implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        // SObjectComposite s=null;
        List<Map<String, Object>> sfRequests;

        // #################################################
        // first Initialize the request
        // #################################################
        if (oldExchange == null) {
            // s = new SObjectComposite("38.0", true);
            sfRequests = new ArrayList<Map<String, Object>>();
            oldExchange = newExchange;
        } else {
            // s = oldExchange.getIn().getBody(SObjectComposite.class);
            sfRequests = (List<Map<String, Object>>) oldExchange.getIn().getBody();

        }

        // #################################################
        // log ID and data
        // #################################################
        String becem__Inet_Id__c = newExchange.getIn().getHeader("becem__Inet_Id__c", String.class);
        System.out.println(becem__Inet_Id__c);
        System.out.println(newExchange.getIn().getBody());

        // s.addGeneric(SObjectComposite.Method.PATCH,url,
        // newExchange.getIn().getBody(),becem__Inet_Id__c);
        // s.addUpsertByExternalId("Account", "becem__Inet_Id__c", becem__Inet_Id__c,
        // newExchange.getIn().getBody(Account.class), becem__Inet_Id__c);

        // #################################################
        // prepare request for a record
        // #################################################
        Map<String, Object> request = new HashMap<String, Object>();

        request.put("method", "PATCH");
        request.put("url", "/services/data/v38.0/sobjects/Account/becem__Inet_Id__c/" + becem__Inet_Id__c);
        request.put("referenceId", UUID.randomUUID().toString());
        request.put("body", newExchange.getIn().getBody());

        // #################################################
        // add record to list of requests
        // #################################################
        sfRequests.add(request);
        oldExchange.getIn().setBody(sfRequests);

        return oldExchange;

    }

}
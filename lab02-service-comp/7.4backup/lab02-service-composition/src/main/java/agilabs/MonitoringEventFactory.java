package agilabs;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component(value="monitoringEventFactory")
public class MonitoringEventFactory {

	ObjectMapper mapper = new ObjectMapper();

	@Value("${camel.springboot.name}")
	private String contextName;
	

	public MonitoringEventFactory() {
		super();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}


	public String generateEvent(String flowInstanceID,String state, Integer executionTimeMs, String msg) throws Exception {
		Map<String,Object> event = new HashMap<String,Object>();
		event.put("flowInstanceID", flowInstanceID);
		event.put("eventProducer", contextName);
		event.put("timestamp",System.currentTimeMillis());
		event.put("state", state);
		event.put("executionTimeMs", executionTimeMs);
		event.put("msg", msg);
		
		String jsonInString = mapper.writeValueAsString(event);
		
		return jsonInString;
	}
}
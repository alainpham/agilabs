package agilabs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandeProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		List<Commande> l = exchange.getIn().getBody(List.class);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String,String>> newList = new ArrayList<>();
		for (Commande commande : l) {
			commande.setPrixTotal(new Float(Math.multiplyExact(commande.getPrixUnitaire().longValue(),commande.getQuantite().longValue())));
			
			
//			Map<String, Object> map = new HashMap<String,Object>();
			Map<String, String> properties = BeanUtils.describe(commande);
			// Convert POJO to Map
//			List<Field> fields = FieldUtils.getAllFieldsList(Commande.class);
//	        for(Field field: fields){
//	        	map.put(field.getName(), field.get(commande));
//	        }
	        
	        newList.add(properties);
		}
		
		exchange.getIn().setBody(newList);

	}

}

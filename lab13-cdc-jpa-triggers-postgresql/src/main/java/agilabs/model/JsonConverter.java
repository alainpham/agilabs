package agilabs.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public class JsonConverter  implements AttributeConverter<Object, String>{

	private ObjectMapper objectMapper = new ObjectMapper();

	
	@Override
	public String convertToDatabaseColumn(Object attribute) {
		// TODO Auto-generated method stub
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object convertToEntityAttribute(String dbData) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		try {

			return objectMapper.readValue(dbData, Object.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}

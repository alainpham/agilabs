package agilabs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import example.avro1.Employee;

public class TestV1V2 {

	public static void main(String[] args) throws IOException {
		
		Employee e = new Employee(); 
		e.setAge(21);
		e.setName("John Doe");
		e.setEmails(Arrays.asList("jd@mail.com","jdoe@yahoo.com"));
		

		
		System.out.println(e);
		DatumWriter<Employee> userDatumWriter = new SpecificDatumWriter<Employee>(Employee.class);
		DataFileWriter<Employee> dataFileWriter = new DataFileWriter<Employee>(userDatumWriter);
		dataFileWriter.create(e.getSchema(), new File("employees.avro"));
		dataFileWriter.append(e);
		dataFileWriter.close();
		      
		
		DatumReader<example.avro2.Employee> userDatumReader = new SpecificDatumReader<example.avro2.Employee>(example.avro2.Employee.class);
		DataFileReader<example.avro2.Employee> dataFileReader = new DataFileReader<example.avro2.Employee>(new File("employees.avro"), userDatumReader);
		example.avro2.Employee ev2 = null;
		while (dataFileReader.hasNext()) {
			ev2 = dataFileReader.next(ev2);
		System.out.println(ev2);
		}
	}

}

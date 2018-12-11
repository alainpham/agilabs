package agilabs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonV2 {

	
	private String firstName;
	private String lastName;
	private String middlename;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddlename() {
		return middlename;
	}
	@JsonProperty(required = true)
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	
	
	
}

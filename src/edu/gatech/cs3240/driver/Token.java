package edu.gatech.cs3240.driver;

public class Token {
	private String id;
	private String value;
	
	public Token(String id, String value){
		this.id = id;
		this.value = value;
	}
	
	public String getID(){
		return id;
	}
	
	public String getValue(){
		return value;
	}
}

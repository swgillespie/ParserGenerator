package edu.gatech.cs3240.parser;

public class Production {
	private String variable;
	private String rule;
	private boolean isStart;
	
	public Production (String v, String r, boolean s){
		variable = v;
		rule = r;
		isStart = s;
	}
	
	public String getVar(){
		return variable;
	}

	public String getRule(){
		return rule;
	}
	
	public boolean isStart(){
		return isStart;
	}
	
	@Override
	public String toString() {
		return "Production: " + variable + " -> " + rule;
	}
}

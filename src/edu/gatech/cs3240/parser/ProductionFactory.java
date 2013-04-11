package edu.gatech.cs3240.parser;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Reads the grammar and creates to data structures of the productions found
 * 1) An arraylist of non-terminal names
 * 2) A hash map. The key is the non-terminal name. 
 * The value is an arraylist of productions, where the left hand side of each production is the non-terminal
 */
public class ProductionFactory {
	private ArrayList<String> variables;
	private HashMap<String, ArrayList<Production>> productions;
	
	public ArrayList<String> getVariables(){
		return variables;
	}
	
	public HashMap<String, ArrayList<Production>> getProductions(){
		return productions;
	}
	
}

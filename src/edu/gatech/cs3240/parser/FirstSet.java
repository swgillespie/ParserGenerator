package edu.gatech.cs3240.parser;

import java.util.*;

public class FirstSet {
	
	//this class will create a hashtable of the first sets of all variables in the grammar
	//the keys will be the variables in the grammar and the values will be the first sets of terminals, which
	//will be stored as an arraylist of Charaters (note this is a wrapper class so to get char value have to do charValue()
	//which will return the char value of the Character)
	
	//nts: have check to not add same terminal twice to set
	
	
	private HashMap<String, ArrayList<Character>> firstSets; 
	private HashMap<String, ArrayList<Production>> prods;
	private ArrayList<Production> prodNames;
	
	public FirstSet(HashMap productions, ArrayList names){
		prods = productions;
		prodNames = names;
		
		
	}
	
	public void makeFirstSets(){
		//makes all the first sets and stores in firstSets hashmap
	}
	
	public HashMap getFirstSets(){
		return firstSets;
	}
	
	

}

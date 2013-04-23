package edu.gatech.cs3240.parser;

import java.util.*;

public class FirstSet {
	
	//this class will create a hashtable of the first sets of all variables in the grammar
	//the keys will be the variables in the grammar and the values will be the first sets of terminals, which
	//will be stored as an arraylist of Strings
	
	//nts: have check to not add same terminal twice to set
	
	
	private HashMap<String, ArrayList<String>> firstSets;
	private HashMap<String, ArrayList<Production>> prods;
	private ArrayList<String> vars;
	
	public FirstSet(HashMap<String, ArrayList<Production>> productions, ArrayList<String> names){
		prods = productions;
		vars = names;
	}
	
	private void makeFirstSets(){
		//makes all the first sets and stores in firstSets hashmap
	}
	
	public HashMap<String, ArrayList<String>> getFirstSets(){
		return firstSets;
	}
	
	//pass in a rule and check to see if the first thing in the rule is a terminal
	public String hasTermFirst(String rule){
		StringBuilder tempTerm = new StringBuilder();
		String term = null;
		if(rule.equals("<epsilon>")){
			term = "<empty>";
		}
		else{
			if(rule.charAt(0) != '<' || rule.charAt(1) != '<'){
				int i = 0;
				while(rule.charAt(i) != 32 && i<rule.length() && rule.charAt(i) != 12){ //while not equal to space or new line
					if(rule.charAt(i) != 32){ //don't add starting spaces to string
						tempTerm.append(rule.charAt(i));
					}
					i++;
				}
				term = tempTerm.toString();
			}
		}
		return term;
	}
	
	
	//pass rule into here, will return first terminal for each production
	public String getTerminal(String rule){
		StringBuilder tempRule = new StringBuilder();
		ArrayList<Production> rules;
		String term = hasTermFirst(rule);
		String newVar;
		boolean end = false;
		if(term != null){
			return term;
		}
		else{
			while(!end){//this will get the next var in rule
				for(int i = 0; i<rule.length(); i++){
					if(!tempRule.toString().endsWith(">")){ //check so only get the first non terminal in rule
						if(rule.charAt(i) != 62){
							tempRule.append(rule.charAt(i));
						}
						else if(rule.charAt(i) == 62){
							tempRule.append(rule.charAt(i));
						}
					}
				}
			}//end while 
			newVar = tempRule.toString();
			newVar = newVar.trim();
			rules = prods.get(newVar); 
			for(int i=0; i<rules.size(); i++){
				getTerminal(rules.get(i).getRule());
			}
		}
		return "";
	}
}

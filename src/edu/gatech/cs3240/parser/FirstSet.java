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
		makeFirstSets();
	}
	
	private void makeFirstSets(){
		for(int i=(vars.size()-1); i>=0;i--){                      							//go from bottom up through nonterm vars
			ArrayList<String> fs = new ArrayList<String>();
			String var = vars.get(i); 														//current variable getting the first set of
			ArrayList<Production> currProd = prods.get(var);								//rules of current variable
			if(currProd.get(0).isStart()){
				fs.add("$");
			}
			
			for(int k=0;k<currProd.size();k++){ 											//iterate over rules
				String rule = currProd.get(k).toString();
				rule = rule.trim();
				String term = hasTermFirst(rule);
				
				if(term != null){
					fs.add(term);
				}
				else{ 																		//if we have to add a whole first set
					boolean empty = true;
					int ind = 0;
					while(empty && ind<rule.length()){
						int check = 0;
						StringBuilder tempRule = new StringBuilder();
						for(int x = ind; x<rule.length(); x++){									//iterate through rule to get string 
							if(!tempRule.toString().endsWith(" ") || !tempRule.toString().endsWith(">")){ 							//check so only get the first non terminal in rule
								if(rule.charAt(x) != 62){
									tempRule.append(rule.charAt(x));
									check++;
								}
								else if(rule.charAt(x) == 62){
									tempRule.append(rule.charAt(x));
									check++;
								}
							}
						}
						String temp = tempRule.toString();
						if(temp.endsWith(" ")){
							temp = temp.trim();
							fs.add(temp);
							empty = false;
						}
						else{
							temp = temp.trim();
							ArrayList<String> varFS = firstSets.get(temp);
							
							for(int y=0;y<varFS.size();y++){
								if(!varFS.get(y).equals("<empty"))
									fs.add(varFS.get(y));
							}
							if(varFS.contains("<empty>")){
								if(check == (rule.length()-1)){
									empty = false;
									fs.add("<empty>");
								}
								else{
									ind = (check + 1);
								}
							}
							else{
								empty = false;
							}
						}
					}//end while
					if(empty)
						fs.add("<empty>");
				}//end else
			}//end for
		}//end main for
	}
	
	public HashMap<String, ArrayList<String>> getFirstSets(){
		return firstSets;
	}

	
	//pass in a rule and check to see if the first thing in the rule is a terminal
	private String hasTermFirst(String rule){
		StringBuilder tempTerm = new StringBuilder();
		String term = null;
		if(rule.equals("<epsilon>")){
			term = "<empty>";
		}
		else{
			if(rule.charAt(0) != '<'){
				int i = 0;
				while(rule.charAt(i) != 32 && i<rule.length() && rule.charAt(i) != 12){ //while not equal to space or new line
					if(rule.charAt(i) != 32){ //don't add starting spaces to string
						tempTerm.append(rule.charAt(i));
					}
					i++;
				}
				term = tempTerm.toString(); //make term a string
			}
		}
		return term;
	}
}

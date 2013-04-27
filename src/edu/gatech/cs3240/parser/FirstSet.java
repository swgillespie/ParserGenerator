package edu.gatech.cs3240.parser;

import java.util.*;

public class FirstSet {
	
	/*this class will create a hashtable of the first sets of all variables in the grammar
	the keys will be the variables in the grammar and the values will be the first sets of terminals, which
	will be stored as an arraylist of Strings
	
	nts: have check to not add same terminal twice to set*/
	
	
	private HashMap<String, ArrayList<String>> firstSets;
	private HashMap<String, ArrayList<Production>> prods;
	private ArrayList<String> vars;
	private String empty = "<empty>";
	
	public FirstSet(HashMap<String, ArrayList<Production>> productions, ArrayList<String> names){
		prods = productions;
		vars = names;
		firstSets = new HashMap<String, ArrayList<String>>();
		makeFirstSets();
	}
	
	/*builds the hashtable of first sets where the key is the nonterminal and the value is an arraylist that is the firstset for the nonterminal*/
	private void makeFirstSets(){
		for(int i=(vars.size()-1); i>=0;i--){                      							//go from bottom up through nonterm vars
			ArrayList<String> fs = new ArrayList<String>();
			String var = vars.get(i); 														//current variable getting the first set of
			ArrayList<Production> currProd = prods.get(var);								//rules of current variable
			if(currProd.get(0).isStart()){	
				fs.add("$");																//add $ to first set for starting var
			}
			
			for(int k=0;k<currProd.size();k++){ 											//iterate over rules
				String rule = currProd.get(k).getRule();
				rule = rule.trim();
				String term = hasTermFirst(rule);
				
				if(term != null){   														//add term to first set
					if(!fs.contains(term))
						fs.add(term);
				}
				else{ 																		//if have to add a whole first set
					boolean e = true;
					while(e && rule.length()>1){
						String temp = getVar(rule); 										//get the first variable in the rule
						temp = temp.trim();
						if(!temp.endsWith(">")){    										//if the first variable is a terminal, add to FD then break from while loop
							if(!fs.contains(term))
								fs.add(temp);
							e = false;
						}
						else{         														//if first variable is not a terminal
							temp = temp.substring(1, temp.length() - 1);
							ArrayList<String> varFS = firstSets.get(temp);
							for(int y=0;y<varFS.size();y++){   								//add all terminals except empty from var FS to curr FS
								if(!varFS.get(y).equals(empty))
									if(!fs.contains(term))
										fs.add(varFS.get(y));
							}
							if(varFS.contains(empty)){    									//add empty to FS if contains empty and is only var, or if all other vars contained empty
								if(temp.length() == rule.length()){
									e = false;
									if(!fs.contains(term))
										fs.add(empty);
								}
								else{
									int index = temp.length();
									rule = rule.substring(index).trim();
								}
							}
							else{
								e = false;
							}
						}
					}//end while
				}//end else
			}//end for
			var = var.trim();
			firstSets.put(var, fs);
		}//end main for
	}
	
	public HashMap<String, ArrayList<String>> getFirstSets(){
		return firstSets;
	}
	
	/*get the next variable from the rule*/
	private String getVar(String rule){
		StringBuilder temp = new StringBuilder();
		String var;
		if(rule.charAt(0) != '<'){
			int i = 0;
			while(rule.charAt(i) != 32 && i<rule.length() && rule.charAt(i) != 12){ 		//while not equal to space or new line
				if(rule.charAt(i) != 32){ 													//don't add starting spaces to string
					temp.append(rule.charAt(i));
				}
				i++;
			}
		}//end if
		else{
			int i = 0;
			while(!temp.toString().endsWith(">")){
				temp.append(rule.charAt(i));
				i++;
			}
		}//end else
		var = temp.toString();
		var = var.trim();
		return var;
	}

	
	/*pass in a rule and check to see if the first thing in the rule is a terminal*/
	private String hasTermFirst(String rule){
		StringBuilder tempTerm = new StringBuilder();
		String term = null;
		if(rule.equals("<epsilon>")){
			term = empty;
		}
		else{
			if(rule.charAt(0) != '<'){
				int i = 0;
				while(i<rule.length() && rule.charAt(i) != 32 && rule.charAt(i) != 12 && rule.charAt(i) != '<'){ 		//while not equal to space or new line
					//if(rule.charAt(i) != 32){ 																		//don't add starting spaces to string
						tempTerm.append(rule.charAt(i));
					i++;
				}
				term = tempTerm.toString(); 																			//make term a string
			}
		}
		return term;
	}
}

package edu.gatech.cs3240.lexer;

import java.util.ArrayList;


/*
 * Represents a NFA
 * NFAs can be created from character classes
 * NFAs can be modified using star, union, and concatenation
 */
public class NFA {
	int EMPTY = 127;
	public State start;
	public State accept;
		
	/*
	 * Make a NFA for a character class
	 */
	public NFA(CharClass charClass){
		accept = new State(true);
		start = new State(charClass.chars, accept);
	}
	
	/*
	 * Make a NFA for a single char
	 * Use for literals in the regex
	 */
	public NFA(char a){
		accept = new State(true);
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add((int)a);
		start = new State(list, accept);
	}
	
	/*
	 * Apply the star operator to the NFA
	 */
	public void star(){

	}
	
	/*
	 * Union this NFA with the given NFA
	 */
	public void union(NFA a){

	}
	
	/*
	 * Concatenate this NFA with the given NFA
	 */
	public void concat(NFA a){
		
	}
	
}


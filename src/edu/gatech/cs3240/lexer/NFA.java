package edu.gatech.cs3240.lexer;

import java.util.ArrayList;


/*
 * Represents a NFA
 * NFAs can be created from character classes
 * NFAs can be modified using star, union, and concatenation
 */
public class NFA {
	public State start;
	public State accept;
	public String type;
		
	/*
	 * Make a NFA for a character class
	 */
	public NFA(CharClass charClass, String type){
		this.type = type;
		accept = new State(true);
		accept.setType(type);
		start = new State(charClass.chars, accept);
		start.setType(type);
	}
	
	/*
	 * Make a NFA for a single char
	 * Use for literals in the regex
	 */
	public NFA(char a, String type){
		this.type = type;
		accept = new State(true);
		accept.setType(type);
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add((int)a);
		start = new State(list, accept);
		start.setType(type);
	}
	
	/*
	 * Make an empty NFA
	 */
	public NFA(String type){
		this.type = type;
	}
	public NFA(){
		type = "";
	}
	/*
	 * Apply the star operator to the NFA
	 */
	public void star(){
		//Link accept to start
		accept.onEmpty(start);
		//Create a new start state
		State oldStart = start;
		start = new State(false);
		start.setType(type);
		start.onEmpty(oldStart);
		//Create a new accept state
		State oldAccept = accept;
		oldAccept.setAccept(false);
		accept = new State(true);
		accept.setType(type);
		oldAccept.onEmpty(accept);
		//Link start and accept for the case of zero repetitions
		start.onEmpty(accept);
	}
	
	/*
	 * Union this NFA with the given NFA
	 */
	public void union(NFA a){
		//Create a new start state
		State oldStart = start;
		start = new State(false);
		start.setType(type);
		start.onEmpty(oldStart);
		start.onEmpty(a.start);
		//Create a new accept state
		State oldAccept = accept;
		oldAccept.setAccept(false);
		accept = new State(true);
		accept.setType(type);
		oldAccept.onEmpty(accept);
		a.accept.setAccept(false);
		a.accept.onEmpty(accept);
	}
	
	/*
	 * Concatenate this NFA with the given NFA
	 */
	public void concat(NFA a){
		accept.setAccept(false);
		accept.onEmpty(a.start);
		accept = a.accept;
	}
	
	
	/*
	 * Apply the operation to this NFA
	 */
	public void plus(){
		//Link accept to start
		accept.onEmpty(start);
	}
	
	public void combine(NFA a){
		//Create a new start state
				type = "";
				State oldStart = start;
				start = new State(false);
				start.setType(type);
				start.onEmpty(oldStart);
				start.onEmpty(a.start);
				
	}
}


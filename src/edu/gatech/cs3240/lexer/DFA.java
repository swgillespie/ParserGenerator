package edu.gatech.cs3240.lexer;

import java.util.*;

/*
 * Creates a DFA from an existing NFA
 * 
 * 
 * sidenote: apparently nothing i had tried to commit the last two days actually commited bc i was using the read only URI, oops
 */

public class DFA extends NFA{
	
	//public NFA nfa;
	public State start;
	public ArrayList<Integer> alphabet;
	public TreeSet<State> stateSet;
	
	public DFA(NFA from){
		this.start = from.start; 
		this.alphabet = from.alphabet;
		
		
	}
	
	public void create(NFA nfa){

		}
	
	/*
	 * Returns set of NFA states reachable from an NFA state on empty transitions
	 */
	
	public TreeSet<State> epClosure(State state){
		return stateSet;
	}
	
	/*
	 * Returns set of NFA states reachable from a set of states on empty transitions
	 */
	
	public TreeSet<State> epClosure(TreeSet<State> set){
		return stateSet;
	}
	
	/*
	 * Retrns a set of states to which there is a transition on char a from an NFA state in the input set, where the 
	 * input set represents a DFA state made from combined NFA states
	 */
	public TreeSet<State> transition(TreeSet<State> set, Integer c){
		return stateSet;
	}
	
	

}

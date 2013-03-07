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
	private ArrayList<State> emptyTrans;
	private ArrayList<State> stateSet;
	private ArrayList<State> transSet;
	private ArrayList<State> moveSet;
	
	
	public ArrayList<ArrayList<State>> dfa; //DFA represented as array list of array list where nested array list holds states
	
	public DFA(NFA from){
		this.start = from.start; 
		dfa = new ArrayList<ArrayList<State>>();
		makeAlpha(from);
	}
	
	/*
	 * creates alphabet of all possible transition chars for DFA conversion
	 */
	public void makeAlpha(NFA n){
		alphabet = new ArrayList<Integer>(); 
		for(int i=0; i<n.start.getTrans().length; i++){
			alphabet.add(i+32); //+32 representing chars ascii value
		}
	}
	
	public void create(NFA nfa){
		epClosure(nfa.start); // get all 
		}
	
	/*
	 * Returns set of NFA states reachable from an NFA state on empty transitions
	 * 
	 * state is any nfa state, return DFA state made from combined NFA states
	 */
	public ArrayList<State> epClosure(State state){
		Stack<State> stack = new Stack<State>();
		emptyTrans = new ArrayList<State>();
		emptyTrans.add(state);
		for(State s : state.getTrans()[95]){
			stack.push(s); // add states to stack
			emptyTrans.add(s);
		}
		while(!stack.isEmpty()){ //if the stack isn't empty
			State s = stack.pop();
			for(State st : s.getTrans()[95]){
				stack.push(st);
				emptyTrans.add(st);
			}
		}
		return emptyTrans;
	}

	/*
	 * Returns a DFA state represented as a set of NFA states reachable 
	 * from a set of states on empty transitions
	 */
	public ArrayList<State> epClosure(ArrayList<State> set){
		stateSet = new ArrayList<State>();
		for(State s : set){
			ArrayList<State> subSet = epClosure(s);
			for(State state : subSet){
				stateSet.add(state);
			}
		}
		return stateSet;
	}
	
	/*
	 * Returns a set of states to which there is a transition on char a from an NFA state in the input set, where the 
	 * input set represents a DFA state made from combined NFA states
	 */
	public ArrayList<State> transition(ArrayList<State> set, Integer c){
		//c = ascii value of char
		transSet = new ArrayList<State>();
		for(State s : set){
			ArrayList<State> subSet = move(s, c-32);
			for(State state : subSet){
				transSet.add(state);
			}
		}
		return transSet;
	}
	
	public ArrayList<State> move(State state, Integer i){
		//i = c-32
		Stack<State> stack = new Stack<State>();
		moveSet = new ArrayList<State>();
		moveSet.add(state);
		for(State s : state.getTrans()[i]){
			stack.push(s); // add states to stack
			moveSet.add(s);
		}
		while(!stack.isEmpty()){ //if the stack isn't empty
			State s = stack.pop();
			for(State st : s.getTrans()[i]){
				stack.push(st);
				moveSet.add(st);
			}
		}
		return moveSet;
	}
	
	
	
	

}

package edu.gatech.cs3240.lexer;

import java.util.*;
/*
 * Represents a single state in a NFA
 * Specifies if it is an accept state
 */
public class State{
	
	private final int EMPTY = 95;
	private boolean accept = false;
	public String type; 
	public boolean marked = false; 
	public boolean dead = false;
	
	/*
	 * Array of array lists that store states
	 * The ith array list contains states that are transitioned to on char = i+32
	 * The 96th array list contains states that are transitioned to on empty strings
	 */
	private ArrayList<State> transitions[] = new ArrayList[96];	
	
	/*
	 * Make a clean state with the specified accept value
	 */
	public State(boolean accept){
		for(int i = 0; i < transitions.length; i++ ){
			transitions[i] = new ArrayList<State>();
		}
		this.accept = accept;
	}
	
	/*
	 * Makes a state with transitions to the given state on the given chars
	 */
	public State(ArrayList<Integer> chars, State state){
		for(int i = 0; i < transitions.length; i++ ){
			transitions[i] = new ArrayList<State>();
		}
		for(int i :chars){
			transitions[i-32].add(state);
		}
	}
	
	/*
	 * Add a transition on the empty string to the given state
	 */
	public void onEmpty(State to){
		transitions[EMPTY].add(to);
	}
	
	public void setType(String t){
		type = t;
	}
	
	public ArrayList<State>[] getTrans(){
		return transitions; 
	}
	
	public void setMarked(boolean b){
		marked = b;
	}
	
	/*
	 * adds new transitions to a state after state is created
	 */
	public void setTrans(Integer charInt, State state){
		if(!transitions[charInt].isEmpty()){
			transitions[charInt].add(state);
		}
		else{
			transitions[charInt] = new ArrayList<State>();
			transitions[charInt].add(state);
		}
	}
	
	public LinkedList<State> getEpState(){
        LinkedList<State> emState = new LinkedList<State>();
        for(int i =0; i<transitions[95].size(); i++){
            emState.addLast(emState.get(i));
        }
        return emState;
    }
	
	/*
	 * Change the accept value
	 */
	public void setAccept(boolean accept){
		this.accept = accept;
	}
	
	public boolean getAccept(){
		return accept;
	}
	
	public void setDead(boolean d){
		dead = d;
	}
	
	/*
	 * Generate a list of all states reachable from this state on an empty transition
	 * Need to fix infinite loop problem
	 */
	public ArrayList<State> statesOnEmpty(){
		ArrayList<State> states = new ArrayList<State>();
		states.add(this);
		for(State s :transitions[EMPTY]){
			ArrayList<State> subList = s.statesOnEmpty();
			for(State sub: subList){
				if(!states.contains(sub)){
					states.add(sub);
				}
			}
		}
		return states;
	}
	
	public State match(char c){
		return transitions[c-32].get(0);
	}
}

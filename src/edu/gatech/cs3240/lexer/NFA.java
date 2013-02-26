package edu.gatech.cs3240.lexer;

import java.util.ArrayList;
import java.util.Set;

public class NFA {
	int start = 1;
	ArrayList<Integer> accept;
	ArrayList<State> states;
	
	NFA(){
		states.add(new State(1));
	}
	
	private class State{
		int name;
		ArrayList<Transition> transitions;
		State(int name){
			this.name = name;
			transitions = new ArrayList<Transition>();
		}
		
		void addTransition(char in, int state){
			Transition t = new Transition(in, state);
		}
		
	}
	
	private class Transition{
		char input;
		ArrayList<Integer> states;
		Transition(char in){
			input = in;
			states = new ArrayList<Integer>();
		}
		
		Transition(char in, int state){
			input = in;
			states = new ArrayList<Integer>();
			addState(state);
		}
		
		void addState(int state){
			states.add(state);
		}
		
		void remState(int state){
			states.remove(state);
		}
	}
}


package edu.gatech.cs3240.lexer;


import java.io.*;
import java.util.*;


/*
 * Creates a DFA from an existing NFA
 */

public class DFA extends NFA implements Serializable {
	

	private static final long serialVersionUID = 1L; // used for serialization
	
	public State startState;
	public ArrayList<Integer> alphabet;
	private ArrayList<State> emptyTrans;
	private ArrayList<State> currStateSet;
	private ArrayList<State> stateSet;
	private ArrayList<State> sSet;
	private ArrayList<State> transSet;
	private ArrayList<State> tSet;
	private State newState;
	private Hashtable<State, ArrayList<State>> pairs1; 
	private Hashtable<ArrayList<State>, State> pairs2;
	private boolean accept;
	private State currState;
	public State deadState;
	
	
	public ArrayList<State> dfa; //DFA represented as array list of array list where nested array list holds states
	
	
	
	public DFA(NFA from){
		dfa = new ArrayList<State>();
		pairs1 = new Hashtable<State,ArrayList<State>>();
		pairs2 = new Hashtable<ArrayList<State>, State>();
		accept = false;
		create(from);
		System.out.println("*****************dfa: "+dfa);
	}
	
	// BEGIN DFA SERIALIZATION CODE
	
	/**
	 * readDFAFromFile reads a DFA from a file and returns the constructed
	 * DFA object.
	 * @param filename The filename of the serialized DFA
	 * @return The reconstructed DFA object
	 */
	public static DFA readDFAFromFile(String filename) throws FileNotFoundException {
		FileInputStream fis;
		ObjectInputStream ois;
		try {
			fis = new FileInputStream(filename); // open filestream
			ois = new ObjectInputStream(fis);    // make filestream able to read objects
			DFA dfa_in = (DFA)ois.readObject();  // read an object from the file
			ois.close();                         // close the filestream
			return dfa_in;
		} catch (FileNotFoundException e) {
			throw e; // thrown when the filename doesn't exist
		} catch (IOException i) {
			// this is very bad and shouldn't happen
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			// this also should never happen
			c.printStackTrace();
		}
		return null; // should be unreachable, if this method ever returns null it means that
		// 1) either the object input stream got corrupted somehow or
		// 2) the DFA class doesn't know how to deserialize itself, which is unlikely since
		// this method is being defined inside of the DFA class.
		// either way neither of those things should ever happen, and if dfa ever equals
		// null then bad things are happening.
	}
	
	/**
	 * writeToFile serializes this DFA object to a file named filename.
	 * @param filename The name of the file to create
	 */
	public void writeToFile(String filename) {
		FileOutputStream fos;
		ObjectOutputStream oos;
		try {
			fos = new FileOutputStream(filename); // open filestream
			oos = new ObjectOutputStream(fos);    // make filestream able to write objects
			oos.writeObject(this);                // write this object to file
			oos.close();                          // close the filestream
		} catch (IOException i) {
			// this is very bad and shouldn't happen
			i.printStackTrace();
		}
	}
	
	
	public void create(NFA nfa){
		int count = 0;
		ArrayList<State> startStateSet = epClosure(nfa.start);
		for(State s : startStateSet){
			if(s.getAccept()){
				accept = true;
			}
		}
		State dfaStart = new State(accept);
		State deadState = new State(false);
		deadState.setDead(true);
		ArrayList<State> deadSet = new ArrayList<State>(); // this is an empty set
		pairs1.put(dfaStart, startStateSet);
		pairs2.put(startStateSet, dfaStart);
		
		pairs1.put(deadState, deadSet);
		pairs2.put(deadSet, deadState);
		dfa.add(dfaStart);
		
		for(State s : dfa){ //check if there are any unmarked states
			if(!s.marked){
				count++;
				currState = s;
				break;
			}
		}
		
		while(count > 0){
			accept = false;
			currState.setMarked(true);
			currStateSet = pairs1.get(currState);
			
			for(int c=0;c<96;c++){
				tSet = transition(currStateSet,c);
				if(tSet.isEmpty()){ //
					sSet = tSet; //considering null states
				}
				else{
					sSet = epClosure(tSet);
				}
			
				if(!pairs1.containsValue(sSet)){
					for(State s : sSet){
						if(s.getAccept()){
							accept = true; 
						}
					}
					newState = new State(accept);
					dfa.add(newState);
					pairs1.put(newState, sSet);
					pairs2.put(sSet, newState);
				}
				else{
					newState = pairs2.get(sSet);
				}
				
				currState.setTrans(c, newState);
			}
			
			count = 0;
			for(State s : dfa){ //check if there are any unmarked states
				if(!s.marked){
					count++;
					currState = s; //update currstate
					break;
				}
			}	
		}//end while
		dfa.add(deadState); // dead state will be the last 
		this.deadState = deadState;
	}//end create

	
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
		while(!stack.isEmpty()){
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
				if(!stateSet.contains(state)){
					stateSet.add(state);
				}
			}

		}
		return stateSet;
	}
	
	/*
	 * Returns a set of states to which there is a transition on char a from an NFA state in the input set, where the 
	 * input set represents a DFA state made from combined NFA states
	 */
	public ArrayList<State> transition(ArrayList<State> set, Integer c){
		transSet = new ArrayList<State>();
		for(State s : set){
			if(!s.getTrans()[c].isEmpty()){
				for(State state : s.getTrans()[c]){
					if(!transSet.contains(state)){ //prevent adding same state twice
						transSet.add(state);
					}
				}
			}
		}
		return transSet;
	}
	
	public ArrayList<State> getStateSet(State s){
		return pairs1.get(s);
	}
	
}

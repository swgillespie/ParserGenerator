package edu.gatech.cs3240.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import edu.gatech.cs3240.lexer.DFA;
import edu.gatech.cs3240.lexer.LexerException;
import edu.gatech.cs3240.lexer.State;

public class TableWalker {
	DFA table;
	protected static char EOF = 0;
	private Scanner scanner;
	private Stack<Character> unget_stack;
	ArrayList<String> ids = new ArrayList<String>();
	ArrayList<String> idsList = new ArrayList<String>();
	String value = "";
	char currentChar;
	State currentState;
	String lastVal;
	ArrayList<String> lastIds = new ArrayList<String>();
	
	public TableWalker(String fileName, DFA dfa) throws WalkerException{
		table = dfa;
		File inputFile = new File(fileName);
		if (!inputFile.exists()) {
			throw new WalkerException("File not found: " + fileName);
		}
		try {
			scanner = new Scanner(new FileInputStream(inputFile));
			scanner.useDelimiter("");
		} catch (FileNotFoundException f) {
			// this should never happen
		}
		unget_stack = new Stack<Character>();
	}
	
	protected char nextChar() {
		if (unget_stack.isEmpty()) {
			if (scanner.hasNext()) {
				return scanner.next().charAt(0);
			}
			return EOF;
		} else {
			return unget_stack.pop();
		}
	}
	
	protected char nextValidChar(){
		char c = nextChar();
		while(c!= 0 && c!= '\n' && (c<=' ' || c>'~')){
			c = nextChar();
		}
		return c;
	}
	
	protected void ungetChar(char s) {
		unget_stack.push(s);
	}
	
	public Token next() throws WalkerException{
		
		clear();
		initalize();
		if(currentChar == EOF){
			return null;X
		}
		if(currentState.getAccept()){
			acceptIds();
		}
		currentState = currentState.match(currentChar);
		while(currentState!=table.deadState){
			value+=currentChar;
			removeIds();
			if(currentState.getAccept()){
				lastVal = value;
				acceptIds();
			}
			currentChar = nextValidChar();
			if(currentChar == EOF || currentChar == '\n'){
				break;
			}
			currentState = currentState.match(currentChar);
		}
		if(currentState==table.deadState){
			ungetChar(currentChar);
		}
		if(lastIds.size()!=1){
			System.out.println(lastVal + lastIds.size()+": "+ lastIds);
			throw new WalkerException("Ambiguos REGEX specs");
		}
		return new Token(lastIds.get(0), lastVal);
		
	}
	
	private void clear(){
		//Clear Variables
		ids.clear();
		lastIds.clear();
		idsList.clear();
		value = "";
		lastVal = value;
	}
	
	private void initalize(){
		currentChar = nextValidChar();
		while(currentChar == '\n'){
			currentChar = nextValidChar();
		}
		currentState = table.dfa.get(0);
		for(State s: table.getStateSet(currentState)){
			if(!ids.contains(s.type)){
				ids.add(s.type);
			}
		}
		ids.remove("");
		//System.out.println(ids);
	}
	
	private void removeIds(){
		//Create a list of possible types at this state
		idsList = new ArrayList<String>();
		//System.out.println(ids + " - " + idsList);
		for(State s : table.getStateSet(currentState)){
			//System.out.println(s.type + (ids.contains(s.type)));
			if(!idsList.contains(s.type) && ids.contains(s.type)){
				idsList.add(s.type);
			}
		}
		//System.out.println(ids + " - " + idsList);
		ids = idsList;
		//System.out.println(ids + " - " + idsList);
	}
	private void acceptIds(){
		lastIds.clear();
		for(State s : table.getStateSet(currentState)){
			if(s.getAccept() && ids.contains(s.type)){
				lastIds.add(s.type);
			}
		}
		//System.out.println(lastIds);

	}
}

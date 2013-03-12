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
	
	protected void ungetChar(char s) {
		unget_stack.push(s);
	}
	
	public Token next() throws WalkerException{
		char currentChar = nextChar();
		State currentState = table.startState;
		ids = new ArrayList<String>();
		idsList = new ArrayList<String>();
		value = "";
		
		while(currentState!=null){
			currentState = currentState.match(currentChar);
			value+=currentChar;
		}
		value+=currentChar;
		if(ids.size()!=1){
			throw new WalkerException("Ambiguos REGEX specs");
		}
		return new Token(ids.get(0), value);
		
	}
	
	private void removeIds(State state){
		for(State s : table.getStateSet(currentState)){
			if(!idsList.contains(s.type)){
				idsList.add(s.type);
			}
		}
		for(String s : ids){
			if(!idsList.contains(s)){
				ids.remove(s);
			}
		}
	}
}

package edu.gatech.cs3240.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import edu.gatech.cs3240.driver.WalkerException;
import edu.gatech.cs3240.lexer.DFA;

/*
 * Reads the grammar and creates to data structures of the productions found
 * 1) An arraylist of non-terminal names
 * 2) A hash map. The key is the non-terminal name. 
 * The value is an arraylist of productions, where the left hand side of each production is the non-terminal
 */
public class ProductionFactory {
	private ArrayList<String> variables;
	private HashMap<String, ArrayList<Production>> productions;
	private Scanner scanner;
	private Stack<Character> unget_stack;
	protected static char EOF = 0;
	private char nextChar;
	
	
	public ArrayList<String> getVariables(){
		return variables;
	}
	
	public HashMap<String, ArrayList<Production>> getProductions(){
		return productions;
	}
	
	public ProductionFactory(String fileName) throws ParserException{
		productions = new HashMap<String, ArrayList<Production>>();
		variables = new ArrayList<String>();

		File inputFile = new File(fileName);
		if (!inputFile.exists()) {
			throw new ParserException("File not found: " + fileName);
		}
		try {
			scanner = new Scanner(new FileInputStream(inputFile));
			scanner.useDelimiter("");
		} catch (FileNotFoundException f) {
			// this should never happen
		}
		unget_stack = new Stack<Character>();
		
		start();
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
	
	private void start() throws ParserException{
		nextChar = nextValidChar();
		while(nextChar != EOF){
			readProduction();
		}
	}
	
	private void readProduction() throws ParserException{
		String variable = "";
		String rule = "";

		if(nextChar != '<'){
			throw new ParserException("Production does not start with a variable");
		}
		nextChar = nextValidChar();
		while(nextChar!='>'){
			variable += nextChar;
			nextChar = nextValidChar();
		}
		if(variable.length()<0){
			throw new ParserException("Empty variable");
		}
		
		if(!variables.contains(variable)){
			variables.add(variable);
		}
		nextChar = nextValidChar();

		if(nextChar != ':'){
			throw new ParserException("Production has missing or incorrect assignment operator");
		}
		nextChar = nextValidChar();
		if(nextChar != ':'){
			throw new ParserException("Production has missing or incorrect assignment operator");
		}
		nextChar = nextValidChar();
		if(nextChar != '='){
			throw new ParserException("Prodcution has missing or incorrect assignment operator");
		}
		nextChar = nextValidChar();
		while(nextChar!= '\n'){
			while(nextChar != '|' && nextChar != '\n'){
				rule += nextChar;
				nextChar = nextValidChar();
			}
			if(rule.length()<1){
				throw new ParserException("Empty productions");
			}
			if(productions.containsKey(variable)){
				productions.get(variable).add(new Production(variable, rule, variables.get(0)== variable));
			}
			else{
				ArrayList<Production> list = new ArrayList<Production>();
				list.add(new Production(variable, rule, variables.get(0) == variable));
				productions.put(variable, list );
			}
			if(nextChar =='|'){
				nextChar = nextValidChar();
			}
			
		}
		
		nextChar = nextValidChar();
		
	}
	
	
}

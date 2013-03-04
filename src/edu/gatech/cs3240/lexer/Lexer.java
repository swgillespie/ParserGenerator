package edu.gatech.cs3240.lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public abstract class Lexer {
	
	protected static char EOF = 0;
	private Scanner scanner;
	private Stack<Character> unget_stack;
	
	/**
	 * Constructs a new Lexer that lexes an input stream according
	 * to a context-free grammar.
	 * @param fileName The name of a file to lexify.
	 * @throws LexerException Thrown if fileName doesn't describe a 
	 * that exists
	 */
	public Lexer(String fileName) throws LexerException {
		File inputFile = new File(fileName);
		if (!inputFile.exists()) {
			throw new LexerException("File not found: " + fileName);
		}
		try {
			scanner = new Scanner(new FileInputStream(inputFile));
			scanner.useDelimiter("");
		} catch (FileNotFoundException f) {
			// this should never happen
		}
		unget_stack = new Stack<Character>();
	}
	
	/**
	 * Returns the next byte available to the input stream, and 0 if there are no
	 * more bytes.
	 * @return The next character in the input stream, 0 if there are no more characters
	 * available to the stream 
	 */
	protected char next() {
		if (unget_stack.isEmpty()) {
			if (scanner.hasNext()) {
				return scanner.next().charAt(0);
			}
			return EOF;
		} else {
			return unget_stack.pop();
		}
	}
	
	protected void unget(char s) {
		unget_stack.push(s);
	}
	
	/**
	 * Lexes an input according to an implementation-defined grammar. Throws LexerExceptions
	 * if syntax errors are encountered, and returns normally if no errors are found.
	 * @throws LexerException Thrown if a syntax error is encountered
	 */
	public abstract void parse() throws LexerException;
}

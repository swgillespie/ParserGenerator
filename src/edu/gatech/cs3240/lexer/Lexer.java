package edu.gatech.cs3240.lexer;

import java.io.*;
import java.util.Scanner;

public abstract class Lexer {
	
	
	private static final String EOF = "EOF";
	private Scanner scanner;
	
	/**
	 * Constructs a new GrammarLexer that tokenizes an input stream according
	 * to the context-free grammar given to us.
	 * @param fileName The name of a file to tokenize
	 * @throws LexerException Thrown if fileName doesn't describe a 
	 * valid file
	 */
	public Lexer(String fileName) throws LexerException {
		try {
			scanner = new Scanner(new FileInputStream(new File(fileName)));
		} catch (FileNotFoundException f) {
			throw new LexerException("File not found: " + fileName);
		}
	}
	
	protected String nextString() {
		if (scanner.hasNext()) {
			return scanner.next();
		}
		return EOF;
	}
	
	public abstract void parse();
}

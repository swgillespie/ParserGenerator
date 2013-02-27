package edu.gatech.cs3240.lexer;

import java.io.*;
import java.util.Scanner;

public abstract class Lexer {
	
	private static char EOF = 0;
	private Scanner scanner;
	
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
		} catch (FileNotFoundException f) {
			// this should never happen
		}
	}
	
	/**
	 * Returns the next byte available to the input stream, and 0 if there are no
	 * more bytes.
	 * @return The next character in the input stream, 0 if there are no more characters
	 * available to the stream 
	 */
	protected char next() {
		if (scanner.hasNextByte()) {
			return (char)scanner.nextByte();
		}
		return EOF;
	}
	
	/**
	 * Lexes an input according to an implementation-defined grammar. Throws LexerExceptions
	 * if syntax errors are encountered, and returns normally if no errors are found.
	 * @throws LexerException 
	 */
	public abstract void parse() throws LexerException;
}

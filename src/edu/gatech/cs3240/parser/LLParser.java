package edu.gatech.cs3240.parser;

import java.util.Stack;
import edu.gatech.cs3240.driver.*;
import edu.gatech.cs3240.lexer.DFA;
import edu.gatech.cs3240.lexer.LexerException;
import edu.gatech.cs3240.lexer.RegexLexer2;



public class LLParser {
	
	private ParseTableInterface parseTable;
	private TableWalker tokenGenerator;
	
	public LLParser(ParseTableInterface parseTable, String inputSpec, String inputToParse) throws LexerException {
		// initialize everything
		RegexLexer2 lexer = new RegexLexer2(inputSpec);
		lexer.parse();
		DFA dfa = lexer.output;
		tokenGenerator = new TableWalker(inputToParse, dfa);
		this.parseTable = parseTable;
	}
	
	public Token getNext() {
		return tokenGenerator.next();
	}
	
	public void parse() throws ParserException {
		Stack<String> parseStack = new Stack<String>();
		parseStack.push("$"); // push EOF onto stack
		parseStack.push("<" + parseTable.getStartRule().getVar() + ">"); // push start rule onto stack 
		while(!parseStack.isEmpty()) {
			String currentSym = parseStack.pop();
			Token nextToken = tokenGenerator.next();
			System.out.println("Current sym: " + currentSym + ", next token= " + nextToken.getID() + ": " + nextToken.getValue());
			if (nextToken == null) {
				// the TableWalker ran out of tokens
				if (currentSym != "$") // if EOF is not what is expected
					throw new ParserException("Syntax error: expected " + currentSym +", got End Of File");
				return; // otherwise, accept
			}
			if (!parseTable.isTerminal(currentSym)) {
				Production parseEntry = parseTable.getTableEntry(currentSym, nextToken.getID());
				//parseStack.pop();
				String[] splitProduction = parseEntry.getRule().split(" ");
				for (int i = 0; i < splitProduction.length; i++) {
					parseStack.push(splitProduction[(splitProduction.length - 1) - i]);
				}
				tokenGenerator.unnext(nextToken);
			} else { // currentSym is terminal
				if (!currentSym.equals(nextToken.getID())) { // if what we got is not what we expected
					throw new ParserException("Syntax error: expected " + currentSym + ", got " + nextToken.getID());
				}
				// otherwise, we're good. continue the parse
			}
		}
	}

}

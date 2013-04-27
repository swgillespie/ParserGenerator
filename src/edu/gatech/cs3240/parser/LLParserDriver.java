package edu.gatech.cs3240.parser;

import edu.gatech.cs3240.lexer.*;

public class LLParserDriver {
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: java -jar LLParser.jar input_spec input_grammar file_to_parse");
			System.exit(1);
		}
		try {
			String spec = args[0];
			String grammar = args[1];
			String fileToParse = args[2];
			System.out.println("Building parse table...");
			ParseTable parseTable = new ParseTable(grammar);
			LLParser parser = new LLParser(parseTable, spec, fileToParse);
			parser.parse();
			System.out.println("Parse completed successfully!");
		} catch (LexerException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
}

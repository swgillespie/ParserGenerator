package edu.gatech.cs3240.driver;

import edu.gatech.cs3240.lexer.DFA;
import edu.gatech.cs3240.lexer.RegexLexer2;

public class DFAGeneratorDriver {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java -jar DFAGenerator.jar inputfile outfile");
			System.exit(1);
		}
		try {
			String specIn = args[1];
			String out = args[2];
			RegexLexer2 lexer = new RegexLexer2(specIn);
			lexer.parse();
			DFA dfa = lexer.output;
			dfa.writeToFile(out);
		} catch (Exception e) {
			System.out.println("An error occured!");
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}

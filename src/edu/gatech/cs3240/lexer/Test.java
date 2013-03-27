package edu.gatech.cs3240.lexer;

import java.util.ArrayList;

import edu.gatech.cs3240.driver.TableWalker;
import edu.gatech.cs3240.driver.Token;
import edu.gatech.cs3240.driver.WalkerException;

public class Test {

	public static void main(String[] args) throws WalkerException{
		try{
			RegexLexer2 lexer = new RegexLexer2("test.txt");
			/*System.out.println(lexer.current_sym);
			lexer.accept(lexer.current_sym);
			System.out.println(lexer.current_sym);
			lexer.expect(lexer.current_sym, false);
			System.out.println(lexer.current_sym);
			lexer.accept(lexer.current_sym);			
			System.out.println(lexer.current_sym);
			lexer.accept(lexer.current_sym);
			
			ArrayList<String> list = lexer.parseCharClass();
			for(String s : list){
				System.out.println(s);
			}*/
			lexer.parse();
			DFA dfa = lexer.output;
			TableWalker t = new TableWalker("testfile", dfa);
			Token token = t.next();
			while(token!=null){
				System.out.println(token.getID() + ": '" +token.getValue()+"'");
				token = t.next();
			}
		}
		catch(LexerException e){
			System.out.println("ERROR");
			System.out.println(e.getMessage());
		}
	}
}

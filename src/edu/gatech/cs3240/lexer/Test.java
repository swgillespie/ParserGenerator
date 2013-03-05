package edu.gatech.cs3240.lexer;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args){
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
		}
		catch(LexerException e){
			System.out.println("ERROR");
			System.out.println(e.getMessage());
		}
	}
}

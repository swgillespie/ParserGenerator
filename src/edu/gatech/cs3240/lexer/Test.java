package edu.gatech.cs3240.lexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.gatech.cs3240.driver.TableWalker;
import edu.gatech.cs3240.driver.Token;
import edu.gatech.cs3240.driver.WalkerException;

public class Test {

	public static void main(String[] args) throws WalkerException, IOException{
		try{
			String specFile = "test.txt";
			String inputFile = "testfile";
			RegexLexer2 lexer = new RegexLexer2(specFile);
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
			TableWalker t = new TableWalker(inputFile, dfa);
			Token token = t.next();
			
			File f = new File(inputFile + "Output");
			
			if (!f.exists()) {
				f.createNewFile();
			}
 
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
 
			while(token!=null){
				String currToken = token.getID() + " " + token.getValue();
				System.out.println(currToken);
				bw.write(currToken);
				bw.newLine();
				token = t.next();
			}
			
			bw.close();
		}
		catch(LexerException e){
			System.out.println("ERROR");
			System.out.println(e.getMessage());
		}
	}
}

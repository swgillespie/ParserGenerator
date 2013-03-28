package edu.gatech.cs3240.driver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import edu.gatech.cs3240.lexer.DFA;

public class TableWalkerDriver {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: java -jar TableWalker.jar dfa_filename text_filename");
			System.exit(1);
		}
		try {
			DFA dfa = DFA.readDFAFromFile(args[1]);
			String inputFile = args[2];
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
			
		} catch (Exception e) {
			System.out.println("An error occured!");
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}

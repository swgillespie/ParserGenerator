package edu.gatech.cs3240.lexer;

import java.util.ArrayList;
import java.util.Set;

public class RegexLexer extends Lexer {

	private static final char L_PAREN = '(';
	private static final char R_PAREN = ')';
	private static final char L_BRACKET = '[';
	private static final char R_BRACKET = ']';
	private static final char STAR = '*';
	private static final char DOT = '.';
	private static final char CARAT = '^';
	private static final char PLUS = '+';
	private static final char UNION = '|';
	private static final char DASH = '-';
	private static final char SPACE = ' ';
	private static final char I = 'I';
	private static final char N = 'N';
	
	
	private char current_sym;
	
	public RegexLexer(String fileName) throws LexerException {
		super(fileName);
		current_sym = next();
	}
	
	private boolean accept() {
		current_sym = next();
		return true;
	}
	
	private boolean accept(char s) {
		if (current_sym == s) {
			current_sym = next();
			return true;
		} else {
			return false;
		}
	}
	
	private void expect(char s) throws LexerException {
		if (!accept(s)) {
			throw new LexerException("Syntax error: expected " + s + ", got " + current_sym);
		}
	}
	
	// BEGIN GRAMMAR DEFINITION
	
	public void parse() throws LexerException {
		regex();
	}
	
	private void regex() throws LexerException {
		rexp();
	}
	
	private void rexp() throws LexerException {
		rexp1();
		rexp_();
	}
	
	private void rexp_() throws LexerException {
		expect(UNION);
		rexp1();
		try {
			rexp_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp1() throws LexerException {
		rexp2();
		rexp1_();
	}
	
	private void rexp1_() throws LexerException {
		rexp2();
		try {
			rexp1_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp2() throws LexerException {
		try {
			expect(L_PAREN);
			rexp();
			expect(R_PAREN);
			return;
		} catch (LexerException e) {
			try {
				RE_CHAR();
				rexp2_tail();
				return;
			} catch (LexerException e1) {
				rexp3();
				return;
			}
		}
	}
	
	private void rexp2_tail() throws LexerException {
		try {
			expect(STAR);
			return;
		} catch (LexerException e) {
			try {
				expect(PLUS);
				return;
			} catch (LexerException e1) {
				return;
			}
		}
	}
	
	private void rexp3() throws LexerException {
		try {
			char_class();
			return;
		} catch (LexerException e) {
			return;
		}
	}
	
	private void char_class() throws LexerException {
		try {
			expect(DOT);
			return;
		} catch (LexerException e) {
			try {
				char_class1();
				return;
			} catch (LexerException e1) {
				defined_class();
				return;
			}
		}
	}
	
	private void char_class1() throws LexerException {
		try {
			char_set_list();
			return;
		} catch (LexerException e) {
			exclude_set();
			return;
		}
	}
	
	private void char_set_list() throws LexerException {
		try {
			char_set();
			char_set_list();
			return;
		} catch (LexerException e) {
			expect(L_BRACKET);
			return;
		}
	}
	
	private void char_set() throws LexerException {
		CLS_CHAR();
		char_set_tail();
	}
	
	private void char_set_tail() throws LexerException {
		try {
			expect(DASH);
			CLS_CHAR();
			return;
		} catch (LexerException e) {
			return;
		}
	}
	
	private void exclude_set() throws LexerException {
		expect(CARAT);
		char_set();
		expect(R_BRACKET);
		expect(SPACE);
		expect(I);
		expect(N);
		expect(SPACE);
		exclude_set_tail();
	}
	
	private void exclude_set_tail() throws LexerException {
		try {
			expect(L_BRACKET);
			char_set();
			expect(R_BRACKET);
		} catch (LexerException e) {
			defined_class();
		}
	}
	
	private void defined_class() throws LexerException {
		// TODO FIX THIS UP
		expect(I);
		expect(I);
		expect(I);
	}
	
	private void RE_CHAR() throws LexerException {
		// TODO
	}
	
	private void CLS_CHAR() throws LexerException {
		// TODO
	}

}

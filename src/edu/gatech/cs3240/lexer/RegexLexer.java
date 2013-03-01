package edu.gatech.cs3240.lexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

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
	
	private Set<Character> re_escape_chars;
	private Set<Character> re_chars;
	private Set<Character> cls_escape_chars;
	private Set<Character> cls_chars;
	private Set<Character> class_def_chars;
	
	private ArrayList<String> tokens;
	
	private static final Logger logger = Logger.getLogger(RegexLexer.class .getName());
	
	public RegexLexer(String fileName) throws LexerException {
		super(fileName);
		logger.addHandler(new ConsoleHandler());
		init();
		tokens = new ArrayList<String>();
		current_sym = next();
	}
	
	
	private void init() {
		logger.info("Entering method: " + "init");
		ArrayList<Character> class_def_chars = new ArrayList<Character>();
		for (char i = 65; i < 91; i++) {
			class_def_chars.add(i);
		}
		class_def_chars.add('-');
		this.class_def_chars = new HashSet<Character>(class_def_chars);
		ArrayList<Character> escape_chars = new ArrayList<Character>();
		escape_chars.add(' ');
		escape_chars.add('\\');
		escape_chars.add('*');
		escape_chars.add('+');
		escape_chars.add('?');
		escape_chars.add('|');
		escape_chars.add('[');
		escape_chars.add(']');
		escape_chars.add('(');
		escape_chars.add(')');
		escape_chars.add('.');
		escape_chars.add('\'');
		escape_chars.add('"');
		this.re_escape_chars = new HashSet<Character>(escape_chars);
		ArrayList<Character> re_chars = new ArrayList<Character>();
		for(char i = 32; i < 127; i++) {
			if (!this.re_escape_chars.contains(i)) {
				re_chars.add(i);
			}
		}
		this.re_chars = new HashSet<Character>(re_chars);
		
		ArrayList<Character> cls_escape_chars = new ArrayList<Character>();
		escape_chars.add(' ');
		escape_chars.add('\\');
		escape_chars.add('^');
		escape_chars.add('-');
		escape_chars.add('[');
		escape_chars.add(']');
		this.cls_escape_chars = new HashSet<Character>(cls_escape_chars);
		ArrayList<Character> cls_chars = new ArrayList<Character>();
		for(char i = 32; i < 127; i++) {
			if (!this.cls_escape_chars.contains(i)) {
				cls_chars.add(i);
			}
		}
		this.cls_chars = new HashSet<Character>(cls_chars);
	}
	
	private boolean accept(char s) {
		logger.info("Entering method: " + "accept");
		if (current_sym == s) {
			current_sym = next();
			tokens.add("" + s);
			while (current_sym == SPACE) {
				current_sym = next();
			}
			logger.info("Accepting char " + s + ": now: " + current_sym);
			return true;
		} else {
			logger.severe("Not accepting char: " + s + " got: " + current_sym);
			if (current_sym == 0) {
				logger.severe("CURRENT SYM IS ZERO");
				throw new EndOfFileException("Reached end of file!");
			}
			return false;
		}
	}
	
	private void expect(char s) throws LexerException {
		logger.info("Entering method: " + "expect");
		if (!accept(s)) {
			throw new LexerException("Syntax error: expected " + s + ", got " + current_sym);
		}
	}
	
	// BEGIN GRAMMAR DEFINITION
	
	public ArrayList<String> parse() throws LexerException {
		logger.info("Entering method: " + "parse");
		try {
			regex();
		} catch (LexerException e) {
			throw e;
		} catch (EndOfFileException e) {
			logger.info("parse received EndOfFileException, terminating");
		}
		return tokens;
	}
	
	private void regex() throws LexerException {
		logger.info("Entering method: " + "regex");
		rexp();
	}
	
	private void rexp() throws LexerException {
		logger.info("Entering method: " + "rexp");
		rexp1();
		rexp_();
	}
	
	private void rexp_() throws LexerException {
		logger.info("Entering method: " + "rexp_");
		expect(UNION);
		rexp1();
		try {
			rexp_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp1() throws LexerException {
		logger.info("Entering method: " + "rexp1");
		rexp2();
		rexp1_();
	}
	
	private void rexp1_() throws LexerException {
		logger.info("Entering method: " + "rexp1_");
		rexp2();
		try {
			rexp1_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp2() throws LexerException {
		logger.info("Entering method: " + "rexp2");
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
		logger.info("Entering method: " + "rexp2_tail");
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
		logger.info("Entering method: " + "rexp3");
		try {
			char_class();
			return;
		} catch (LexerException e) {
			return;
		}
	}
	
	private void char_class() throws LexerException {
		logger.info("Entering method: " + "char_class");
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
		logger.info("Entering method: " + "char_class1");
		try {
			char_set_list();
			return;
		} catch (LexerException e) {
			exclude_set();
			return;
		}
	}
	
	private void char_set_list() throws LexerException {
		logger.info("Entering method: " + "char_set_list");
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
		logger.info("Entering method: " + "char_set");
		CLS_CHAR();
		char_set_tail();
	}
	
	private void char_set_tail() throws LexerException {
		logger.info("Entering method: " + "char_set_tail");
		try {
			expect(DASH);
			CLS_CHAR();
			return;
		} catch (LexerException e) {
			return;
		}
	}
	
	private void exclude_set() throws LexerException {
		logger.info("Entering method: " + "exclude_set");
		expect(CARAT);
		char_set();
		expect(R_BRACKET);
		expect(I);
		expect(N);
		exclude_set_tail();
	}
	
	private void exclude_set_tail() throws LexerException {
		logger.info("Entering method: " + "exclude_set_tail");
		try {
			expect(L_BRACKET);
			char_set();
			expect(R_BRACKET);
		} catch (LexerException e) {
			defined_class();
		}
	}
	
	private void defined_class() throws LexerException {
		logger.info("Entering method: " + "defined_class");
		String class_name = "";
		do {
			class_name += current_sym;
			if (!this.class_def_chars.contains(current_sym)) {
				throw new LexerException("Invalid class identifier character: got " + current_sym);
			}
			current_sym = next();
		} while (current_sym != ' ');
		tokens.add(class_name);
	}
	
	private void RE_CHAR() throws LexerException {
		logger.info("Entering method: " + "RE_CHAR");
		if (current_sym == '\\') {
			current_sym = next();
			if (!this.re_escape_chars.contains(current_sym)) {
				throw new LexerException("Invalid escape character: got \\" + current_sym);
			}
		} else {
			if (!this.re_chars.contains(current_sym)) {
				throw new LexerException("Illegal character: got " + current_sym);
			}
		}
	}
	
	private void CLS_CHAR() throws LexerException {
		logger.info("Entering method: " + "CLS_CHAR");
		if (current_sym == '\\') {
			current_sym = next();
			if (!this.cls_escape_chars.contains(current_sym)) {
				throw new LexerException("Invalid escape character: got \\" + current_sym);
			}
		} else {
			if (!this.cls_chars.contains(current_sym)) {
				throw new LexerException("Illegal character: got " + current_sym);
			}
		}
	}
	
	private class EndOfFileException extends RuntimeException {
		
		public EndOfFileException(String message) {
			super(message);
		}
	}

}

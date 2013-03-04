package edu.gatech.cs3240.lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
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

	private static final char BACK_SLASH = '\\';
	private static final char NEW_LINE = '\n';
	private static final char DOLLAR ='$';

	private static final char NEWLINE = 10;
	
	
	private char current_sym;
	public Hashtable<String, CharClass> classTable = new Hashtable<String, CharClass>();
	public Hashtable<String, NFA> tokenTable = new Hashtable<String, NFA>();
	
	private Set<Character> re_escape_chars;
	private Set<Character> re_chars;
	private Set<Character> cls_escape_chars;
	private Set<Character> cls_chars;
	private Set<Character> class_def_chars;
	
	private ArrayList<String> tokens;
	private String tokenBuilder;
	
	private static final Logger logger = Logger.getLogger(RegexLexer.class .getName());
	
	public RegexLexer(String fileName) throws LexerException {
		super(fileName);
		init();
		tokens = new ArrayList<String>();
		current_sym = next();
		tokenBuilder = "";
	}
	
	
	
	private void init() {
		logger.info("Entering method, current_sym = " + current_sym);
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
		cls_escape_chars.add(' ');
		cls_escape_chars.add('\\');
		cls_escape_chars.add('^');
		cls_escape_chars.add('-');
		cls_escape_chars.add('[');
		cls_escape_chars.add(']');
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
		logger.info("Entering method, current_sym = " + current_sym + ", s = " + s);
		if (current_sym == s) {
			current_sym = next();
			while (current_sym == SPACE || current_sym == NEWLINE) {
				current_sym = next();
			}
			
			return true;
		} else {
			if (current_sym == EOF || current_sym == NEWLINE) {
				throw new EndOfFileException("Reached end of file!");
			}
			return false;
		}
	}
	
	private void expect(char s, boolean tokenAdd) throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		if (!accept(s)) {
			logger.info("Throwing LexerException: expected: " + s + ", got: " + current_sym);
			throw new LexerException("Syntax error: expected " + s + ", got " + current_sym);
		}
		if (tokenAdd) {
			tokens.add("" + s);
		}
	}
	//BEGIN INPUT GRAMMAR DEFINITION
	
	public void parseFile() throws LexerException{
		charClass();
		tokenDef();
	}
	
	public void charClass() throws LexerException{
		if(!accept(NEW_LINE)){
			classLine();
			charClass();
		};
	}
	
	public void classLine() throws LexerException{
		expect(DOLLAR, false);
		String className = "";
		while((current_sym>='A' & current_sym <='Z') | current_sym == '-'){
			className += current_sym;
			accept(current_sym);
		}
		CharClass charClass = toCharClass(parseCharClass());
		classTable.put(className, charClass);
	}
	
	public void tokenDef()throws LexerException{
		if(!accept(EOF)){
			tokenLine();
			tokenDef();
		}
	}
	
	public void tokenLine() throws LexerException{
		expect(DOLLAR, false);
		String tokenName = "";
		while((current_sym>='A' & current_sym <='Z') | current_sym == '-'){
			tokenName += current_sym;
			accept(current_sym);
		}
		NFA tokenNFA = toNFA(parse());
		tokenTable.put(tokenName, tokenNFA);
	}
	
	// BEGIN CHARCLASS GRAMMAR DEFINITION
	public ArrayList<String> parseCharClass(){
		return null;
	}
	
	public CharClass toCharClass(ArrayList<String> tokens){
		return null;
	}
	// BEGIN REGEX GRAMMAR DEFINITION
	
	public NFA toNFA(ArrayList<String> tokens){
		return null;
	}

	public ArrayList<String> parse() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			regex();
		} catch (LexerException e) {
			throw e;
		} catch (EndOfFileException e) {
			
		} catch (CriticalLexerException e) {
			throw new LexerException(e.getMessage());
		}
		return tokens;
	}
	
	private void regex() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		rexp();
	}
	
	private void rexp() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		rexp1();
		rexp_();
	}
	
	private void rexp_() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		expect(UNION, true);
		rexp1();
		try {
			rexp_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp1() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		rexp2();
		rexp1_();
	}
	
	private void rexp1_() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		rexp2();
		try {
			rexp1_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp2() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			expect(L_PAREN, true);
			rexp();
			expect(R_PAREN, true);
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
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			expect(STAR, true);
			return;
		} catch (LexerException e) {
			try {
				expect(PLUS, true);
				return;
			} catch (LexerException e1) {
				return;
			}
		}
	}
	
	private void rexp3() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			char_class();
			return;
		} catch (LexerException e) {
			return;
		}
	}
	
	private void char_class() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			expect(DOT, true);
			return;
		} catch (LexerException e) {
			try {
				expect(L_BRACKET, true);
				char_class1();
				return;
			} catch (LexerException e1) {
				defined_class();
				return;
			}
		}
	}
	
	private void char_class1() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			char_set_list();
			return;
		} catch (LexerException e) {
			exclude_set();
			return;
		}
	}
	
	private void char_set_list() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			char_set();
			char_set_list();
			return;
		} catch (LexerException e) { 
			expect(R_BRACKET, false);
			tokens.add(tokenBuilder);
			tokenBuilder = "";
			tokens.add("" + R_BRACKET);
			return;
		}
	}
	
	private void char_set() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		CLS_CHAR();
		char_set_tail();
	}
	
	private void char_set_tail() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			expect(DASH, false);
			tokenBuilder += '-';
			CLS_CHAR();
			return;
		} catch (LexerException e) {
			return;
		}
	}
	
	private void exclude_set() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		expect(CARAT, false);
		char_set();
		expect(R_BRACKET, true);
		expect(I, false);
		expect(N, false);
		exclude_set_tail();
	}
	
	private void exclude_set_tail() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		try {
			expect(L_BRACKET, true);
			char_set();
			expect(R_BRACKET, true);
		} catch (LexerException e) {
			defined_class();
		}
	}
	
	private void defined_class() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		String class_name = "";
		do {
			class_name += current_sym;
			if (!this.class_def_chars.contains(current_sym)) {
				logger.info("Throwing invalid class identifier: " + class_name);
				throw new CriticalLexerException("Invalid class identifier character: got " + current_sym);
			}
			current_sym = next();
		} while (current_sym != ' ');
		tokens.add(class_name);
	}
	
	private void RE_CHAR() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		if (current_sym == '\\') {
			current_sym = next();
			if (!this.re_escape_chars.contains(current_sym)) {
				logger.info("Throwing RE_CHAR exception: " + current_sym);
				unget(current_sym);
				current_sym = '\\';
				throw new LexerException("Invalid escape character: got \\" + current_sym);
			}
			tokens.add("" + '\\' + current_sym);
		} else {
			if (!this.re_chars.contains(current_sym)) {
				logger.info("Throwing RE_CHAR exception: " + current_sym);
				throw new LexerException("Illegal character: got " + current_sym);
			}
			tokens.add("" + current_sym);
		}
		current_sym = next();
	}
	
	private void CLS_CHAR() throws LexerException {
		logger.info("Entering method, current_sym = " + current_sym);
		if (current_sym == '\\') {
			current_sym = next();
			if (!this.cls_escape_chars.contains(current_sym)) {
				logger.info("Throwing CLS_CHAR exception: " + current_sym);
				unget(current_sym);
				current_sym = '\\';
				throw new LexerException("Invalid escape character: got \\" + current_sym);
			}
			tokenBuilder += '\\' + current_sym;
		} else {
			logger.info("current_sym = " + current_sym + " in CLS_CHAR: " + 
					this.cls_chars.contains(current_sym));
			if (!this.cls_chars.contains(current_sym)) {
				logger.info("Throwing CLS_CHAR exception: " + current_sym);
				throw new LexerException("Illegal character: got " + current_sym);
			}
			tokenBuilder += current_sym;
		}
		current_sym = next();
	}
	
	private class EndOfFileException extends RuntimeException {
		
		public EndOfFileException(String message) {
			super(message);
		}
	}
	
	private class CriticalLexerException extends RuntimeException {
		
		public CriticalLexerException(String message) {
			super(message);
		}
	}

}

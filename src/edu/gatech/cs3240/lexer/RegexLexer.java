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
	private static final char PERCENT = '%';

	
	public char current_sym;
	public Hashtable<String, CharClass> classTable = new Hashtable<String, CharClass>();
	public Hashtable<String, NFA> tokenTable = new Hashtable<String, NFA>();
	public ArrayList<String> tokenNames = new ArrayList<String>();
	
	//Character Sets
	private Set<Character> re_escape_chars;
	private Set<Character> re_chars;
	private Set<Character> cls_escape_chars;
	private Set<Character> cls_chars;
	private Set<Character> class_def_chars;
	
	//Tokens
	private ArrayList<String> tokens;
	private String tokenBuilder;
	
	//Logging
	private static final Logger logger = Logger.getLogger(RegexLexer.class .getName());
	
	public RegexLexer(String fileName) throws LexerException {
		super(fileName);
		init();
		tokens = new ArrayList<String>();
		current_sym = next();
		tokenBuilder = "";
	}
	
	/*
	 * Initial Character Sets
	 */
	private void init() {
		logger.info("Entering init, current_sym = " + current_sym);
		
		//Identifier Characters
		ArrayList<Character> class_def_chars = new ArrayList<Character>();
		for (char i = 'A'; i <= 'Z'; i++) {
			class_def_chars.add(i);
		}
		class_def_chars.add('-');
		this.class_def_chars = new HashSet<Character>(class_def_chars);
		
		//Regex Escaped Characters
		ArrayList<Character> escape_chars = new ArrayList<Character>();
		escape_chars.add(SPACE);
		escape_chars.add(BACK_SLASH);
		escape_chars.add(STAR);
		escape_chars.add(PLUS);
		escape_chars.add('?');
		escape_chars.add(UNION);
		escape_chars.add(L_BRACKET);
		escape_chars.add(R_BRACKET);
		escape_chars.add(L_PAREN);
		escape_chars.add(R_PAREN);
		escape_chars.add(DOT);
		escape_chars.add('\'');
		escape_chars.add('"');
		this.re_escape_chars = new HashSet<Character>(escape_chars);
		
		//Regex Characters
		ArrayList<Character> re_chars = new ArrayList<Character>();
		for(char i = ' '; i <= '~'; i++) {
			if (!this.re_escape_chars.contains(i)) {
				re_chars.add(i);
			}
		}
		this.re_chars = new HashSet<Character>(re_chars);
		
		//Class Escaped Characters
		ArrayList<Character> cls_escape_chars = new ArrayList<Character>();
		cls_escape_chars.add(SPACE);
		cls_escape_chars.add(BACK_SLASH);
		cls_escape_chars.add(CARAT);
		cls_escape_chars.add('-');
		cls_escape_chars.add(L_BRACKET);
		cls_escape_chars.add(R_BRACKET);
		this.cls_escape_chars = new HashSet<Character>(cls_escape_chars);
		
		//Class Characters
		ArrayList<Character> cls_chars = new ArrayList<Character>();
		for(char i = ' '; i <= '~'; i++) {
			if (!this.cls_escape_chars.contains(i)) {
				cls_chars.add(i);
			}
		}
		this.cls_chars = new HashSet<Character>(cls_chars);
	}
	
	/*
	 * Check if the given character matches. If it does, fetch the next non-white space character and return true.
	 */
	public boolean accept(char s) {
		logger.info("Entering accept, current_sym = " + current_sym + ", s = " + s);
		if (current_sym == s) {
			current_sym = next();
			while (current_sym == SPACE || current_sym == NEW_LINE) {
				current_sym = next();
			}
			return true;
		} else {
			if (current_sym == EOF || current_sym == NEW_LINE) {
				//throw new EndOfFileException("Reached end of file!");
			}
			return false;
		}
	}
	
	private boolean accept(){
		logger.info("Entering accept, current_sym = " + current_sym);
		current_sym = next();
		while (current_sym == SPACE || current_sym == NEW_LINE) {
			current_sym = next();
		}
		return true;
	}
	/*
	 * Check if the given character matches. If it does, conditionally add it to the token list
	 * Otherwise throw an exception
	 */
	public void expect(char s, boolean tokenAdd) throws LexerException {
		logger.info("Entering expect, current_sym = " + current_sym);
		if (!accept(s)) {
			logger.info("Throwing LexerException: expected: " + s + ", got: " + current_sym + '.');
			throw new LexerException("Syntax error: expected " + s + ", got " + current_sym + '.');
		}
		if (tokenAdd) {
			tokens.add("" + s);
		}
	}
	//BEGIN INPUT GRAMMAR DEFINITION
	
	/*
	 * Parse through the entire file, building the final DFA
	 */
	public void parse() throws LexerException{
		charClass();
		//tokenDef();
		//NFA combined = combineNFAs();
		//DFA finalDFA = new DFA(combined);
	}
	
	/*
	 * Combine all the NFAs in the hash table into one large NFA
	 */
	public NFA combineNFAs() throws LexerException{
		NFA combined = new NFA(' ', "");
		if(tokenNames.isEmpty()){
			throw new LexerException("No tokes defined");
		}
		else{
			combined = tokenTable.get(tokenNames.get(0));
			for(int i=1; i<tokenNames.size(); i++){
				combined.concat(tokenTable.get(tokenNames.get(i)));
			}
		}
		return combined;
	}
	
	/*
	 * Parse the character class section of the input file
	 * Section ends when '%%' is found
	 */
	public void charClass() throws LexerException{
		logger.info("Entering charClass, current_sym = " + current_sym);
		if(!accept(PERCENT)){
			classLine();
			charClass();
		}
		else{
			expect(PERCENT, false);
		}
	}
	
	/*
	 * Parse a single character class line
	 * Stores the class name and the charClass in the classTable
	 */
	public void classLine() throws LexerException{
		//All class names start with $
		expect(DOLLAR, false);
		String className = "";
		while((current_sym>='A' & current_sym <='Z') | current_sym == '-'){
			className += current_sym;
			current_sym = next();
		}
		//Put the current_sym back on the stack and then accept to skip whitespace
		unget(current_sym);
		accept();
		ArrayList<String> list = parseCharClass();
		for(String s : list){
			System.out.println(s);
		}
		//CharClass charClass = toCharClass(parseCharClass());
		//classTable.put(className, charClass);
	}
	
	/*
	 * Parse the token definition section of the input file
	 * Needs to be modified to tell when this section ends!!!
	 * EOF no longer works!!!
	 */
	public void tokenDef()throws LexerException{
		if(!accept(EOF)){
			tokenLine();
			tokenDef();
		}
	}
	
	/*
	 * Parse a single token defintion line
	 * Stores the token name and the NFA in the tokenTable
	 */
	public void tokenLine() throws LexerException{
		expect(DOLLAR, false);
		String tokenName = "";
		while((current_sym>='A' & current_sym <='Z') | current_sym == '-'){
			tokenName += current_sym;
			accept(current_sym);
		}
		NFA tokenNFA = toNFA(parseToken());
		tokenNames.add(tokenName);
		tokenTable.put(tokenName, tokenNFA);
	}
	
	// BEGIN CHARCLASS GRAMMAR DEFINITION
	/*
	 * Create a list of tokens for the charClass
	 */
	public ArrayList<String> parseCharClass() throws LexerException{
		tokens = new ArrayList<String>();
		char_class();
		return tokens;
	}
	
	/*
	 * Convert the list of tokens into a CharClass object
	 */
	public CharClass toCharClass(ArrayList<String> tokens){
		return null;
	}
	
	// BEGIN REGEX GRAMMAR DEFINITION
	
	/*
	 * Convert the list of tokens into a NFA
	 */
	public NFA toNFA(ArrayList<String> tokens){
		return null;
	}

	/*
	 * Create a list of tokens for the regex
	 */
	public ArrayList<String> parseToken() throws LexerException {
		logger.info("Entering parseToken, current_sym = " + current_sym);
		
		tokens = new ArrayList<String>();
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
		logger.info("Entering regex, current_sym = " + current_sym);
		rexp();
	}
	
	private void rexp() throws LexerException {
		logger.info("Entering rexp, current_sym = " + current_sym);
		rexp1();
		rexp_();
	}
	
	private void rexp_() throws LexerException {
		logger.info("Entering rexp_, current_sym = " + current_sym);
		expect(UNION, true);
		rexp1();
		try {
			rexp_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp1() throws LexerException {
		logger.info("Entering rexp1, current_sym = " + current_sym);
		rexp2();
		rexp1_();
	}
	
	private void rexp1_() throws LexerException {
		logger.info("Entering rexp1_, current_sym = " + current_sym);
		rexp2();
		try {
			rexp1_();
		} catch (LexerException e) {
			return;
		}
	}
	
	private void rexp2() throws LexerException {
		logger.info("Entering rexp2, current_sym = " + current_sym);
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
		logger.info("Entering rexp2_tail, current_sym = " + current_sym);
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
		logger.info("Entering rexp3, current_sym = " + current_sym);
		try {
			char_class();
			return;
		} catch (LexerException e) {
			return;
		}
	}
	
	private void char_class() throws LexerException {
		logger.info("Entering char_class, current_sym = " + current_sym);
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
		logger.info("Entering char_class1, current_sym = " + current_sym);
		try {
			char_set_list();
			return;
		} catch (LexerException e) {
			exclude_set();
			return;
		}
	}
	
	private void char_set_list() throws LexerException {
		logger.info("Entering char_set_list, current_sym = " + current_sym);
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
		logger.info("Entering char_set, current_sym = " + current_sym);
		CLS_CHAR();
		char_set_tail();
	}
	
	private void char_set_tail() throws LexerException {
		logger.info("Entering char_set_tail, current_sym = " + current_sym);
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
		logger.info("Entering exclude_set, current_sym = " + current_sym);
		expect(CARAT, false);
		char_set();
		expect(R_BRACKET, true);
		expect(I, false);
		expect(N, false);
		exclude_set_tail();
	}
	
	private void exclude_set_tail() throws LexerException {
		logger.info("Entering exclude_set_tail, current_sym = " + current_sym);
		try {
			expect(L_BRACKET, true);
			char_set();
			expect(R_BRACKET, true);
		} catch (LexerException e) {
			defined_class();
		}
	}
	
	private void defined_class() throws LexerException {
		logger.info("Entering defined_class, current_sym = " + current_sym);
		String class_name = "";
		//defined class names start with a $
		expect(DOLLAR, true);
		//keep adding characters until one is invalid
		while(this.class_def_chars.contains(current_sym)){
			class_name += current_sym;
			current_sym = next();
		}
		//In order to make sure newlines and spaces are removed put the current symbol back onto the stack
		//And re-accept
		unget(current_sym);
		accept();
		if(class_name.length()<1){
			logger.info("Throwing invalid class identifier: empty string");
			throw new CriticalLexerException("Invalid class identifier character: empty string");
		}
		tokens.add(class_name);
	}
	
	private void RE_CHAR() throws LexerException {
		logger.info("Entering RE_CHAR, current_sym = " + current_sym);
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
		logger.info("Entering CLS_CHAR, current_sym = " + current_sym);
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

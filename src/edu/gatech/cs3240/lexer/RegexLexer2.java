package edu.gatech.cs3240.lexer;

import java.util.ArrayList;
import java.util.Hashtable;

public class RegexLexer2 extends Lexer{
	private static final boolean NO_SPACES = true;
	private static final boolean SPACES = false;
	private static final char RETURN = 10;
	private static final char EOF = 0;
	private static final char CHAR_HI = '~';
	private static final char CHAR_LOW = ' ';
	private ArrayList<Character> ID_CHARS;
	private ArrayList<Character> CLASS_CHARS;
	private ArrayList<Character> CLASS_ESC_CHARS;
	private ArrayList<Character> TOKEN_CHARS;
	private ArrayList<Character> TOKEN_ESC_CHARS;
	
	
	private char current_sym;
	private String current_type;
	private ArrayList<String> classNames = new ArrayList<String>();
	public Hashtable<String, CharClass> classTable = new Hashtable<String, CharClass>();
	private ArrayList<String> tokenNames = new ArrayList<String>();
	public Hashtable<String, NFA> tokenTable = new Hashtable<String, NFA>();
	private int inParen = 0;
	public DFA output;
	
	public RegexLexer2(String filename) throws LexerException{
		super(filename);
		initialize();
		nextSym(NO_SPACES);
	}
	
	public void initialize(){
		ID_CHARS = new ArrayList<Character>();
		for(char i = 'A'; i<= 'Z'; i++){
			ID_CHARS.add(i);
		}
		ID_CHARS.add('-');
		
		CLASS_ESC_CHARS = new ArrayList<Character>();
		CLASS_ESC_CHARS.add('\\');
		CLASS_ESC_CHARS.add('^');
		CLASS_ESC_CHARS.add('-');
		CLASS_ESC_CHARS.add('[');
		CLASS_ESC_CHARS.add(']');

		CLASS_CHARS = new ArrayList<Character>();
		for(char i = CHAR_LOW; i<= CHAR_HI; i++){
			if(!CLASS_ESC_CHARS.contains(i)){
				CLASS_CHARS.add(i);
			}
		}
		
		TOKEN_ESC_CHARS = new ArrayList<Character>();
		TOKEN_ESC_CHARS.add(' ');
		TOKEN_ESC_CHARS.add('\\');
		TOKEN_ESC_CHARS.add('*');
		TOKEN_ESC_CHARS.add('+');
		TOKEN_ESC_CHARS.add('?');
		TOKEN_ESC_CHARS.add('|');
		TOKEN_ESC_CHARS.add('[');
		TOKEN_ESC_CHARS.add(']');
		TOKEN_ESC_CHARS.add('(');
		TOKEN_ESC_CHARS.add(')');
		TOKEN_ESC_CHARS.add('.');
		TOKEN_ESC_CHARS.add('\'');
		TOKEN_ESC_CHARS.add('"');
		TOKEN_ESC_CHARS.add('$');
		
		TOKEN_CHARS = new ArrayList<Character>();
		for(char i = CHAR_LOW; i<= CHAR_HI; i++){
			if(!TOKEN_ESC_CHARS.contains(i)){
				TOKEN_CHARS.add(i);
			}
		}
	}
	
	public void nextSym(boolean spaces){
		current_sym = next();
		if(spaces == NO_SPACES){
			while(Character.isWhitespace(current_sym) && current_sym != 10){
				current_sym = next();
			}
		} else {
			while(Character.isWhitespace(current_sym) && current_sym != 32 && current_sym != 10) {
				current_sym = next();
			}
		}
		System.out.print(current_sym);
	}
	
	public boolean accept(char s, boolean spaces){
		if(current_sym ==s){
			nextSym(spaces);
			return true;
		}
		else
			return false;
	}
	
	public void parse() throws LexerException{
		
		classSection();
		tokenSection();
		NFA finalNFA = combineNFAs();
		output = new DFA(finalNFA);
		
	}
	
	public void classSection() throws LexerException{
		
		while(!accept('%', SPACES)){
			classLine();
		}
		accept('%', NO_SPACES);	
		accept(RETURN, NO_SPACES);
	}
	
	public void tokenSection() throws LexerException{
		while(!accept(EOF, SPACES)){
			tokenLine();
		}
	}
	
	public void classLine() throws LexerException{
		if(accept('$', SPACES)){
			String className = "";
			while(ID_CHARS.contains(current_sym)){
				className += current_sym;
				nextSym(SPACES);
			}
			if(current_sym==' '){
				nextSym(NO_SPACES);
			}
			if(className.length()<1){
				throw new LexerException("Class name is empty");
			}
			classNames.add(className);
			CharClass charClass  = classRegex();
			classTable.put(className, charClass);
			charClass.print();
			accept(RETURN, NO_SPACES);
		}
		else{
			System.out.println("symbol: " + (int)current_sym);
			throw new LexerException("Class Line started with invalid characeter: " + current_sym);
		}
	}
	
	public void tokenLine() throws LexerException{
		if(accept('$', SPACES)){
			String tokenName = "";
			while(ID_CHARS.contains(current_sym)){
				tokenName += current_sym;
				nextSym(SPACES);
			}
			if(current_sym==' '){
				nextSym(NO_SPACES);
			}
			if(tokenName.length()<1){
				throw new LexerException("Token name is empty");
			}
			tokenNames.add(tokenName);
			current_type = tokenName;
			NFA tokenNFA = tokenRegex();
			tokenTable.put(tokenName, tokenNFA);
			accept(RETURN, NO_SPACES);
		}
		else{
			System.out.println("symbol: " + (int)current_sym);
			throw new LexerException("Token Line started with invalid characeter: " + current_sym);
		}
	}
	
	public CharClass classRegex() throws LexerException{ 
		CharClass charClass = new CharClass();
		if(accept('.', NO_SPACES)){
			charClass.addRange(CHAR_LOW, CHAR_HI);
		}
		else if(accept('[',SPACES)){
			if(accept('^',SPACES)){
				charClass = classSetExcluded(charClass);
			}
			else{
				classSet(charClass);
			}
				
		}
		else{
			System.out.println("symbol: " + (int)current_sym);
			throw new LexerException("Class regex started with invalid characeter: " + current_sym);
		}
		accept(RETURN, NO_SPACES);
		return charClass;
	}
	
	public void classSet(CharClass charClass) throws LexerException{
		ArrayList<Character> charList = new ArrayList<Character>();
		ArrayList<Integer> rangeList = new ArrayList<Integer>();
		while(current_sym != ']'){
			if(current_sym == '-'){
				rangeList.add(charList.size()-1);
			}
			else{
				charList.add(getClassChar());
			}
			nextSym(SPACES);
		}
		for(int i = 0; i<charList.size(); i++){
			if(rangeList.contains(i) && i < charList.size()-1){
				//if index i is marked as the start of a range and is not the last entry in the list add the range
				if(!charClass.addRange(charList.get(i),charList.get(i+1))){
					throw new LexerException("Invalid character range: empty");
				}
				rangeList.remove(rangeList.indexOf(i));
				i++;
			}
			else if(!rangeList.contains(i)){
				//if index i is not marked as the start of a range add the char
				charClass.addChar(charList.get(i));
			}
			else{
				throw new LexerException("Invalid character range: no ending character");
			}
		}
		if(!rangeList.isEmpty()){
			throw new LexerException("Invalid character range: no starting character");
		}
		accept(']',NO_SPACES);
	}
	
	public CharClass classSetExcluded(CharClass charClass) throws LexerException{
		CharClass newClass = new CharClass();
		classSet(charClass);
		if(!accept('I', SPACES)){
			throw new LexerException("Invalid exclude class: No 'I'");
		}
		if(!accept('N', NO_SPACES)){
			throw new LexerException("Invalid exclude class: No 'N'");
		}
		if(accept('$', SPACES)){
			String className = "";
			while(ID_CHARS.contains(current_sym)){
				className += current_sym;
				nextSym(SPACES);
			}
			if(current_sym==' '){
				nextSym(NO_SPACES);
			}
			if(classNames.contains(className)){
				CharClass baseClass = classTable.get(className);
				newClass = new CharClass(charClass, baseClass);
			}
			else{
				throw new LexerException("Invalid exclude class: Non-existant ref class name");
			}
		}
		else{
			throw new LexerException("Invalid exclude class: Invalid ref class name");
		}
		return newClass;	
	}
	
	public char getClassChar() throws LexerException{
		if(accept('\\',SPACES)){
			if(CLASS_ESC_CHARS.contains(current_sym)){
				return current_sym;
			}
			else{
				throw new LexerException("Invalid class escaped character: " + current_sym);
			}
		}
		else if(CLASS_CHARS.contains(current_sym)){
			return current_sym;
		}
		else{
			throw new LexerException("Invalid class character: " + current_sym);
		}
	}

	public NFA tokenRegex() throws LexerException{
		NFA tokenNFA;
		tokenNFA = tokenRegexStart();
		if(tokenNFA == null){
			throw new LexerException("Invalid token regex: empty");
		}
		return tokenNFA;
	}
	
	public NFA tokenRegexStart() throws LexerException{
		NFA tokenNFA = tokenRegexHead();
		NFA unionNFA = tokenRegexUnion();
		if(tokenNFA != null && unionNFA != null){
			tokenNFA.union(unionNFA);
		}
		return tokenNFA;
	}
	
	public NFA tokenRegexHead() throws LexerException{
		NFA tokenNFA = tokenRegexExp();
		if(tokenNFA != null){
			NFA concatNFA = tokenRegexTail();
			if(concatNFA != null){
				tokenNFA.concat(concatNFA);
			}
		}
		return tokenNFA;
	}
	
	public NFA tokenRegexUnion() throws LexerException{
		NFA tokenNFA = null;
		if(accept('|', NO_SPACES)){
			tokenNFA = tokenRegexStart();
		}
		return tokenNFA;
	}
	
	public NFA tokenRegexExp() throws LexerException{
		NFA tokenNFA = null;
		CharClass charClass = new CharClass();
		if(accept('(', NO_SPACES)){
			inParen++;
			tokenNFA = tokenRegexStart();
			if(tokenNFA != null){
				tokenNFA = tokenRegexOp(tokenNFA);
			}
		}
		else if(accept('$', SPACES)){
			String className = "";
			while(ID_CHARS.contains(current_sym)){
				className += current_sym;
				nextSym(SPACES);
			}
			if(current_sym==' '){
				nextSym(NO_SPACES);
			}
			if(classNames.contains(className)){
				tokenNFA = new NFA(classTable.get(className), current_type);
			}
			else{
				System.out.println(current_sym);
				throw new LexerException("Invalid character class: " + className);
			}
		}
		else if(accept('.', NO_SPACES)){
			charClass.addRange(CHAR_LOW, CHAR_HI);
			tokenNFA = new NFA(charClass, current_type);
		}
		else if(accept('[',SPACES)){
			if(accept('^',SPACES)){
				charClass = classSetExcluded(charClass);
			}
			else{
				classSet(charClass);
			}
			tokenNFA = new NFA(charClass, current_type);	
		}
		else{
			char next = getTokenChar();
			if(next>0){
				tokenNFA = new NFA(next, current_type);
				tokenNFA = tokenRegexOp(tokenNFA);	
			}
		}
		
		return tokenNFA;
	}
	
	public NFA tokenRegexTail() throws LexerException{
		NFA tokenNFA = null;
		if(inParen>0 && current_sym ==')'){
			inParen--;
			accept(')', NO_SPACES);
		}
		else if(inParen<1 && !TOKEN_CHARS.contains(current_sym)&& !TOKEN_ESC_CHARS.contains(current_sym)){
			//simply return null
		}
		else{
			tokenNFA = tokenRegexHead();
		}
		return tokenNFA;
	}
	
	public NFA tokenRegexOp(NFA tokenNFA){
		if(accept('*', NO_SPACES)){
			tokenNFA.star();
		}
		else if(accept('+', NO_SPACES)){
			tokenNFA.plus();
		}
		return tokenNFA;
	}
	
	public char getTokenChar() throws LexerException{
		char next = EOF;
		if(accept('\\',SPACES)){
			if(TOKEN_ESC_CHARS.contains(current_sym)){
				next = current_sym;
				nextSym(NO_SPACES);
			}
		}
		else if(TOKEN_CHARS.contains(current_sym)){
			next = current_sym;
			nextSym(NO_SPACES);
		}
		return next;		
	}
	
	public NFA combineNFAs() throws LexerException{
		NFA combined = null;
		if(tokenNames.isEmpty()){
			throw new LexerException("No tokes defined");
		}
		else{
			combined = tokenTable.get(tokenNames.get(0));
			for(int i=1; i<tokenNames.size(); i++){
				combined.combine(tokenTable.get(tokenNames.get(i)));
			}
		}
		return combined;
	}
}

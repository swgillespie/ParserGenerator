package edu.gatech.cs3240.lexer;

import java.util.regex.Pattern;

public class GrammarLexer extends Lexer{
	
	private static final Pattern IDENTIFIER = Pattern.compile("$[A-Z][A-Z]*");
	private static final Pattern CC_REGEX = Pattern.compile(Pattern.quote("[") + ".*" + Pattern.quote("]"));
	private static final Pattern IN = Pattern.compile("IN");
	private static final Pattern TOK_REGEX = Pattern.compile("(.*)" + Pattern.quote("*") + "*");
	

	public GrammarLexer(String fileName) throws LexerException {
		super(fileName);
	}

	@Override
	public void parse() {
		// TODO Auto-generated method stub
	}
	
	private boolean isIdentifier(String token) {
		return IDENTIFIER.matcher(token).matches();
	}
	
	private boolean isCCRegex(String token) {
		return CC_REGEX.matcher(token).matches();
	}
	
	private boolean isIn(String token) {
		return IN.matcher(token).matches();
	}
	
	private boolean isTokRegex(String token) {
		return TOK_REGEX.matcher(token).matches();
	}
	
}

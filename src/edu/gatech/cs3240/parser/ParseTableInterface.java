package edu.gatech.cs3240.parser;

public interface ParseTableInterface {

	public Production getTableEntry(String nonterminal, String terminal);
}

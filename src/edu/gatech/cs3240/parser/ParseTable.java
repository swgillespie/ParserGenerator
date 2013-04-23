package edu.gatech.cs3240.parser;

import java.util.*;

public class ParseTable implements ParseTableInterface {
	
	private FirstSet firstSet;
	private FollowSet followSet;
	private HashMap<String, ArrayList<Production>> productions;
	private Set<String> terminals;
	private Set<String> nonterminals;
	private HashMap<String, HashMap<String, Production>> parseTable;

	public ParseTable(String grammarFile) {
		ProductionFactory factory = new ProductionFactory(grammarFile);
		productions = factory.getProductions();
		terminals = new HashSet<String>(factory.getVariables());
		firstSet = new FirstSet(productions);
		followSet = new FollowSet(productions);
		buildNonterminals();
	}
	
	public void buildNonterminals() {
		nonterminals = new HashSet<String>();
		for (String s : productions.keySet()) {
			for (Production production : productions.get(s)) {
				String[] splitProd = production.getRule().split(" ");
				for (int i = 0; i < splitProd.length; i++) {
					if (!terminals.contains(splitProd[i]) && !nonterminals.contains(splitProd[i])) {
						// if it's not a terminal and it's not already in the nonterminal set
						nonterminals.add(splitProd[i]);
					}
				}
			}
		}
	}
	
	public void buildTable() {
		
	}
	
	@Override
	public Production getTableEntry(String nonterminal, String terminal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Production getStartRule() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

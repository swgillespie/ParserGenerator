package edu.gatech.cs3240.parser;

import java.util.*;

public class ParseTable implements ParseTableInterface {
	
	private FirstSet firstSet;
	private FollowSet followSet;
	private HashMap<String, ArrayList<Production>> productions;
	private ArrayList<String> variables;
	private Set<String> terminals;
	private Set<String> nonterminals;
	private HashMap<String, HashMap<String, Production>> parseTable;

	public ParseTable(String grammarFile) throws ParserException {
		System.out.println("Building productions...");
		ProductionFactory factory = new ProductionFactory(grammarFile);
		productions = factory.getProductions();
		variables = factory.getVariables();
		nonterminals = new HashSet<String>(factory.getVariables());
		System.out.println("Building first set...");
		firstSet = new FirstSet(productions, variables);
		System.out.println("Building follow set...");
		followSet = FollowSetFactory.makeFollowSet(productions, variables, firstSet.getFirstSets());
		buildTerminals();
		buildTable();
		System.out.println("table: " + parseTable);
	}
	
	public void buildTerminals() {
		terminals = new HashSet<String>();
		for (String s : productions.keySet()) {
			for (Production production : productions.get(s)) {
				String[] splitProd = production.getRule().split(" ");
				for (int i = 0; i < splitProd.length; i++) {
					if (!terminals.contains(splitProd[i]) && !nonterminals.contains(splitProd[i])) {
						// if it's not a terminal and it's not already in the nonterminal set
						terminals.add(splitProd[i]);
					}
				}
			}
		}
	}
	
	public void buildTable() {
		HashMap<String, ArrayList<String>> first = firstSet.getFirstSets();
		HashMap<String, ArrayList<String>> follow = followSet.getFollowSets();
		System.out.println("FIRST: " + first);
		System.out.println("FOLLOW: " + follow);
		parseTable = new HashMap<String, HashMap<String, Production>>();
		parseTable = new HashMap<String, HashMap<String, Production>>();
		for (String nonterminal : nonterminals) {
			parseTable.put(nonterminal, new HashMap<String, Production>());
			for (String terminal : terminals) {
				for (Production production : productions.get(nonterminal)) {
					if (first.containsKey(production.getVar())) {
						if (first.get(nonterminal).contains(terminal)) {
							System.out.println("Making rule " + nonterminal + " -> " + terminal + " (" + production.getRule() + ")");
							parseTable.get(nonterminal).put(terminal, production);
						} else if (first.get(nonterminal).contains("<empty>")) {
							System.out.println("Making rule " + nonterminal + " -> " + terminal + " (" + production.getRule() + ")");
							parseTable.get(nonterminal).put(terminal, production);
						}
					}
					if (follow.containsKey(production.getRule())) {
						if (follow.get(nonterminal).contains(terminal)) {
							System.out.println("Making rule " + nonterminal + " -> " + terminal + " (" + production.getRule() + ")");
							parseTable.get(nonterminal).put(terminal, production);
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean isTerminal(String variable) {
		//return terminals.contains(variable);
		System.out.println(variable);
		return variable.charAt(0) != '<';
	}
	
	@Override
	public Production getTableEntry(String nonterminal, String terminal) {
		if (nonterminal.charAt(0) == '<') {
			nonterminal = nonterminal.substring(1, nonterminal.length() - 1);
		}
		System.out.println("Table entry[" + nonterminal + ", " +terminal + "] = " + parseTable.get(nonterminal).get(terminal).getRule());
		return parseTable.get(nonterminal).get(terminal);
	}

	@Override
	public Production getStartRule() {
		return productions.get(variables.get(0)).get(0); // return the first rule
	}
	
	
}

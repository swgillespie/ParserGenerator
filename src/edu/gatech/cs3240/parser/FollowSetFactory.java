package edu.gatech.cs3240.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FollowSetFactory
{
	ArrayList<Production> rules;
	// Create a follow set--a hash of a variable (String) mapped to an array list of follow characters (ArrayList<Character)
	
	public static void main(String args[])
	{
		Production p1 = new Production("exp", "<exp><addop><term>", true);
		Production p2 = new Production("exp", "<term>", false);
		Production p3 = new Production("addop", "+", false);
		Production p4 = new Production("addop", "-", false);
		Production p5 = new Production("term", "<term><mulop><factor>", false);
		Production p6 = new Production("term", "<factor>", false);
		Production p7 = new Production("mulop", "+", false);
		Production p8 = new Production("factor", "(<exp>)", false);
		Production p9 = new Production("factor", "number", false);
		
		HashMap<String, ArrayList<Production>> productions = new HashMap<String, ArrayList<Production>>();
		productions.put("exp", new ArrayList<Production>(Arrays.asList(p1,p2)));
		productions.put("addop", new ArrayList<Production>(Arrays.asList(p3,p4)));
		productions.put("term", new ArrayList<Production>(Arrays.asList(p5,p6)));
		productions.put("mulop", new ArrayList<Production>(Arrays.asList(p7)));
		productions.put("factor", new ArrayList<Production>(Arrays.asList(p8,p9)));
		
		HashMap<String, ArrayList<String>> firstSet = new HashMap<String, ArrayList<String>>();
		firstSet.put("exp", new ArrayList<String>(Arrays.asList("(","number")));
		firstSet.put("term", new ArrayList<String>(Arrays.asList("(","number")));
		firstSet.put("factor", new ArrayList<String>(Arrays.asList("(","number")));
		firstSet.put("addop", new ArrayList<String>(Arrays.asList("+","-")));
		firstSet.put("mulop", new ArrayList<String>(Arrays.asList("*")));
		
		FollowSet fs = makeFollowSet(productions, new ArrayList<String>(productions.keySet()), firstSet);
		System.out.println(fs.getFollowSets());
	}
	
	public static FollowSet makeFollowSet(HashMap<String, ArrayList<Production>> productions, ArrayList<String> variables, HashMap<String, ArrayList<String>> firstSets)
	{
		FollowSet followSet = new FollowSet();
		
		// Checks for the starting production and adds $ to its follow set, otherwise initializes an empty follow set.
		for (String var : variables)
		{
			ArrayList<Production> currProds = productions.get(var);
			
			for (Production p : currProds)
			{
				if (p.isStart())
				{
					followSet.put(var, "$");
				}
			}
		}
		
		boolean change = true;
		while (change)
		{
			change = false;
			for (String var : variables) {
				System.out.println("Current variable: " + var);
				for (String temp : variables) {
					for (Production production : productions.get(temp)) {
						ArrayList<String> terminals = getTerminals(production.getRule());
						if (production.getRule().contains("<"+ var + ">")) {
							System.out.println("a" + var + "b found: " + production);
							String[] split_str = production.getRule().split(" <"+ var + "> ");
							System.out.println("split: " + Arrays.toString(split_str) + ", len=" + split_str.length);
							if (split_str.length == 2) {
								System.out.println("Entering block 1");
								String[] left_split = split_str[0].split(" ");
								String[] right_split = split_str[1].split(" ");
								String left_str = left_split[left_split.length - 1];
								String right_str = right_split[0];
								System.out.println("leftmost: " + left_str + " rightmost: " + right_str);
								if (terminals.contains(right_str)) {
									// right_str is a terminal symbol
									System.out.println("Adding " + right_str + " to follow set " + var);
									change |= followSet.put(var, right_str);
								} else {
									System.out.println("Unioning " + right_str + " and " + var);
									change |= followSet.firstSetUnion(var, firstSets.get(right_str));
								}
								if (followSet.getSingleSet(right_str) != null && followSet.getSingleSet(right_str).contains("<empty>")) {
									System.out.println("Unioning " + production.getVar() + " and " + var);
									change |= followSet.union(var, production.getVar());
								}
							} else {
								change |= followSet.union(var, production.getVar());
								System.out.println("Unioning 2 " + production.getVar() + " and " + var);
							}
						}
					}
				}
//			for (String var : variables)
//			{
//				System.out.println("prods= " +  productions);
//				ArrayList<Production> currProds = productions.get(var);
//				System.out.println("var= " + var + " , currProds= " + currProds);
//				for (Production p : currProds)
//				{
//					ArrayList<String> nonTerms = getNonTerminals(p.getRule());
//					ArrayList<String> terms = getTerminals(p.getRule());
//					System.out.println("nonterms=" + nonTerms);
//					System.out.println("terms=" + terms);
//					for (int i = 0; i < nonTerms.size(); i++)
//					{
//						
//						// If variable is a NonTerminal
//						if (nonTerms.get(i) != null)
//						{
//							// If nonTerminal has an <empty> following it
//							if (i == nonTerms.size()-1)
//							{
//								for (String s : followSet.getSingleSet(var))
//								{
//									change = followSet.addRule(nonTerms.get(i), s);
//								}
//							}
//							else
//							{
//								// If the variable following the nonTerminal is a nonTerminal
//								if (nonTerms.get(i+1) !=null)
//								{
//									for (String s : firstSets.get(nonTerms.get(i+1)))
//									{
//										change = followSet.addRule(nonTerms.get(i), s);
//									}
//								}
//								// Else add the terminal to the follow set
//								else
//								{
//									change = followSet.addRule(nonTerms.get(i), terms.get(i+1));
//								}
//							}
//						}
						
					}
				}
//			}
//		}
		
		return followSet;
	}
	
	private static ArrayList<String> getNonTerminals(String rule)
	{
		ArrayList<String> nonTerms = new ArrayList<String>();
//		boolean recordingNonTerm = false;
//		String nextNonTerm = "";
//		
//		for (int i = 0; i < rule.length(); i++)
//		{
//			String nextChar = rule.substring(i, i+1);
//			if (nextChar.equals("<"))
//			{
//				recordingNonTerm = true;
//			}
//			else if (nextChar.equals(">") && recordingNonTerm)
//			{
//				recordingNonTerm = false;
//				nonTerms.add(nextNonTerm);
//				nextNonTerm = "";
//			}
//			else if (recordingNonTerm)
//			{
//				nextNonTerm += nextChar;
//			}
//			else
//			{
//				nonTerms.add(null);
//			}
//		}
//
//		return nonTerms;
		String[] variables = rule.split(" ");
		for (int i = 0; i < variables.length; i++) {
			if (variables[i].startsWith("<") && variables[i].endsWith(">"))
				nonTerms.add(variables[i]);
		}
		return nonTerms;
	}
	
	private static ArrayList<String> getTerminals(String rule)
	{
		ArrayList<String> terms = new ArrayList<String>();
//		boolean recordingNonTerm = false;
//		
//		for (int i = 0; i < rule.length(); i++)
//		{
//			String nextChar = rule.substring(i, i+1);
//			if (nextChar.equals("<"))
//			{
//				recordingNonTerm = true;
//			}
//			else if (nextChar.equals(">") && recordingNonTerm)
//			{
//				recordingNonTerm = false;
//				terms.add(null);
//			}
//			else if (!recordingNonTerm)
//			{
//				terms.add(nextChar);
//			}
//		}
//
//		return terms;
		String[] variables = rule.split(" ");
		for (int i = 0; i < variables.length; i++) {
			if (!(variables[i].startsWith("<") && variables[i].endsWith(">")))
				terms.add(variables[i]);
		}
		return terms;
	}
}

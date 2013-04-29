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
					followSet.addRule(var, "$");
				}
				else
				{
					followSet.addRule(var, "");
				}
			}
		}
		
		boolean change = true;
		while (change)
		{
			change = false;
			for (String var : variables)
			{
				ArrayList<Production> currProds = productions.get(var);
				
				for (Production p : currProds)
				{
					ArrayList<String> nonTerms = getNonTerminals(p.getRule());
					ArrayList<String> terms = getTerminals(p.getRule());
					
					for (int i = 0; i < nonTerms.size(); i++)
					{
						// If variable is a NonTerminal
						if (nonTerms.get(i) != null)
						{
							// If nonTerminal has an <empty> following it
							if (i == nonTerms.size()-1)
							{
								for (String s : followSet.getSingleSet(var))
								{
									change = followSet.addRule(nonTerms.get(i), s);
								}
							}
							else
							{
								// If the variable following the nonTerminal is a nonTerminal
								if (nonTerms.get(i+1) !=null)
								{
									boolean hasEmpty = false;
									for (String s : firstSets.get(nonTerms.get(i+1)))
									{
										if (s.equals("<empty>"))
											hasEmpty = true;
									}
									
									if (hasEmpty)
									{
										for (String s : followSet.getSingleSet(var))
										{
											change = followSet.addRule(nonTerms.get(i), s);
										}
									}
									else
									{
										for (String s : firstSets.get(nonTerms.get(i+1)))
										{
											change = followSet.addRule(nonTerms.get(i), s);
										}
									}
								}
								// Else add the terminal to the follow set
								else
								{
									change = followSet.addRule(nonTerms.get(i), terms.get(i+1));
								}
							}
						}
					}
				}
			}
		}
		
		return followSet;
	}
	
	private static ArrayList<String> getNonTerminals(String rule)
	{
		String[] variables = rule.split(" ");
		ArrayList<String> nonTerms = new ArrayList<String>();
		for (String var : variables)
		{
			if (var.startsWith("<") && var.endsWith(">"))
				nonTerms.add(var.substring(1,var.length()-1));
			else
				nonTerms.add(null);
		}
		
		return nonTerms;
	}
	
	private static ArrayList<String> getTerminals(String rule)
	{
		String[] variables = rule.split(" ");
		ArrayList<String> terms = new ArrayList<String>();
		for (String var : variables)
		{
			if (var.startsWith("<") && var.endsWith(">"))
				terms.add(null);
			else
				terms.add(var);
		}
		
		return terms;
	}
}

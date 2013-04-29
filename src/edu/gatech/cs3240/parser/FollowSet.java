package edu.gatech.cs3240.parser;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowSet 
{
	private ArrayList<String> names;
	private HashMap<String, ArrayList<String>> followSet;
	
	public FollowSet() {
		followSet = new HashMap<String, ArrayList<String>>();
	}
	
	public ArrayList<String> getName()
	{
		return names;
	}
	
//	public boolean put(String nonterminal, String terminal) {
//		if (!followSet.containsKey(nonterminal)) {
//			followSet.put(nonterminal, new ArrayList<String>());
//		} else if (followSet.get(nonterminal).contains(terminal))
//			return false;
//		followSet.get(nonterminal).add(terminal);
//		return true;
//	}
//	
//	public boolean union(String nonterminalA, String nonterminalB) {
//		if (!(followSet.containsKey(nonterminalA) && followSet.containsKey(nonterminalB)))
//			return false;
//		else if (followSet.get(nonterminalA).containsAll(followSet.get(nonterminalB)))
//			return false;
//		followSet.get(nonterminalA).addAll(followSet.get(nonterminalB));
//		return true;
//	}
//	
//	public boolean firstSetUnion(String nonterminalA, ArrayList<String> firstSet) {
//		if (followSet.get(nonterminalA).containsAll(firstSet))
//			return false;
//		followSet.get(nonterminalA).addAll(firstSet);
//		return true;
//	}
	
	public boolean addRule(String variable, String r)
	{
		boolean change = false;
		
		ArrayList<String> rules;
		if ((rules = followSet.get(variable)) != null)
		{
			if (r != "" && !rules.contains(r))
			{
				rules.add(r);
				change = true;
			}
		}
		else
		{
			rules = new ArrayList<String>();
			
			if (r != "")
			{
				rules.add(r);
				change = true;
			}
		}
		
		followSet.put(variable, rules);
		
		return change;
	}
	
	public ArrayList<String> getSingleSet(String nonTerm)
	{
		return followSet.get(nonTerm);
	}
	
	public HashMap<String, ArrayList<String>> getFollowSets()
	{
		return followSet;
	}
	
	@Override
	public String toString() {
		return followSet.toString();
	}
}

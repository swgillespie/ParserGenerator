package edu.gatech.cs3240.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FollowSetFactory
{
	ArrayList<Production> rules;
	// Create a follow set--a hash of a variable (String) mapped to an array list of follow characters (ArrayList<Character)
	HashMap<String,ArrayList<Character>> followSet = new HashMap<String,ArrayList<Character>>();
	
	public HashMap<String,ArrayList<Character>> makeFollowSet(ArrayList<Production> rules, HashMap<String, ArrayList<Character>> firstSets)
	{
		
		for (Production prod : rules)
		{
			if (prod.isStart())
			{
				followSet.put(prod.getRule(), new ArrayList<Character>(Arrays.asList('$')));
			}
			else
			{
				followSet.put(prod.getRule(), new ArrayList<Character>());
			}
		}
		
		int i = 0;
		//while ()
		
		return followSet;
	}
	
//	private void addVariableToFollowSet(String ruleName, Character var)
//	{
//		ArrayList<Character> followArr = followSet.get(ruleName);
//		if (followArr == null)
//		{
//			followArr = new ArrayList<Character>();
//			followArr.add(var);
//		}
//		else
//		{
//			followArr.add(var);
//		}
//	}
}

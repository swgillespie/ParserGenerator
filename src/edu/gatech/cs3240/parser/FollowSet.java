package edu.gatech.cs3240.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FollowSet 
{
	private ArrayList<String> names;
	private HashMap<String, ArrayList<String>> followSet = new HashMap<String, ArrayList<String>>();
	
	public ArrayList<String> getName()
	{
		return names;
	}
	
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
}

package edu.gatech.cs3240.parser;

import java.util.ArrayList;

public class FollowSet 
{
	private String name;
	private ArrayList<Character> variables;
	private boolean isChanged = false;
	
	public FollowSet(String name, ArrayList<Character> variables)
	{
		this.name = name;
		this.variables = variables;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ArrayList<Character> getVariables()
	{
		return variables;
	}
	
	public void setChanged(boolean change)
	{
		isChanged = change;
	}
	
	public boolean isChanged()
	{
		return isChanged;
	}
}

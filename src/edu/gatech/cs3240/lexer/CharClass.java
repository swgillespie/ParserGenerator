package edu.gatech.cs3240.lexer;

import java.util.ArrayList;
/*
 * Represents the character classes that can be specified in the input file.
 * Characters can be added individually or as a range.
 * Also supports exclusive construction
 */
public class CharClass {
	public ArrayList<Integer> chars;
	
	//Create a normal character class
	public CharClass(){
		chars = new ArrayList<Integer>();
	}
	
	//Create an exclusive character class, from two others
	public CharClass(CharClass excluded, CharClass included){
		chars = new ArrayList<Integer>();
		for(int i : included.chars){
			if(!excluded.chars.contains(i)){
				chars.add(i);
			}
		}
	}
	
	//Add a single character
	public void addChar(char a){
		chars.add((int)a);
	}
	
	//Add a range of characters and return true
	//Returns false if the range is empty and no characters are added
	public boolean addRange(char a, char b){
		if(b-a <=0){
			return false;
		}
		else{
			for(int i = a; i<=b; i++){
				chars.add(i);
			}
			return true;
		}
	}
		
}

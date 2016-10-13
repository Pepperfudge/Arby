package myPackage;

import java.util.HashMap;

public class Move {
	private static HashMap<Character, Integer> letterToCol = createLettertoColumnMap();
	private static HashMap<Integer, Character> colToLetter = createColumntoLetterMap();

	public int currRow, currColumn, newRow, newColumn;
	
	public Move(String UCIFormat){
		currColumn = letterToCol.get(UCIFormat.charAt(0));
		currRow = 8 - Character.getNumericValue(UCIFormat.charAt(1));
		newColumn = letterToCol.get(UCIFormat.charAt(2));
		newRow = 8 - Character.getNumericValue(UCIFormat.charAt(3));
	}
	
	public Move(int currRow, int currColumn, int newRow, int newColumn){
		this.currRow = currRow;
		this.currColumn  = currColumn;
		this.newRow = newRow;
		this.newColumn = newColumn;
	}
	
	public String convertToUCIFormat(){
		return colToLetter.get(currColumn) + Integer.toString(8 - currRow) 
			   + colToLetter.get(newColumn) + Integer.toString(8-newRow); 
	}
	
	private static HashMap<Character, Integer> createLettertoColumnMap(){
		HashMap<Character, Integer> hash = new HashMap<Character, Integer> ();
		hash.put('a', 0);
		hash.put('b', 1);
		hash.put('c', 2);
		hash.put('d', 3);
		hash.put('e', 4);
		hash.put('f', 5);
		hash.put('g', 6);
		hash.put('h', 7);
		return hash;
	}
	
	private static HashMap<Integer, Character> createColumntoLetterMap(){
		HashMap<Integer, Character> hash = new HashMap<Integer, Character> ();
		hash.put(0,'a');
		hash.put(1,'b');
		hash.put(2,'c');
		hash.put(3,'d');
		hash.put(4,'e');
		hash.put(5,'f');
		hash.put(6,'g');
		hash.put(7,'h');
		return hash;
	}
}

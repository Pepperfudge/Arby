package myPackage;

import java.util.HashMap;

public class Square {
	public int row;
	public int col;
	
	private static HashMap<Character, Integer> letterToCol = createLettertoColumnMap();
//	private static HashMap<Integer, Character> colToLetter = createColumntoLetterMap();
	
	public Square(int row, int col){
		this.row = row;
		this.col = col;
	}
	public Square(String chessNotation){
		col = letterToCol.get(chessNotation.charAt(0));
		row =  Character.getNumericValue(chessNotation.charAt(1)) - 1;
	}
	
	private static HashMap<Character, Integer> createLettertoColumnMap(){
		HashMap<Character, Integer> hash = new HashMap<Character, Integer> ();
		hash.put('a', 7);
		hash.put('b', 6);
		hash.put('c', 5);
		hash.put('d', 4);
		hash.put('e', 3);
		hash.put('f', 2);
		hash.put('g', 1);
		hash.put('h', 0);
		return hash;
	}
	
//	private static HashMap<Integer, Character> createColumntoLetterMap(){
//		HashMap<Integer, Character> hash = new HashMap<Integer, Character> ();
//		hash.put(7,'a');
//		hash.put(6,'b');
//		hash.put(5,'c');
//		hash.put(4,'d');
//		hash.put(3,'e');
//		hash.put(2,'f');
//		hash.put(1,'g');
//		hash.put(0,'h');
//		return hash;
//	}
}

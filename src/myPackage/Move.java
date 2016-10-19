package myPackage;

import java.util.HashMap;

public class Move {
	private static HashMap<Character, Integer> letterToCol = createLettertoColumnMap();
	private static HashMap<Integer, Character> colToLetter = createColumntoLetterMap();

	public int currRow, currColumn, newRow, newColumn;
	public char promotionPiece;
	
	public Move(String UCIFormat){
		currColumn = letterToCol.get(UCIFormat.charAt(0));
		currRow = Character.getNumericValue(UCIFormat.charAt(1)) - 1;
		newColumn = letterToCol.get(UCIFormat.charAt(2));
		newRow = Character.getNumericValue(UCIFormat.charAt(3)) - 1;
		if (UCIFormat.length() == 5){
			promotionPiece = UCIFormat.charAt(4);
		} else {
			promotionPiece = 'x';
		}
	}
	
	public Move(int currRow, int currColumn, int newRow, int newColumn){
		this.currRow = currRow;
		this.currColumn  = currColumn;
		this.newRow = newRow;
		this.newColumn = newColumn;
		this.promotionPiece = 'x';
	}
	
	public Move(int currRow, int currColumn, int newRow, int newColumn, char promotionPiece){
		this.currRow = currRow;
		this.currColumn  = currColumn;
		this.newRow = newRow;
		this.newColumn = newColumn;
		this.promotionPiece = promotionPiece;
	}
	public String convertToUCIFormat(){
		if (promotionPiece != 'x'){
			return colToLetter.get(currColumn) + Integer.toString(currRow + 1) 
			   + colToLetter.get(newColumn) + Integer.toString( newRow + 1)
			   + promotionPiece;  
		} else{
			return colToLetter.get(currColumn) + Integer.toString(currRow + 1) 
			   + colToLetter.get(newColumn) + Integer.toString( newRow + 1); 
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null) {
	        return false;
	    }
	    if (!Move.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final Move other = (Move) obj;
	    return this.currColumn == other.currColumn && this.currRow == other.currRow 
	    		&& this.newColumn == other.newColumn && this.newRow == other.newRow
	    		&& this.promotionPiece == other.promotionPiece;
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
	
	private static HashMap<Integer, Character> createColumntoLetterMap(){
		HashMap<Integer, Character> hash = new HashMap<Integer, Character> ();
		hash.put(7,'a');
		hash.put(6,'b');
		hash.put(5,'c');
		hash.put(4,'d');
		hash.put(3,'e');
		hash.put(2,'f');
		hash.put(1,'g');
		hash.put(0,'h');
		return hash;
	}
}

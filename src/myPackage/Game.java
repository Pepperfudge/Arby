package myPackage;

import java.util.ArrayList;

public class Game{
	private char[][] board = {{'r','h','b','q','k','b','h','r'},
							 {'p','p','p','p','p','p','p','p'},
							 {'x','x','x','x','x','x','x','x'},
							 {'x','x','x','x','x','x','x','x'},
							 {'x','x','x','x','x','x','x','x'},
							 {'x','x','x','x','x','x','x','x'},
							 {'P','P','P','P','P','P','P','P'},
							 {'R','H','B','Q','K','B','H','R'}};
	
	private static char[] whitePieces = {'R','H','B','Q','K','P'};
	
	public Game(Game prevPosition,Move move){
		board = copyBoard(prevPosition.board);
		board[move.newRow][move.newColumn] = board[move.currRow][move.currColumn];
		board[move.currRow][move.currColumn] = 'x';
	}
	
	public Game() {
		
	}

	public ArrayList<Move> generateLegalMoves(){
		System.out.println("generateLegalMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (board[i][j] == 'p'){
					moves.addAll(generatePawnMoves(i, j));
				}
			}
		}
		return moves;
	}
	
	private ArrayList<Move> generatePawnMoves(int row, int col){
		System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row == 1 && board[3][col] == 'x'){
			moves.add(new Move(row, col, 3, col));
		} 
		if (row < 7){	
			if (board[row+1][col] == 'x'){
				moves.add(new Move(row,col,row+1,col ));
			}
			if (col > 0 && contains(whitePieces,board[row+1][col -1]) ){
				moves.add(new Move(row, col, row+1,col-1));
			} 
			if (col < 7 && contains(whitePieces,board[row+1][col + 1]) ){
				moves.add(new Move(row, col, row+1,col+1));
			} 
		}		
		return moves;
	}
	
	private static char[][] copyBoard(char[][] board){
		char [][] copy = new char[board.length][];
		for(int i = 0; i < board.length; i++){
		    copy[i] = board[i].clone();
		}
		return copy;
	}
	
	private static boolean contains(char[] array, char c){
		for (int i = 0; i<array.length; i++){
			if (array[i] == c){
				return true;
			}
		}
		return false;
	}
}

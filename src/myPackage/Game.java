package myPackage;

import java.util.ArrayList;

public class Game{
	private char[][] board = {{'R','N','B','K','Q','B','N','R'},
							 {'P','P','P','P','P','P','P','P'},
							 {'x','x','x','x','x','x','x','x'},
							 {'x','x','x','x','x','x','x','x'},
							 {'x','x','x','x','x','x','x','x'},
							 {'x','x','x','x','x','x','x','x'},
							 {'p','p','p','p','p','p','p','p'},
							 {'r','n','b','k','q','b','n','r'}};
	
	private char sideToMove;
	
	private static char[] whitePieces = {'R','H','B','Q','K','P'};
	
	public Game(Game prevPosition,Move move){
		board = copyBoard(prevPosition.board);
		board[move.newRow][move.newColumn] = board[move.currRow][move.currColumn];
		board[move.currRow][move.currColumn] = 'x';
		if (prevPosition.sideToMove == 'w'){
			this.sideToMove = 'b';
		} else{
			this.sideToMove = 'w';
		}
	}
	/* Constructor to create game out of standard position format
	   rnbqkbnr/pppppp1p/8/6p1/4P3/8/PPPP1PPP/RNBQKBNR w KQkq g6
	   <Piece Placement>
       ' ' <Side to move>
       ' ' <Castling ability>
       ' ' <En passant target square>
	 */
	public Game(String epdFormat){
		String[] fields = epdFormat.split(" ");
		
		sideToMove = fields[1].charAt(0);
		String[] piecePlacement = fields[0].split("/");
		
		
		//iterate through every rank of the board
		for(int i = 0; i < 8; i++){
			String rank = piecePlacement[i];
			int rowIndex = 7 - i;
			int colIndex = 7;
			//iterate through every piece of the rank
			for (int j = 0; j < rank.length(); j++){
				char piece = rank.charAt(j);
				int emptySquares = Character.getNumericValue(piece);
				//if piece is a number, fill empty squares with x's
				if (Character.isDigit(piece)){
					for (int k = 0; k < emptySquares; k++){
						board[rowIndex][colIndex] = 'x';
						colIndex--;
					}
				}else {
					//otherwise add the piece to the matrix
					board[rowIndex][colIndex] = piece;
					colIndex--;
				}
			}
		}
		
				
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
	
	public ArrayList<Move> generatePawnMoves(int row, int col){
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
	
	public static boolean contains(char[] array, char c){
		for (int i = 0; i<array.length; i++){
			if (array[i] == c){
				return true;
			}
		}
		return false;
	}
	
}

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
				if (board[i][j] == 'k'){ 	
					moves.addAll(generateKingMoves(i, j));
				}
				if (board[i][j] == 'r'){ 	
					moves.addAll(generateRookMoves(i, j));
				}
				if (board[i][j] == 'b'){ 	
					moves.addAll(generateBishopMoves(i, j));
				}
				if (board[i][j] == 'q'){ 	
					moves.addAll(generateQueenMoves(i, j));
				}
				if (board[i][j] == 'h'){ 	
					moves.addAll(generateKnightMoves(i, j));
				}
			}
		}
		
		/*if (blackShortCastle = on) {
			if (board[0][1] == 'x' && board[0][2] == 'x'){
				moves.add(new Move(0,3,0,1));
				if (nextMove = (0, 3, 0, 1) && board [0][3] = 'k') {
				board[0][2] = r;
				board[0][0] = 'x';
				}
			}	
		}
		if (blackLongCastle = on) {
			if (board[0][4] == 'x' && board[0][5] == 'x' && board[0][6] == 'x' ){
				moves.add(new Move(0,3,0,5));
				if (nextMove = (0, 3, 0, 5) && board [0][3] = 'k') {
				board[0][4] = r;
				board[0][7] = 'x';	
				}
			}	
		}*/
		
		return moves;
	}
	
	private ArrayList<Move> generatePawnMoves(int row, int col){
		System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row == 1 && board[2][col] == 'x' && board[3][col] == 'x'){
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
	
	private ArrayList<Move> generateKingMoves(int row, int col){
		System.out.println("generateKingMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		if (col +1 <=7) {
			if (board[row][col +1] == 'x' || contains(whitePieces,board[row][col +1])) {
			moves.add(new Move(row,col,row,col +1));
			}
		}	
		if (col -1 >=0) {
			if (board[row][col -1] == 'x' || contains(whitePieces,board[row][col -1]) ){
			moves.add(new Move(row,col,row,col -1));
			}
		}	
		if (row+1<=7){
			if (board[row +1][col] == 'x' || contains(whitePieces,board[row+1][col] )){
			moves.add(new Move(row, col, row +1,col));
			}
		}
		if	(row-1>=0) {
			if (board[row -1][col] == 'x' || contains(whitePieces,board[row-1][col] )){
			moves.add(new Move(row,col,row -1,col));
			}
		}
		if  (row +1 <= 7 && col+1 <=7) {
			if (board[row +1][col +1] == 'x' || contains(whitePieces,board[row+1][col +1] )) {
			moves.add(new Move(row,col,row +1,col +1));
			}
		}	
		if  (row +1 <= 7 && col-1 >=0) {
			if (board[row +1][ col -1] == 'x' || contains(whitePieces,board[row+1][col -1] )) {
			moves.add(new Move(row,col,row +1, col -1));
			}
		}
		if  (row -1 >= 0 && col+1 <=7) {
			if (board[row -1][ col +1] == 'x' || contains(whitePieces,board[row-1][col +1] )) {
			moves.add(new Move(row,col,row -1, col +1));
			}
		}
		if  (row -1 >=0 && col-1 >=0) {
			if (board[row -1][ col -1] == 'x' || contains(whitePieces,board[row-1][col -1] )) {
			moves.add(new Move(row,col,row -1, col -1));
			}
		}		
		return moves;
	}
	
	private ArrayList<Move> generateRookMoves(int row, int col){
		System.out.println("generateRookMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for (int i = row +1; i<=7; i++) {
		    if (board[i][col] == 'x' || contains(whitePieces,board[i][col]) ) {
			moves.add(new Move(row,col,i, col));
			} 
			if (board[i][col] != 'x' ) {
			break;
			}
		}
		for (int i = row -1; i>=0; i--) {
		    if (board[i][col] == 'x' || contains(whitePieces,board[i][col] )) {
			moves.add(new Move(row,col,i, col));
			} 
			if (board[i][col] != 'x')  {
			break;
			} 
		} 		
		for (int i = col +1; i<=7; i++) {
			if (board[row][i] == 'x' || contains(whitePieces,board[row][i] )) {
			moves.add(new Move(row,col,row, i));
			} 
			if (board[row][i] != 'x' ) {
			break;
			}
		}
		for (int i = col -1; i>=0; i--) {
		    if (board[row][i] == 'x' || contains(whitePieces,board[row][i] )) {
			moves.add(new Move(row,col,row, i));
			} 
			if (board[row][i] != 'x' ) {
			break;
			}  
		} 
		
		return moves;
	}
	
	private ArrayList<Move> generateBishopMoves(int row, int col){
		System.out.println("generateBishopMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for (int i = row +1, j = col + 1; i<=7 && j <=7; i++, j++) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row +1, j = col -1; i<=7 && j >=0; i++, j--) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row -1, j = col +1; i>=0 && j <=7; i--, j++) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x')  {
			break;
			}
		}
		for (int i = row -1, j = col -1; i>=0 && j>=0; i--, j--) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
				
		return moves;
	}
	
	private ArrayList<Move> generateQueenMoves(int row, int col){
		System.out.println("generateQueenMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for (int i = row +1, j = col + 1; i<=7 && j <=7; i++, j++) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row +1, j = col -1; i<=7 && j >=0; i++, j--) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row -1, j = col +1; i>=0 && j <=7; i--, j++) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x')  {
			break;
			}
		}
		for (int i = row -1, j = col -1; i>=0 && j>=0; i--, j--) {
			if (board[i][j] == 'x' || contains(whitePieces,board[i][j] )) {
			moves.add(new Move(row,col,i, j));
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		
		for (int i = row +1; i<=7; i++) {
		    if (board[i][col] == 'x' || contains(whitePieces,board[i][col]) ) {
			moves.add(new Move(row,col,i, col));
			} 
			if (board[i][col] != 'x' ) {
			break;
			}
		}
		for (int i = row -1; i>=0; i--) {
		    if (board[i][col] == 'x' || contains(whitePieces,board[i][col] )) {
			moves.add(new Move(row,col,i, col));
			} 
			if (board[i][col] != 'x')  {
			break;
			} 
		} 		
		for (int i = col +1; i<=7; i++) {
			if (board[row][i] == 'x' || contains(whitePieces,board[row][i] )) {
			moves.add(new Move(row,col,row, i));
			} 
			if (board[row][i] != 'x' ) {
			break;
			}
		}
		for (int i = col -1; i>=0; i--) {
		    if (board[row][i] == 'x' || contains(whitePieces,board[row][i] )) {
			moves.add(new Move(row,col,row, i));
			} 
			if (board[row][i] != 'x' ) {
			break;
			}  
		} 
				
		return moves;
	}
	
	private ArrayList<Move> generateKnightMoves(int row, int col){
		System.out.println("generateKnightMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		if  (row +2 <= 7 && col+1 <=7) {
			if (board[row+2][col +1] == 'x' || contains(whitePieces,board[row+2][col +1])) {
			moves.add(new Move(row,col,row+2,col +1));
			}
		}	
		if  (row +2 <= 7 && col-1 >=0) {
			if (board[row+2][col -1] == 'x' || contains(whitePieces,board[row+2][col -1]) ){
			moves.add(new Move(row,col,row+2,col -1));
			}
		}	
		if  (row +1 <= 7 && col+2 <=7) {
			if (board[row +1][col+2] == 'x' || contains(whitePieces,board[row+1][col+2] )){
			moves.add(new Move(row, col, row +1,col+2));
			}
		}
		if  (row +1 <= 7 && col-2 >=0) {
			if (board[row +1][col-2] == 'x' || contains(whitePieces,board[row+1][col-2] )){
			moves.add(new Move(row,col,row +1,col-2));
			}
		}
		if  (row -1 >= 0 && col+2 <=7) {
			if (board[row -1][col +2] == 'x' || contains(whitePieces,board[row-1][col +2] )) {
			moves.add(new Move(row,col,row -1,col +2));
			}
		}	
		if  (row -1 >= 0 && col-2 >=0) {
			if (board[row -1][ col -2] == 'x' || contains(whitePieces,board[row-1][col -2] )) {
			moves.add(new Move(row,col,row -1, col -2));
			}
		}
		if  (row -2 >= 0 && col+1 <=7) {
			if (board[row -2][ col +1] == 'x' || contains(whitePieces,board[row-2][col +1] )) {
			moves.add(new Move(row,col,row -2, col +1));
			}
		}
		if  (row -2 >=0 && col-1 >=0) {
			if (board[row -2][ col -1] == 'x' || contains(whitePieces,board[row-2][col -1] )) {
			moves.add(new Move(row,col,row -2, col -1));
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

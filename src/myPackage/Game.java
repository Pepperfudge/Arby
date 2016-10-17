package myPackage;

import java.util.ArrayList;

public class Game{
	private char[][] board;
	
	private char sideToMove;
	
	
	//these variables are true if the castle is still possible
	private boolean whiteQCastle;
	private boolean whiteKCastle;
	private boolean blackQCastle;
	private boolean blackKCastle;
	
	
	private static char[] whitePieces = {'R','N','B','Q','K','P'};
	
	/* default constructor to start a new game
	 */
	public Game() {
		board =  new char[][] 
				{{'R','N','B','K','Q','B','N','R'},
				 {'P','P','P','P','P','P','P','P'},
				 {'x','x','x','x','x','x','x','x'},
				 {'x','x','x','x','x','x','x','x'},
				 {'x','x','x','x','x','x','x','x'},
				 {'x','x','x','x','x','x','x','x'},
				 {'p','p','p','p','p','p','p','p'},
				 {'r','n','b','k','q','b','n','r'}
				 };
				 
		whiteQCastle = true;
		whiteKCastle = true;
		blackQCastle = true;
		blackKCastle = true;
	}
	
	public Game(Game prevPosition,Move move){
		board = copyBoard(prevPosition.board);
		char piece = board[move.currRow][move.currColumn];
		makeMove(move);

		if (piece == 'k' || piece == 'K'){
			//if the king moves it loses it's ability to castle
			if (piece == 'k'){
				blackQCastle = false;
				blackKCastle = false;
			} else{
				whiteQCastle = false;
				whiteKCastle = false;
			}
			if (Math.abs(move.currColumn - move.newColumn) == 2){
				//king side castle
				if (move.newColumn == 1){
					//move the rook in addition to the king
					makeMove(new Move(move.currRow, 0, move.currRow, 2));
				} else{ //queen side castle
					makeMove(new Move(move.currRow,7, move.currRow, 4 ));
				}
			}
		}
		
		if (piece == 'r'){
			if (blackKCastle && move.currColumn == 0 && move.currRow == 7){
				blackKCastle = false;
			}
			else if (blackQCastle && move.currColumn == 7 && move.currRow == 7){
				blackQCastle = false;
			}
		}
		
		if (piece == 'R'){
			if (whiteKCastle && move.currColumn == 0 && move.currRow == 0){
				whiteKCastle = false;
			}
			else if (whiteQCastle && move.currColumn == 7 && move.currRow == 0){
				whiteQCastle = false;
			}
		}

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
		board = new char[8][8];
		String[] fields = epdFormat.split(" ");
		
		sideToMove = fields[1].charAt(0);
		parseCastling(fields[2]);

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
	
	private void parseCastling(String castling){
		whiteKCastle = false;
		whiteQCastle = false;
		blackKCastle = false;
		blackQCastle = false;
		if (castling.contains("Q")){
			whiteQCastle = true;
		}
		if (castling.contains("K")){
			whiteKCastle = true;
		}
		if (castling.contains("q")){
			blackQCastle = true;
		}
		if (castling.contains("k")){
			blackKCastle = true;
		}
	}
	
	private void makeMove(Move move){
		board[move.newRow][move.newColumn] = board[move.currRow][move.currColumn];
		board[move.currRow][move.currColumn] = 'x';
	}
	
	public ArrayList<Move> generateLegalMoves(){
//		System.out.println("generateLegalMoves");
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
				if (board[i][j] == 'n'){ 	
					moves.addAll(generateKnightMoves(i, j));
				}
			}
		}
		
		if (blackKCastle) {
			if (board[7][1] == 'x' && board[7][2] == 'x'){
				moves.add(new Move(7,3,7,1));
			}	
		}
		if (blackQCastle) {
			if (board[7][4] == 'x' && board[7][5] == 'x' && board[7][6] == 'x' ){
				moves.add(new Move(7,3,7,5));
			}	
		}
		
		return moves;
	}
	
	private ArrayList<Move> generatePawnMoves(int row, int col){
//		System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row == 6 && board[5][col] == 'x' && board[4][col] == 'x'){
			moves.add(new Move(row, col, 4, col));
		} 
		if (row > 0){	
			if (board[row-1][col] == 'x'){
				moves.add(new Move(row,col,row-1,col ));
			}
			if (col > 0 && contains(whitePieces,board[row-1][col -1]) ){
				moves.add(new Move(row, col, row-1,col-1));
			} 
			if (col < 7 && contains(whitePieces,board[row-1][col + 1]) ){
				moves.add(new Move(row, col, row-1,col+1));
			} 
		}		
		return moves;
	}
	
	private ArrayList<Move> generateKingMoves(int row, int col){
//		System.out.println("generateKingMoves");
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
//		System.out.println("generateRookMoves");
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
//		System.out.println("generateBishopMoves");
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
//		System.out.println("generateQueenMoves");
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
//		System.out.println("generateKnightMoves");
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

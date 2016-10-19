package myPackage;

import java.util.ArrayList;

public class Game{
	private char[][] board;
	
	private char sideToMove;
	
	private int [] whiteKingLocation = new int [2];
	private int [] blackKingLocation = new int [2];
	
	//these variables are true if the castle is still possible
	private boolean whiteQCastle;
	private boolean whiteKCastle;
	private boolean blackQCastle;
	private boolean blackKCastle;
	
	
	private static char[] whitePieces = {'R','N','B','Q','K','P'};
	private static char[] blackPieces = {'r','n','b','q','k','p'};
	
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
		sideToMove = 'w';
				 
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
		System.out.format("Turn to move: %s\n", sideToMove);
	}
	
	private void makeMove(Move move){
		board[move.newRow][move.newColumn] = board[move.currRow][move.currColumn];
		board[move.currRow][move.currColumn] = 'x';
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
	


	
	/*public ArrayList<Integer> findKing(){
		ArrayList<Integer> kingLocation = new ArrayList<Integer>();
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (board[i][j] == 'k'){
					kingLocation.add(i);
					kingLocation.add(j);
				}
			}
		}
	return kingLocation;	
	}
	*/

	
	public ArrayList<Move> generateLegalMoves(){
//		System.out.println("generateLegalMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (sideToMove == 'w'){
			moves.addAll(generateWhiteMoves());
		} else {
			moves.addAll(generateBlackMoves());
		}
		return moves;
	}
	
	private ArrayList<Move> generateWhiteMoves(){
		ArrayList<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){ 
            	if (board[i][j] == 'K'){
            		whiteKingLocation [0] = i;
            		whiteKingLocation [1] = j;
            	}	
			}	
		}

		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (board[i][j] == 'P'){
					moves.addAll(generateWhitePawnMoves(i, j));
				}	
				if (board[i][j] == 'K'){ 	
					moves.addAll(generateKingMoves(i, j,blackPieces));
				}
				if (board[i][j] == 'R'){ 	
					moves.addAll(generateRookMoves(i, j,blackPieces));
				}
				if (board[i][j] == 'B'){ 	
					moves.addAll(generateBishopMoves(i, j,blackPieces));
				}
				if (board[i][j] == 'Q'){ 	
					moves.addAll(generateQueenMoves(i, j,blackPieces));
				}
				if (board[i][j] == 'N'){ 	
					moves.addAll(generateKnightMoves(i, j,blackPieces));
				}
			}
		}
		
		moves.addAll(generateCastleMoves());
		
		return moves;
	}
	
	private ArrayList<Move> generateBlackMoves(){
		ArrayList<Move> moves = new ArrayList<>();
		for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){ 
            	if (board[i][j] == 'k'){
            		blackKingLocation [0] = i;
            		blackKingLocation [1] = j;
            	}	
			}	
		}
		
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (board[i][j] == 'p'){
					moves.addAll(generateBlackPawnMoves(i, j));
				}	
				if (board[i][j] == 'k'){ 	
					moves.addAll(generateKingMoves(i, j,whitePieces));
				}
				if (board[i][j] == 'r'){ 	
					moves.addAll(generateRookMoves(i, j,whitePieces));
				}
				if (board[i][j] == 'b'){ 	
					moves.addAll(generateBishopMoves(i, j,whitePieces));
				}
				if (board[i][j] == 'q'){ 	
					moves.addAll(generateQueenMoves(i, j,whitePieces));
				}
				if (board[i][j] == 'n'){ 	
					moves.addAll(generateKnightMoves(i, j,whitePieces));
				}
			}
		}
		
		moves.addAll(generateCastleMoves());
		
		return moves;
	}
	
	private ArrayList<Move> generateCastleMoves(){
		ArrayList<Move> moves = new ArrayList<Move>();
		boolean kingCastle, queenCastle;
		int kingRow;
		if (sideToMove == 'w'){
			kingCastle = whiteKCastle;
			queenCastle = whiteQCastle;
			kingRow = 0;
		} else {
			kingCastle = blackKCastle;
			queenCastle = blackQCastle;
			kingRow = 7;
		}
		
		if (kingCastle) {
			if (board[kingRow][1] == 'x' && board[kingRow][2] == 'x' 
					&& !isKingInCheck(kingRow,3,sideToMove)
					&& !isKingInCheck(kingRow,2,sideToMove) 
					&& !isKingInCheck(kingRow,1,sideToMove)){
				moves.add(new Move(kingRow,3,kingRow,1));
			}	
		}
		if (queenCastle) {
			if (board[kingRow][4] == 'x' && board[kingRow][5] == 'x' 
					&& board[7][6] == 'x' 
					&& !isKingInCheck(kingRow,3,sideToMove) 
					&& !isKingInCheck(kingRow,4,sideToMove) 
					&& !isKingInCheck(kingRow,5,sideToMove) 
					&& !isKingInCheck(kingRow,6,sideToMove)){
				moves.add(new Move(kingRow,3,kingRow,5));
			}	
		}	
		
		return moves;
	}

	
	private ArrayList<Move> generateBlackPawnMoves(int row, int col){
//		System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row == 6 && board[5][col] == 'x' && board[4][col] == 'x'){
			Move interimMove = new Move(6, col, 4, col);
            Game nextPosition = new Game(this, interimMove);
            if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
            	moves.add(new Move(6, col, 4, col));
            }	
		} 
		if (row > 0){	
			if (board[row-1][col] == 'x'){
				Move interimMove = new Move(row,col,row-1,col);
	            Game nextPosition = new Game(this, interimMove);
	            if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){	
	            	moves.add(new Move(row,col,row-1,col ));
	            }	
			}
			if (col > 0 && contains(whitePieces,board[row-1][col -1]) ){
				Move interimMove = new Move(row, col, row-1,col-1);
	            Game nextPosition = new Game(this, interimMove);
	            if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
	            	moves.add(new Move(row, col, row-1,col-1));
	            }	
			} 
			if (col < 7 && contains(whitePieces,board[row-1][col + 1]) ){
				Move interimMove = new Move(row, col, row-1,col+1);
	            Game nextPosition = new Game(this, interimMove);
	            if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
	            	moves.add(new Move(row, col, row-1,col+1));
	            }	
			} 
		}		
		return moves;
	}
	
	private ArrayList<Move> generateWhitePawnMoves(int row, int col){
//		System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row == 1 && board[2][col] == 'x' && board[3][col] == 'x'){
			Move interimMove = new Move(1, col, 3, col);
            Game nextPosition = new Game(this, interimMove);
            if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
            	moves.add(new Move(1, col, 3, col));
            }
		} 
		if (row > 0){	
			if (board[row+1][col] == 'x'){
				Move interimMove = new Move(row,col,row+1,col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){	
					moves.add(new Move(row,col,row+1,col));
				}
			}
			if (col > 0 && contains(blackPieces,board[row+1][col -1]) ){
				Move interimMove = new Move(row, col, row+1,col-1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
					moves.add(new Move(row, col, row+1,col-1));
				}
			} 
			if (col < 7 && contains(blackPieces,board[row+1][col + 1]) ){
				Move interimMove = new Move(row, col, row+1,col+1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
					moves.add(new Move(row, col, row+1,col+1));
				}	
			} 
		}		
		return moves;
	}
	
	private ArrayList<Move> generateKingMoves(int row, int col, char[] opponentPieces){
//		System.out.println("generateKingMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		if (col +1 <=7) {
			if (board[row][col +1] == 'x' || contains(opponentPieces,board[row][col +1])) {
				if (isKingInCheck(row,col +1, sideToMove) == false) {
					moves.add(new Move(row,col,row,col +1));
				}
			}
		}	
		if (col -1 >=0) {
			if (board[row][col -1] == 'x' || contains(opponentPieces,board[row][col -1])) {
				if (isKingInCheck(row,col -1,sideToMove) == false) {
					moves.add(new Move(row,col,row,col -1));
				}
			}
		}	
		if (row+1<=7){
			if (board[row +1][col] == 'x' || contains(opponentPieces,board[row+1][col] )){
				if (isKingInCheck(row+1,col,sideToMove) == false) {
					moves.add(new Move(row, col, row +1,col));
				}
			}
		}
		if	(row-1>=0) {
			if (board[row -1][col] == 'x' || contains(opponentPieces,board[row-1][col] )){
				if (isKingInCheck(row-1,col,sideToMove) == false) {
					moves.add(new Move(row,col,row -1,col));
				}	
			}
		}
		if  (row +1 <= 7 && col+1 <=7) {
			if (board[row +1][col +1] == 'x' || contains(opponentPieces,board[row+1][col +1] )) {
				if (isKingInCheck(row+1,col+1,sideToMove) == false) {
					moves.add(new Move(row,col,row +1,col +1));
				}
			}
		}	
		if  (row +1 <= 7 && col-1 >=0) {
			if (board[row +1][col-1] == 'x' || contains(opponentPieces,board[row+1][col-1] )) {
				if (isKingInCheck(row+1,col-1,sideToMove) == false) {
					moves.add(new Move(row,col,row +1, col -1));
				}
			}
		}
		if  (row -1 >= 0 && col+1 <=7) {
			if (board[row -1][ col +1] == 'x' || contains(opponentPieces,board[row-1][col +1] )) {
				if (isKingInCheck(row-1,col+1,sideToMove) == false) {
					moves.add(new Move(row,col,row -1, col +1));
				}
			}
		}

		if  (row -1 >=0 && col-1 >=0) {
			if (board[row -1][ col -1] == 'x' || contains(opponentPieces,board[row-1][col -1] )) {
				if (isKingInCheck(row-1,col-1,sideToMove) == false) {
					moves.add(new Move(row,col,row -1, col -1));
				}
			}
		}		
		return moves;
	}
	
	private ArrayList<Move> generateRookMoves(int row, int col, char[] opponentPieces){
//		System.out.println("generateRookMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for (int i = row +1; i<=7; i++) {
		    if (board[i][col] == 'x' || contains(opponentPieces,board[i][col]) ) {
		    	Move interimMove = new Move(row, col, i,col);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, col));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, col));	
			    		}	
		    	}		
			} 
			if (board[i][col] != 'x' ) {
			break;
			}
		}
		for (int i = row -1; i>=0; i--) {
		    if (board[i][col] == 'x' || contains(opponentPieces,board[i][col] )) {
		    	Move interimMove = new Move(row, col, i,col);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, col));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, col));	
			    		}	
		    	}
			} 
			if (board[i][col] != 'x')  {
			break;
			} 
		} 		
		for (int i = col +1; i<=7; i++) {
			if (board[row][i] == 'x' || contains(opponentPieces,board[row][i] )) {
				Move interimMove = new Move(row, col, row,i);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,row, i));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,row, i));	
			    		}	
		    	}
			} 
			if (board[row][i] != 'x' ) {
			break;
			}
		}
		for (int i = col -1; i>=0; i--) {
		    if (board[row][i] == 'x' || contains(opponentPieces,board[row][i] )) {
		    	Move interimMove = new Move(row, col, row,i);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,row, i));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,row, i));	
			    		}	
		    	}
			} 
			if (board[row][i] != 'x' ) {
			break;
			}  
		} 
		
		return moves;
	}
	
	private ArrayList<Move> generateBishopMoves(int row, int col, char[] opponentPieces){
//		System.out.println("generateBishopMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for (int i = row +1, j = col + 1; i<=7 && j <=7; i++, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row +1, j = col -1; i<=7 && j >=0; i++, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row -1, j = col +1; i>=0 && j <=7; i--, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x')  {
			break;
			}
		}
		for (int i = row -1, j = col -1; i>=0 && j>=0; i--, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
				
		return moves;
	}
	
	private ArrayList<Move> generateQueenMoves(int row, int col, char[] opponentPieces){
//		System.out.println("generateQueenMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for (int i = row +1, j = col + 1; i<=7 && j <=7; i++, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row +1, j = col -1; i<=7 && j >=0; i++, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		for (int i = row -1, j = col +1; i>=0 && j <=7; i--, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x')  {
			break;
			}
		}
		for (int i = row -1, j = col -1; i>=0 && j>=0; i--, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces,board[i][j] )) {
				Move interimMove = new Move(row, col, i,j);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, j));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, j));	
			    		}	
		    	}
			} 
			if (board[i][j] != 'x' ) {
			break;
			}
		}
		
		for (int i = row +1; i<=7; i++) {
		    if (board[i][col] == 'x' || contains(opponentPieces,board[i][col]) ) {
		    	Move interimMove = new Move(row, col, i,col);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, col));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, col));	
			    		}	
		    	}		
			} 
			if (board[i][col] != 'x' ) {
			break;
			}
		}
		for (int i = row -1; i>=0; i--) {
		    if (board[i][col] == 'x' || contains(opponentPieces,board[i][col] )) {
		    	Move interimMove = new Move(row, col, i,col);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,i, col));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,i, col));	
			    		}	
		    	}
			} 
			if (board[i][col] != 'x')  {
			break;
			} 
		} 		
		for (int i = col +1; i<=7; i++) {
			if (board[row][i] == 'x' || contains(opponentPieces,board[row][i] )) {
				Move interimMove = new Move(row, col, row,i);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,row, i));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,row, i));	
			    		}	
		    	}
			} 
			if (board[row][i] != 'x' ) {
			break;
			}
		}
		for (int i = col -1; i>=0; i--) {
		    if (board[row][i] == 'x' || contains(opponentPieces,board[row][i] )) {
		    	Move interimMove = new Move(row, col, row,i);
		    	Game nextPosition = new Game(this, interimMove);
		    	if (opponentPieces == blackPieces){
		    			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
		    				moves.add(new Move(row,col,row, i));
		    			}
		    	}		
		    	if (opponentPieces == whitePieces){
			    		if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
			    			moves.add(new Move(row,col,row, i));	
			    		}	
		    	}
			} 
			if (board[row][i] != 'x' ) {
			break;
			}  
		} 			
		return moves;
	}
	
	private ArrayList<Move> generateKnightMoves(int row, int col, char[] opponentPieces){
//		System.out.println("generateKnightMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if  (row +2 <= 7 && col+1 <=7) {
			if (board[row+2][col +1] == 'x' || contains(opponentPieces,board[row+2][col +1])) {
				Move interimMove = new Move(row, col, row+2,col +1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row+2,col +1));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row+2,col +1));	
					}	
				}
			} 
		}	
		if  (row +2 <= 7 && col-1 >=0) {
			if (board[row+2][col -1] == 'x' || contains(opponentPieces,board[row+2][col -1]) ){
				Move interimMove = new Move(row, col, row+2,col -1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row+2,col -1));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row+2,col -1));	
					}	
				}
			}
		}	
		if  (row +1 <= 7 && col+2 <=7) {
			if (board[row +1][col+2] == 'x' || contains(opponentPieces,board[row+1][col+2] )){
				Move interimMove = new Move(row, col, row+1,col +2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row+1,col +2));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row+1,col +2));	
					}	
				}
			}
		}
		if  (row +1 <= 7 && col-2 >=0) {
			if (board[row +1][col-2] == 'x' || contains(opponentPieces,board[row+1][col-2] )){
				Move interimMove = new Move(row, col, row+1,col -2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row+1,col -2));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row+1,col -2));	
					}	
				}
			}
		}
		if  (row -1 >= 0 && col+2 <=7) {
			if (board[row -1][col +2] == 'x' || contains(opponentPieces,board[row-1][col +2] )) {
				Move interimMove = new Move(row, col, row-1,col +2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row-1,col +2));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row-1,col +2));	
					}	
				}
			}
		}	
		if  (row -1 >= 0 && col-2 >=0) {
			if (board[row -1][ col -2] == 'x' || contains(opponentPieces,board[row-1][col -2] )) {
				Move interimMove = new Move(row, col, row-1,col -2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row-1,col -2));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row-1,col-2));	
					}	
				}
			}
		}
		if  (row -2 >= 0 && col+1 <=7) {
			if (board[row -2][ col +1] == 'x' || contains(opponentPieces,board[row-2][col +1] )) {
				Move interimMove = new Move(row, col, row-2,col +1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row-2,col +1));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row-2,col +1));	
					}	
				}
			}
		}
		if  (row -2 >=0 && col-1 >=0) {
			if (board[row -2][ col -1] == 'x' || contains(opponentPieces,board[row-2][col -1] )) {
				Move interimMove = new Move(row, col, row-2,col -1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w'){
				    if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false){
				    	moves.add(new Move(row,col,row-2,col -1));
				    }
				}		
				if (sideToMove == 'b'){
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false){
					    moves.add(new Move(row,col,row-2,col -1));	
					}	
				}
			}
		}		
		return moves;
	}
	
	private boolean isKingInCheck(int row, int col,char color) {
		if (color == 'w'){
			return isWhiteKingInCheck(row, col);
		} else {
			return isBlackKingInCheck(row, col);
		}
	}	
	
	private boolean isWhiteKingInCheck(int row, int col){
		for (int i = row +1, j = col -1; i<=7 && j >=0; i++, j--) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			}	
			else if (board[i][j] != 'K' ) {		
			}
			else if (board[i][j] != 'x' ) {
				break;
			}
		}
		for (int i = row -1, j = col +1; i>=0 && j <=7; i--, j++) {
			if (board[i][j] == 'q' ||board[i][j] == 'b') {
				return true;
			}
			else if (board[i][j] != 'K' ) {		
			}
			else if (board[i][j] != 'x' ) {
					break;
			}
		}
		for (int i = row -1, j = col -1; i>=0 && j>=0; i--, j--) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			}
			else if (board[i][j] != 'K' ) {		
			}
			else if (board[i][j] != 'x' ) {
				break;
			}
		}
		for (int i = row +1, j = col +1; i<=7 && j<=7; i++, j++) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			}
			else if (board[i][j] != 'K' ) {		
			}
			else if (board[i][j] != 'x' ) {
				break;
			}
		}
// Rows and columns test			
		for (int i = row +1; i<=7; i++) {
			if (board[i][col] == 'q' || board[i][col] == 'r') {
				return true;
			}
			else if (board[i][col] != 'K' ) {		
			}
			else if (board[i][col] != 'x' ) {
				break;
			}
		}
		for (int i = row -1; i>=0; i--) {
			if (board[i][col] == 'q' || board[i][col] == 'r') {
				return true;
			}
			else if (board[i][col] != 'K' ) {		
			}
			else if (board[i][col] != 'x' ) {
				break;
			} 
		} 		
		for (int i = col +1; i<=7; i++) {
			if (board[row][i] == 'q' || board[row][i] == 'r') {
				return true;
			}
			else if (board[row][i] != 'K' ) {		
			}
			else if (board[row][i] != 'x' ) {
				break;
			}
		}
		for (int i = col -1; i>=0; i--) {
			if (board[row][i] == 'q' || board[row][i] == 'r') {
				return true;
			}	
			else if (board[row][i] != 'K' ) {		
			}	
			else if (board[row][i] != 'x' ) {
				break;
			}
// King and Pawn test			
		}
		if (row+1 <=7 && col-1 >= 0) {
			if (board[row+1][col-1] == 'k' || board[row+1][col-1] == 'p'){
				return true;
			}
		}	
		if (row+1 <= 7) {
			if (board[row+1][col] == 'k'){ 
				return true;
			}
		}
		if (row +1 <=7 && col+1 <= 7) {
			if (board[row+1][col+1] == 'k' || board[row+1][col+1] == 'p'){
				return true;
			}
		}
		if (col-1 >= 0) {
			if (board[row][col-1] == 'k') {
				return true;
			}
		}
		if (col+1 <= 7) {
			if (board[row][col+1] == 'k') {
				return true;
			}
		}
		if (row -1 >=0 && col-1 >= 0) {
			if (board[row-1][col-1] == 'k' ) {
				return true;
			}
		}
		if (row -1 >=0) {
			if (board[row-1][col] == 'k') {
				return true;
			}
		}
		if (row -1 >=0 && col+1 <= 7) {
			if (board[row-1][col+1] == 'k' ){
				return true;	
			}
		}	
// Knight test
		if  (row +2 <= 7 && col+1 <=7) {
			if (board[row+2][col+1] == 'n'){
				return true;
			}
		}
		if  (row +2 <= 7 && col-1 >=0) { 
			if (board[row+2][col-1] == 'n'){
				return true;
			}
		}
		if  (row +1 <= 7 && col+2 <=7) {
			if (board[row+1][col+2] == 'n'){
				return true;
			}
		}
		if  (row +1 <= 7 && col-2 >=0) {
			if (board[row+1][col-2] == 'n'){
				return true;
			}
		}
		if  (row -1 >= 0 && col+2 <=7) { 
			if (board[row-1][col+2] == 'n'){
				return true;
			}
		}
		if  (row -1 >= 0 && col-2 >=0) {
			if (board[row-1][col-2] == 'n'){ 
				return true;
			}
		}
		if  (row -2 >= 0 && col+1 <=7) { 
			if (board[row-2][col+1] == 'n') {
				return true;
			}
		}
		if  (row -2 >=0 && col-1 >=0) {
			if (board[row-2][col-1] == 'n') {
				return true;		
			}
		}
		
		return false;
	}

	private boolean isBlackKingInCheck(int row, int col){
		//Diagonals test		
		for (int i = row +1, j = col -1; i<=7 && j >=0; i++, j--) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			}
			else if (board[i][j] != 'k' ) {		
			}
			else if (board[i][j] != 'x' ) {
				break;
			}
		}
		for (int i = row -1, j = col +1; i>=0 && j <=7; i--, j++) {
			if (board[i][j] == 'Q' ||board[i][j] == 'B') {
				return true;
			}
			else if (board[i][j] != 'k' ) {		
			}
			else if (board[i][j] != 'x' ) {
				break;
			}
		}
		for (int i = row -1, j = col -1; i>=0 && j>=0; i--, j--) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			}
			else if (board[i][j] != 'k' ) {		
			}
			else if (board[i][j] != 'x' ) {
				break;
			}
		}
		for (int i = row +1, j = col +1; i<=7 && j <=7; i++, j++) {
			if (board[i][j] == 'Q' ||board[i][j] == 'B') {
				return true;
			}
			else if (board[i][j] != 'k' ) {		
			}
			else if (board[i][j] != 'x' ) {
				break;
			}
		}
// Rows and columns test			
		for (int i = row +1; i<=7; i++) {
			if (board[i][col] == 'Q' || board[i][col] == 'R') {
				return true;
			}
			else if (board[i][col] != 'k' ) {		
			}
			else if (board[i][col] != 'x' ) {
				break;
			}
		}
		for (int i = row -1; i>=0; i--) {
			if (board[i][col] == 'Q' || board[i][col] == 'R') {
				return true;
			}
			else if (board[i][col] != 'k' ) {		
			}
			else if (board[i][col] != 'x' ) {
				break;
			} 
		} 		
		for (int i = col +1; i<=7; i++) {
			if (board[row][i] == 'Q' || board[row][i] == 'R') {
				return true;
			}
			else if (board[row][i] != 'k' ) {		
			}
			else if (board[row][i] != 'x' ) {
				break;
			}
		}
		for (int i = col -1; i>=0; i--) {
			if (board[row][i] == 'Q' || board[row][i] == 'R') {
				return true;
			}
			else if (board[row][i] != 'k' ) {		
			}
			else if (board[row][i] != 'x' ) {
				break;
			}
// King and Pawn test			
		}
		if (row+1 <=7 && col-1 >= 0) {
			if (board[row+1][col-1] == 'K'){
				return true;
			}
		}	
		if (row+1 <= 7) {
			if (board[row+1][col] == 'K'){ 
				return true;
			}
		}
		if (row +1 <=7 && col+1 <= 7) {
			if (board[row+1][col+1] == 'K'){
				return true;
			}
		}
		if (col-1 >= 0) {
			if (board[row][col-1] == 'K') {
				return true;
			}
		}
		if (col+1 <= 7) {
			if (board[row][col+1] == 'K') {
				return true;
			}
		}
		if (row -1 >=0 && col-1 >= 0) {
			if (board[row-1][col-1] == 'K' || board[row-1][col-1] == 'P') {
				return true;
			}
		}
		if (row -1 >=0) {
			if (board[row-1][col] == 'K') {
				return true;
			}
		}
		if (row -1 >=0 && col+1 <= 7) {
			if (board[row-1][col+1] == 'K' || board[row-1][col+1] == 'P'){
				return true;	
			}
		}	
// Knight test
		if  (row +2 <= 7 && col+1 <=7) {
			if (board[row+2][col+1] == 'N'){
				return true;
			}
		}
		if  (row +2 <= 7 && col-1 >=0) { 
			if (board[row+2][col-1] == 'N'){
				return true;
			}
		}
		if  (row +1 <= 7 && col+2 <=7) {
			if (board[row+1][col+2] == 'N'){
				return true;
			}
		}
		if  (row +1 <= 7 && col-2 >=0) {
			if (board[row+1][col-2] == 'N'){
				return true;
			}
		}
		if  (row -1 >= 0 && col+2 <=7) { 
			if (board[row-1][col+2] == 'N'){
				return true;
			}
		}
		if  (row -1 >= 0 && col-2 >=0) {
			if (board[row-1][col-2] == 'N'){ 
				return true;
			}
		}
		if  (row -2 >= 0 && col+1 <=7) { 
			if (board[row-2][col+1] == 'N') {
				return true;
			}
		}
		if  (row -2 >=0 && col-1 >=0) {
			if (board[row-2][col-1] == 'N') {
				return true;		
			}
		}
		
		return false;
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

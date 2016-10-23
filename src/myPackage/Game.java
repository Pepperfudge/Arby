package myPackage;

import java.util.ArrayList;

public class Game {
	private char[][] board;

	private char sideToMove;

	private int[] whiteKingLocation = new int[2];
	private int[] blackKingLocation = new int[2];

	private boolean enPassant;
	private int enPassantTarget;

	private int whiteMaterialScore = 39;
	private int blackMaterialScore = 39;

	// these variables are true if the castle is still possible
	private boolean whiteQCastle;
	private boolean whiteKCastle;
	private boolean blackQCastle;
	private boolean blackKCastle;

	private static char[] whitePieces = { 'R', 'N', 'B', 'Q', 'K', 'P' };
	private static char[] blackPieces = { 'r', 'n', 'b', 'q', 'k', 'p' };

	/*
	 * default constructor to start a new game
	 */
	public Game() {
		board = new char[][] { { 'R', 'N', 'B', 'K', 'Q', 'B', 'N', 'R' }, { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
				{ 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x' }, { 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x' },
				{ 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x' }, { 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x' },
				{ 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p' }, { 'r', 'n', 'b', 'k', 'q', 'b', 'n', 'r' } };
		sideToMove = 'w';

		whiteQCastle = true;
		whiteKCastle = true;
		blackQCastle = true;
		blackKCastle = true;
	}

	public Game(Game prevPosition, Move move) {
		
		whiteQCastle = prevPosition.whiteQCastle;
		whiteKCastle = prevPosition.whiteKCastle;
		blackQCastle = prevPosition.blackQCastle;
		blackKCastle = prevPosition.blackKCastle;
		
		if (prevPosition.sideToMove == 'w') {
			this.sideToMove = 'b';
		} else {
			this.sideToMove = 'w';
		}
		
		board = copyBoard(prevPosition.board);
		char piece = board[move.currRow][move.currColumn];
		makeMove(move);

		if (piece == 'k' || piece == 'K') {
			// if the king moves it loses it's ability to castle
			if (piece == 'k') {
				blackQCastle = false;
				blackKCastle = false;
			} else {
				whiteQCastle = false;
				whiteKCastle = false;
			}
			if (Math.abs(move.currColumn - move.newColumn) == 2) {
				// king side castle
				if (move.newColumn == 1) {
					// move the rook in addition to the king
					makeMove(new Move(move.currRow, 0, move.currRow, 2));
				} else { // queen side castle
					makeMove(new Move(move.currRow, 7, move.currRow, 4));
				}
			}
		}

		if (piece == 'r') {
			if (blackKCastle && move.currColumn == 0 && move.currRow == 7) {
				blackKCastle = false;
			} else if (blackQCastle && move.currColumn == 7 && move.currRow == 7) {
				blackQCastle = false;
			}
		}

		if (piece == 'R') {
			if (whiteKCastle && move.currColumn == 0 && move.currRow == 0) {
				whiteKCastle = false;
			} else if (whiteQCastle && move.currColumn == 7 && move.currRow == 0) {
				whiteQCastle = false;
			}
		}

		if (piece == 'p' || piece == 'P') {
			if (Math.abs(move.currRow - move.newRow) == 2) {
				enPassantTarget = move.newColumn;
				enPassant = true;
			} else {
				enPassant = false;
			}
			if (Math.abs(move.currColumn - move.newColumn) == 1
					&& prevPosition.board[move.newRow][move.newColumn] == 'x') {
				if (move.newRow == 2) {
					// black en passant
					board[3][move.newColumn] = 'x';
				} else { // white en passant
					board[4][move.newColumn] = 'x';
				}
			}
		}


		// System.out.format("Turn to move: %s\n", sideToMove);

		if (prevPosition.board[move.newRow][move.newColumn] == 'Q') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 9;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'R') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 5;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'B') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 3;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'N') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 3;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'P') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 1;
		} else {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'Q') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 8;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'N') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 2;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'R') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 4;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'B') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 2;
		}

		if (prevPosition.board[move.newRow][move.newColumn] == 'q') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 9;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'r') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 5;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'b') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 3;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'n') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 3;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'p') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 1;
		} else {
			this.blackMaterialScore = prevPosition.blackMaterialScore;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'q') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 8;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'n') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 2;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'r') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 4;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'b') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 2;
		}
	}

	private void makeMove(Move move) {
		char piece = board[move.currRow][move.currColumn];
		if (piece == 'x'){
			throw new IllegalArgumentException("No piece found to move");
		}
		char promotionPiece;

		// promotion piece defaults to a queen
		if (move.promotionPiece == 'x') {
			promotionPiece = 'q';
		} else {
			promotionPiece = move.promotionPiece;
		}

		if (piece == 'p' && move.newRow == 0) {
			board[move.newRow][move.newColumn] = promotionPiece;
			board[move.currRow][move.currColumn] = 'x';
		} else if (piece == 'P' && move.newRow == 7) {
			board[move.newRow][move.newColumn] = Character.toUpperCase(promotionPiece);
			board[move.currRow][move.currColumn] = 'x';
		} else {
			board[move.newRow][move.newColumn] = board[move.currRow][move.currColumn];
			board[move.currRow][move.currColumn] = 'x';
		}

	}

	/*
	 * Constructor to create game out of standard position format
	 * rnbqkbnr/pppppp1p/8/6p1/4P3/8/PPPP1PPP/RNBQKBNR w KQkq g6 <Piece
	 * Placement> ' ' <Side to move> ' ' <Castling ability> ' ' <En passant
	 * target square>
	 */
	public Game(String epdFormat) {
		board = new char[8][8];
		String[] fields = epdFormat.split(" ");

		sideToMove = fields[1].charAt(0);
		parseCastling(fields[2]);

		String[] piecePlacement = fields[0].split("/");

		// iterate through every rank of the board
		for (int i = 0; i < 8; i++) {
			String rank = piecePlacement[i];
			int rowIndex = 7 - i;
			int colIndex = 7;
			// iterate through every piece of the rank
			for (int j = 0; j < rank.length(); j++) {
				char piece = rank.charAt(j);
				int emptySquares = Character.getNumericValue(piece);
				// if piece is a number, fill empty squares with x's
				if (Character.isDigit(piece)) {
					for (int k = 0; k < emptySquares; k++) {
						board[rowIndex][colIndex] = 'x';
						colIndex--;
					}
				} else {
					// otherwise add the piece to the matrix
					board[rowIndex][colIndex] = piece;
					colIndex--;
				}
			}
		}

	}

	private void parseCastling(String castling) {
		whiteKCastle = false;
		whiteQCastle = false;
		blackKCastle = false;
		blackQCastle = false;
		if (castling.contains("Q")) {
			whiteQCastle = true;
		}
		if (castling.contains("K")) {
			whiteKCastle = true;
		}
		if (castling.contains("q")) {
			blackQCastle = true;
		}
		if (castling.contains("k")) {
			blackKCastle = true;
		}
	}

	public Move findBestMove(int depth) {
		if (sideToMove == 'w') {
			return findBestMoveWhite(depth);
		} else {
			return findBestMoveBlack(depth);
		}
	}

	public Move findBestMoveWhite(int depth) {
		ArrayList<Move> moves = generateLegalMoves();

		double maxValue = Double.NEGATIVE_INFINITY;
		Move bestMove = null;
		for (int i = 0; i < moves.size(); i++) {
			// for (int i = 0; i < 2; i++){
			Move move = moves.get(i);
			// Move move = moves.get(new Random().nextInt(moves.size()));
			double moveValue = evaluateMoveWhite(move, depth - 1, maxValue, Double.POSITIVE_INFINITY);
			if (moveValue >= maxValue) {
				maxValue = moveValue;
				bestMove = move;
			}
		}
		// System.out.println(bestMove.convertToUCIFormat());
		// System.out.printf("W, depth %d: %f\n", depth, maxValue);
		return bestMove;
	}

	public Move findBestMoveBlack(int depth) {
		ArrayList<Move> moves = generateLegalMoves();

		double minValue = Double.POSITIVE_INFINITY;
		Move bestMove = null;
		for (int i = 0; i < moves.size(); i++) {
			// for (int i = 0; i < 2; i++){
			Move move = moves.get(i);
			// Move move = moves.get(new Random().nextInt(moves.size()));
			double moveValue = evaluateMoveBlack(move, depth - 1, Double.NEGATIVE_INFINITY, minValue);

			if (moveValue < minValue) {
				minValue = moveValue;
				bestMove = move;
			}
		}
		//System.out.println(bestMove.convertToUCIFormat());
		//System.out.printf("B, depth %d: %f\n", depth, minValue);
		return bestMove;
	}

	private double evaluateMoveWhite(Move whiteMove, int depth, double alpha, double beta) {
		Game newPosition = new Game(this, whiteMove);
		if (depth == 0) {
			//System.out.println(whiteMove.convertToUCIFormat());
			//System.out.printf("W depth %d: %f\n", depth, newPosition.evaluateBoard());
			return newPosition.evaluateBoard();
		} else {
			// to evaluate whites move we must evaluate black's response
			// Black should pick the move with the minimum value
			ArrayList<Move> blackMoves = newPosition.generateLegalMoves();
			double minValue = Double.POSITIVE_INFINITY;
			for (int i = 0; i < blackMoves.size(); i++) {
				// for (int i = 0; i < 2; i++){
				Move blackMove = blackMoves.get(i);
				// Move blackMove = blackMoves.get(new
				// Random().nextInt(blackMoves.size()));
				double moveValue = newPosition.evaluateMoveBlack(blackMove, depth - 1, alpha, Math.min(minValue, beta));

				if (moveValue < minValue) {
					minValue = moveValue;
				}
				if (moveValue < alpha) {
					//System.out.printf("Trim alpha %f depth %d\n", alpha, depth);
					break;
				}

			}
			//System.out.println(whiteMove.convertToUCIFormat());
			//System.out.printf("W, depth %d: %f\n", depth, minValue);
			return minValue;
		}

	}

	private double evaluateMoveBlack(Move blackMove, int depth, double alpha, double beta) {
		Game newPosition = new Game(this, blackMove);
		if (depth == 0) {
			// if the max depth has been reached we simply return
			// the value of the board
			//System.out.println(blackMove.convertToUCIFormat());
			//System.out.printf("B, depth %d: %f\n", depth, newPosition.evaluateBoard());
			return newPosition.evaluateBoard();
		} else {
			// to evaluate blacks move we must evaluate whites's response
			// White should pick the move with the maximum value
			ArrayList<Move> whiteMoves = newPosition.generateLegalMoves();
			double maxValue = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < whiteMoves.size(); i++) {
				// for (int i = 0; i < 2; i++){
				Move whiteMove = whiteMoves.get(i);
				// Move whiteMove = whiteMoves.get(new
				// Random().nextInt(whiteMoves.size()));
				double moveValue = newPosition.evaluateMoveWhite(whiteMove, depth - 1, Math.max(alpha, maxValue), beta);
				if (moveValue > maxValue) {
					maxValue = moveValue;
				}
				if (moveValue > beta) {
					//System.out.printf("Trim beta %f depth %d\n", beta, depth);
					break;
				}
			}
			//System.out.println(blackMove.convertToUCIFormat());
			//System.out.printf("B, depth %d: %f\n", depth, maxValue);
			return maxValue;
		}

	}

	private double evaluateBoard(){
		double positionScore=0;
		double blackScore=0;
		double whiteScore=0;
		double blackKingSafety=0;
		double whiteKingSafety=0;
		double blackDevelopment=0;
		double whiteDevelopment=0;
		double blackPawnStructure=0;
		double whitePawnStructure=0;
		double whiteKnightActivity = 0;
		double blackKnightActivity = 0;
		double whitePieceActivity = 0;
		double blackPieceActivity = 0;
		int[] whiteCheckLocation = new int[2];
		int[] blackCheckLocation = new int[2];
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'P'){
					if (i>=3 && j>0 && j<7 && (board[i-1][j-1] == 'P' || board[i-1][j+1] == 'P')){
						whitePawnStructure = whitePawnStructure + 0.05;
					}
					if (blackMaterialScore < 15){
						if (i==4){whitePawnStructure = whitePawnStructure + 0.15;}
						if (i==5){whitePawnStructure = whitePawnStructure + 0.4;}
						if (i==6){whitePawnStructure = whitePawnStructure + 1;}	
					}
				}
				if (board[i][j] == 'K') {
					whiteCheckLocation[0] = i;
					whiteCheckLocation[1] = j;
				}
				if (board[i][j] == 'N') {
					if(i==0 || i==7){ whiteKnightActivity = whiteKnightActivity - 0.1;}
					if(i==1 || i==6){ whiteKnightActivity = whiteKnightActivity - 0.05;}
					if(j==0 || j==7){ whiteKnightActivity = whiteKnightActivity - 0.1;}
					if(j==1 || j==6){ whiteKnightActivity = whiteKnightActivity - 0.05;}
				}
				if (board[i][j] == 'k') {
					blackCheckLocation[0] = i;
					blackCheckLocation[1] = j;
				}
				if (board[i][j] == 'p'){
					if (i<=4 && j>0 && j<7 && (board[i+1][j-1] == 'p' || board[i+1][j+1] == 'p')){
						blackPawnStructure = blackPawnStructure + 0.05;
					}
					if (whiteMaterialScore < 15){
						if (i==3){blackPawnStructure = blackPawnStructure + 0.15;}
						if (i==1){blackPawnStructure = blackPawnStructure + 0.4;}
						if (i==1){blackPawnStructure = blackPawnStructure + 1;}	
					}
				}
				if (board[i][j] == 'n') {
					if(i==0 || i==7){ blackKnightActivity = blackKnightActivity - 0.1;}
					if(i==1 || i==6){ blackKnightActivity = blackKnightActivity - 0.05;}
					if(j==0 || j==7){ blackKnightActivity = blackKnightActivity - 0.1;}
					if(j==1 || j==6){ blackKnightActivity = blackKnightActivity - 0.05;}
				}
			}	
		}			
		
		//blackKingSafety
		if (whiteMaterialScore > 15){
			if (board[7][1] != 'P' && board[6][1] != 'P'){blackKingSafety = blackKingSafety - 0.1;}
			if (board[7][1] != 'P' && board[7][1] != 'b'){blackKingSafety = blackKingSafety - 0.05;}
			if (board[7][2] != 'P'){blackKingSafety = blackKingSafety - 0.05;}
			if (board[7][0] != 'p' && board[6][0] != 'p'){blackKingSafety = blackKingSafety - 0.05;}
			if (blackCheckLocation[0] != 7) {blackKingSafety = blackKingSafety - 0.1;} 
			if ((board[7][1] == 'k' || board[7][0] == 'k') && board[7][0] != 'r'){ 
				blackKingSafety = blackKingSafety + 0.3;
				if (board[6][1] != 'p' && board[6][0] != 'p'){
					blackKingSafety = blackKingSafety - 0.1;
				}	
				if (board[6][1] !='p' && board[5][1] !='p'){
					blackKingSafety = blackKingSafety - 0.1;
				}
				if (board[6][0] !='p' && board[5][0] !='p'){
					blackKingSafety = blackKingSafety - 0.1;
				}
				if (board[7][2] != 'P'){blackKingSafety = blackKingSafety - 0.05;}
			}
			else if ((board[7][5] == 'k' || board[7][6] == 'k' || board[7][7] == 'k') && board[7][0] != 'r'){ 
				blackKingSafety = blackKingSafety + 0.2;
				if (board[7][5] == 'k') { 
					blackKingSafety = blackKingSafety - 0.05;
					if (board[6][5] != 'p' && board[5][5] != 'p'){blackKingSafety = blackKingSafety - 0.1;}
					if (board[6][5] != 'p' && board[6][6] != 'p'){blackKingSafety = blackKingSafety - 0.1;}
				}
				if (board[6][5] != 'p' && board[6][6] != 'p'){
					blackKingSafety = blackKingSafety - 0.1;
				}
				if (board[6][6] != 'p' && board[5][6] != 'p'){
					blackKingSafety = blackKingSafety - 0.1;
				}
				if (board[6][7] != 'p' && board[5][7] != 'p'){
					blackKingSafety = blackKingSafety - 0.1;
				}
			}	
			else if (blackKCastle == false && blackQCastle == false && (blackCheckLocation[1] == 3 || blackCheckLocation[1] == 4)) {
				blackKingSafety = blackKingSafety - 0.2;
			}
		}
		
		//whiteKingSafety
		if (blackMaterialScore > 15){
			if (board[1][1] != 'P' && board[2][1] != 'P'){whiteKingSafety = whiteKingSafety - 0.1;}
			if (board[1][1] != 'P' && board[1][1] != 'B'){whiteKingSafety = whiteKingSafety - 0.05;}
			if (board[1][2] != 'P'){whiteKingSafety = whiteKingSafety - 0.05;}
			if (board[1][0] != 'P' && board[2][0] != 'P'){whiteKingSafety = whiteKingSafety - 0.05;}
			if (whiteCheckLocation[0] != 0) {whiteKingSafety = whiteKingSafety - 0.1;}  
			if ((board[0][1] == 'K' || board[0][0] == 'K') && board[7][0] != 'R'){ 
				whiteKingSafety = whiteKingSafety + 0.3;	
				if (board[1][1] != 'P' && board[1][0] != 'P'){
					whiteKingSafety = whiteKingSafety - 0.1;
				}	
				if (board[1][1] != 'P' && board[2][1] != 'P'){
					whiteKingSafety = whiteKingSafety - 0.1;
				}
				if (board[1][0] != 'P' && board[2][0] != 'P'){
					whiteKingSafety = whiteKingSafety - 0.1;
				}
				if (board[1][2] != 'P'){whiteKingSafety = whiteKingSafety - 0.05;}
			}
			else if ((board[0][5] == 'K' || board[0][6] == 'K' || board[0][7] == 'K') && board[7][0] != 'R'){ 
				whiteKingSafety = whiteKingSafety + 0.2;
				if (board[0][5] == 'K') { 
					whiteKingSafety = whiteKingSafety - 0.05;
					if (board[1][5] != 'P' && board[2][5] != 'P'){whiteKingSafety = whiteKingSafety - 0.1;}
					if (board[1][5] != 'P' && board[1][6] != 'P'){whiteKingSafety = whiteKingSafety - 0.1;}
				}
				
				if (board[1][6] != 'P' && board[1][7] != 'P'){
					whiteKingSafety = whiteKingSafety - 0.1;
				}
				if (board[1][6] != 'P' && board[2][6] != 'P'){
					whiteKingSafety = whiteKingSafety - 0.1;
				}
				if (board[1][7] != 'P' && board[2][7] != 'P'){
					whiteKingSafety = whiteKingSafety - 0.1;
				}
			}	
			else if (whiteKCastle == false && whiteQCastle == false && (whiteCheckLocation[1] == 3 || whiteCheckLocation[1] == 4)) {
				whiteKingSafety = whiteKingSafety - 0.2;
			}	
		}
		
		//blackDevelopment
		if (board[7][1] != 'n') { blackDevelopment = blackDevelopment + 0.1; }
		if (board[7][2] != 'b') { blackDevelopment = blackDevelopment + 0.15; }
		if (board[7][5] != 'b')	{ blackDevelopment = blackDevelopment + 0.15; }
		if (board[7][6] != 'n')	{ blackDevelopment = blackDevelopment + 0.1; }
		if (board[7][1] != 'n' && board[7][2] != 'b' && board[7][5] != 'b' && board[7][6] != 'n'){
			if (board[7][4] != 'q'){blackDevelopment = blackDevelopment + 0.05; }
		}
		
		//whiteDevelopment
		if (board[0][1] != 'N') { whiteDevelopment = whiteDevelopment + 0.1; }
		if (board[0][2] != 'B') { whiteDevelopment = whiteDevelopment + 0.15; }
		if (board[0][5] != 'B')	{ whiteDevelopment = whiteDevelopment + 0.15; }
		if (board[0][6] != 'N')	{ whiteDevelopment = whiteDevelopment + 0.1; }
		if (board[0][1] != 'n' && board[0][2] != 'b' && board[0][5] != 'b' && board[0][6] != 'n'){
			if (board[0][4] != 'Q'){ whiteDevelopment = whiteDevelopment + 0.05; }
		}	
		//blackPawnStructure
		if (board[5][3]=='p') {
			blackPawnStructure = blackPawnStructure + 0.05;
		}
		if (board[5][4] =='p') {
			blackPawnStructure = blackPawnStructure + 0.05;
		}
		if (board[4][3]=='p' || board[3][3] =='p') {
			blackPawnStructure = blackPawnStructure + 0.1;
		}
		if (board[4][4] =='p' || board[3][4] == 'p') {
			blackPawnStructure = blackPawnStructure + 0.1;
		}
		
		//whitePawnStructure
		if (board[2][3]=='P') {
			whitePawnStructure = whitePawnStructure + 0.05;
		}
		if (board[2][4] =='P') {
			whitePawnStructure = whitePawnStructure + 0.05;
		}
		if (board[4][3]=='P' || board[3][3] =='P') {
			whitePawnStructure = whitePawnStructure + 0.1;
		}
		if (board[4][4] =='P' || board[3][4] == 'P') {
			whitePawnStructure = whitePawnStructure + 0.1;
		}
		whitePieceActivity = whiteKnightActivity;
		blackPieceActivity = blackKnightActivity;
		
		blackScore = blackMaterialScore + blackKingSafety + blackDevelopment + blackPawnStructure + blackPieceActivity;
		whiteScore = whiteMaterialScore + whiteKingSafety + whiteDevelopment + whitePawnStructure + whitePieceActivity;
		
		if (sideToMove == 'w'){
			/*if (isWhiteKingInCheck(whiteCheckLocation[0], whiteCheckLocation[1]) == true){
				ArrayList<Move> checkmateMoves = generateLegalMoves();
				if(checkmateMoves.isEmpty()){ 
					positionScore = -1000;
				}
			}*/
			positionScore = whiteScore - blackScore;
		}
		else{
			/*if (isBlackKingInCheck(blackCheckLocation[0], blackCheckLocation[1]) == true){
				ArrayList<Move> checkmateMoves = generateLegalMoves();
				if(checkmateMoves.isEmpty()){ 
					positionScore = 1000;
				}
			}*/
			positionScore = whiteScore - blackScore;
		}	
		
		return positionScore;
	}

	/*
	 * public ArrayList<Integer> findKing(){ ArrayList<Integer> kingLocation =
	 * new ArrayList<Integer>(); for (int i = 0; i < 8; i++){ for (int j = 0; j
	 * < 8; j++){ if (board[i][j] == 'k'){ kingLocation.add(i);
	 * kingLocation.add(j); } } } return kingLocation; }
	 */

	public ArrayList<Move> generateLegalMoves() {
		// System.out.println("generateLegalMoves");
		//System.out.println("positionScore is" + evaluateBoard());
		// System.out.println("whiteKcastle is" + whiteKCastle);
		ArrayList<Move> moves = new ArrayList<Move>();
		if (sideToMove == 'w') {
			moves.addAll(generateWhiteMoves());
		} else {
			moves.addAll(generateBlackMoves());
		}
		return moves;
	}

	private ArrayList<Move> generateWhiteMoves() {
		ArrayList<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'K') {
					whiteKingLocation[0] = i;
					whiteKingLocation[1] = j;
				}
			}
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'P') {
					moves.addAll(generateWhitePawnMoves(i, j));
				}
				if (board[i][j] == 'K') {
					moves.addAll(generateKingMoves(i, j, blackPieces));
				}
				if (board[i][j] == 'R') {
					moves.addAll(generateRookMoves(i, j, blackPieces));
				}
				if (board[i][j] == 'B') {
					moves.addAll(generateBishopMoves(i, j, blackPieces));
				}
				if (board[i][j] == 'Q') {
					moves.addAll(generateQueenMoves(i, j, blackPieces));
				}
				if (board[i][j] == 'N') {
					moves.addAll(generateKnightMoves(i, j, blackPieces));
				}
			}
		}

		moves.addAll(generateCastleMoves());

		return moves;
	}

	private ArrayList<Move> generateBlackMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'k') {
					blackKingLocation[0] = i;
					blackKingLocation[1] = j;
				}
			}
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'p') {
					moves.addAll(generateBlackPawnMoves(i, j));
				}
				if (board[i][j] == 'k') {
					moves.addAll(generateKingMoves(i, j, whitePieces));
				}
				if (board[i][j] == 'r') {
					moves.addAll(generateRookMoves(i, j, whitePieces));
				}
				if (board[i][j] == 'b') {
					moves.addAll(generateBishopMoves(i, j, whitePieces));
				}
				if (board[i][j] == 'q') {
					moves.addAll(generateQueenMoves(i, j, whitePieces));
				}
				if (board[i][j] == 'n') {
					moves.addAll(generateKnightMoves(i, j, whitePieces));
				}
			}
		}

		moves.addAll(generateCastleMoves());

		return moves;
	}

	private ArrayList<Move> generateCastleMoves() {
		ArrayList<Move> moves = new ArrayList<Move>();
		boolean kingCastle, queenCastle;
		int kingRow;
		if (sideToMove == 'w') {
			kingCastle = whiteKCastle;
			queenCastle = whiteQCastle;
			kingRow = 0;
		} else {
			kingCastle = blackKCastle;
			queenCastle = blackQCastle;
			kingRow = 7;
		}

		if (kingCastle) {
			if (board[kingRow][1] == 'x' && board[kingRow][2] == 'x' && !isKingInCheck(kingRow, 3, sideToMove)
					&& !isKingInCheck(kingRow, 2, sideToMove) && !isKingInCheck(kingRow, 1, sideToMove)) {
				moves.add(new Move(kingRow, 3, kingRow, 1));
			}
		}
		if (queenCastle) {
			if (board[kingRow][4] == 'x' && board[kingRow][5] == 'x' && board[kingRow][6] == 'x'
					&& !isKingInCheck(kingRow, 3, sideToMove) && !isKingInCheck(kingRow, 4, sideToMove)
					&& !isKingInCheck(kingRow, 5, sideToMove) && !isKingInCheck(kingRow, 6, sideToMove)) {
				moves.add(new Move(kingRow, 3, kingRow, 5));
			}
		}

		return moves;
	}

	private ArrayList<Move> generateBlackPawnMoves(int row, int col) {
		// System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row == 6 && board[5][col] == 'x' && board[4][col] == 'x') {
			Move interimMove = new Move(6, col, 4, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
				moves.add(new Move(6, col, 4, col));
			}
		}

		if (board[row - 1][col] == 'x') {
			Move interimMove = new Move(row, col, row - 1, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
				moves.add(new Move(row, col, row - 1, col));
			}
		}
		if (col > 0 && contains(whitePieces, board[row - 1][col - 1])) {
			Move interimMove = new Move(row, col, row - 1, col - 1);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
				moves.add(new Move(row, col, row - 1, col - 1));
			}
		}
		if (col < 7 && contains(whitePieces, board[row - 1][col + 1])) {
			Move interimMove = new Move(row, col, row - 1, col + 1);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
				moves.add(new Move(row, col, row - 1, col + 1));
			}
		}
		if (enPassant == true && row == 3 && (col == enPassantTarget - 1 || col == enPassantTarget + 1)) {
			Move interimMove = new Move(row, col, 2, enPassantTarget);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
				moves.add(new Move(row, col, 2, enPassantTarget));
			}
		}

		return moves;
	}

	private ArrayList<Move> generateWhitePawnMoves(int row, int col) {
		// System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row == 1 && board[2][col] == 'x' && board[3][col] == 'x') {
			Move interimMove = new Move(1, col, 3, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
				moves.add(new Move(1, col, 3, col));
			}
		}

		if (board[row + 1][col] == 'x') {
			Move interimMove = new Move(row, col, row + 1, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
				moves.add(new Move(row, col, row + 1, col));
			}
		}
		if (col > 0 && contains(blackPieces, board[row + 1][col - 1])) {
			Move interimMove = new Move(row, col, row + 1, col - 1);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
				moves.add(new Move(row, col, row + 1, col - 1));
			}
		}
		if (col < 7 && contains(blackPieces, board[row + 1][col + 1])) {
			Move interimMove = new Move(row, col, row + 1, col + 1);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
				moves.add(new Move(row, col, row + 1, col + 1));
			}
		}
		if (enPassant == true && row == 4 && (col == enPassantTarget - 1 || col == enPassantTarget + 1)) {
			Move interimMove = new Move(row, col, 5, enPassantTarget);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
				moves.add(new Move(row, col, 5, enPassantTarget));
			}
		}

		return moves;
	}

	private ArrayList<Move> generateKingMoves(int row, int col, char[] opponentPieces) {
		// System.out.println("generateKingMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

		if (col + 1 <= 7) {
			if (board[row][col + 1] == 'x' || contains(opponentPieces, board[row][col + 1])) {
				if (isKingInCheck(row, col + 1, sideToMove) == false) {
					moves.add(new Move(row, col, row, col + 1));
				}
			}
		}
		if (col - 1 >= 0) {
			if (board[row][col - 1] == 'x' || contains(opponentPieces, board[row][col - 1])) {
				if (isKingInCheck(row, col - 1, sideToMove) == false) {
					moves.add(new Move(row, col, row, col - 1));
				}
			}
		}
		if (row + 1 <= 7) {
			if (board[row + 1][col] == 'x' || contains(opponentPieces, board[row + 1][col])) {
				if (isKingInCheck(row + 1, col, sideToMove) == false) {
					moves.add(new Move(row, col, row + 1, col));
				}
			}
		}
		if (row - 1 >= 0) {
			if (board[row - 1][col] == 'x' || contains(opponentPieces, board[row - 1][col])) {
				if (isKingInCheck(row - 1, col, sideToMove) == false) {
					moves.add(new Move(row, col, row - 1, col));
				}
			}
		}
		if (row + 1 <= 7 && col + 1 <= 7) {
			if (board[row + 1][col + 1] == 'x' || contains(opponentPieces, board[row + 1][col + 1])) {
				if (isKingInCheck(row + 1, col + 1, sideToMove) == false) {
					moves.add(new Move(row, col, row + 1, col + 1));
				}
			}
		}
		if (row + 1 <= 7 && col - 1 >= 0) {
			if (board[row + 1][col - 1] == 'x' || contains(opponentPieces, board[row + 1][col - 1])) {
				if (isKingInCheck(row + 1, col - 1, sideToMove) == false) {
					moves.add(new Move(row, col, row + 1, col - 1));
				}
			}
		}
		if (row - 1 >= 0 && col + 1 <= 7) {
			if (board[row - 1][col + 1] == 'x' || contains(opponentPieces, board[row - 1][col + 1])) {
				if (isKingInCheck(row - 1, col + 1, sideToMove) == false) {
					moves.add(new Move(row, col, row - 1, col + 1));
				}
			}
		}

		if (row - 1 >= 0 && col - 1 >= 0) {
			if (board[row - 1][col - 1] == 'x' || contains(opponentPieces, board[row - 1][col - 1])) {
				if (isKingInCheck(row - 1, col - 1, sideToMove) == false) {
					moves.add(new Move(row, col, row - 1, col - 1));
				}
			}
		}
		return moves;
	}

	private ArrayList<Move> generateRookMoves(int row, int col, char[] opponentPieces) {
		// System.out.println("generateRookMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'x' || contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
			}
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'x' || contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
			}
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'x' || contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
			}
			if (board[row][i] != 'x') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'x' || contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
			}
			if (board[row][i] != 'x') {
				break;
			}
		}

		return moves;
	}

	private ArrayList<Move> generateBishopMoves(int row, int col, char[] opponentPieces) {
		// System.out.println("generateBishopMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}

		return moves;
	}

	private ArrayList<Move> generateQueenMoves(int row, int col, char[] opponentPieces) {
		// System.out.println("generateQueenMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'x' || contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, j));
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}

		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'x' || contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
			}
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'x' || contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, i, col));
					}
				}
			}
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'x' || contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
			}
			if (board[row][i] != 'x') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'x' || contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
				if (opponentPieces == whitePieces) {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row, i));
					}
				}
			}
			if (board[row][i] != 'x') {
				break;
			}
		}
		return moves;
	}

	private ArrayList<Move> generateKnightMoves(int row, int col, char[] opponentPieces) {
		// System.out.println("generateKnightMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row + 2 <= 7 && col + 1 <= 7) {
			if (board[row + 2][col + 1] == 'x' || contains(opponentPieces, board[row + 2][col + 1])) {
				Move interimMove = new Move(row, col, row + 2, col + 1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 2, col + 1));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 2, col + 1));
					}
				}
			}
		}
		if (row + 2 <= 7 && col - 1 >= 0) {
			if (board[row + 2][col - 1] == 'x' || contains(opponentPieces, board[row + 2][col - 1])) {
				Move interimMove = new Move(row, col, row + 2, col - 1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 2, col - 1));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 2, col - 1));
					}
				}
			}
		}
		if (row + 1 <= 7 && col + 2 <= 7) {
			if (board[row + 1][col + 2] == 'x' || contains(opponentPieces, board[row + 1][col + 2])) {
				Move interimMove = new Move(row, col, row + 1, col + 2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 1, col + 2));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 1, col + 2));
					}
				}
			}
		}
		if (row + 1 <= 7 && col - 2 >= 0) {
			if (board[row + 1][col - 2] == 'x' || contains(opponentPieces, board[row + 1][col - 2])) {
				Move interimMove = new Move(row, col, row + 1, col - 2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 1, col - 2));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row + 1, col - 2));
					}
				}
			}
		}
		if (row - 1 >= 0 && col + 2 <= 7) {
			if (board[row - 1][col + 2] == 'x' || contains(opponentPieces, board[row - 1][col + 2])) {
				Move interimMove = new Move(row, col, row - 1, col + 2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 1, col + 2));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 1, col + 2));
					}
				}
			}
		}
		if (row - 1 >= 0 && col - 2 >= 0) {
			if (board[row - 1][col - 2] == 'x' || contains(opponentPieces, board[row - 1][col - 2])) {
				Move interimMove = new Move(row, col, row - 1, col - 2);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 1, col - 2));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 1, col - 2));
					}
				}
			}
		}
		if (row - 2 >= 0 && col + 1 <= 7) {
			if (board[row - 2][col + 1] == 'x' || contains(opponentPieces, board[row - 2][col + 1])) {
				Move interimMove = new Move(row, col, row - 2, col + 1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 2, col + 1));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 2, col + 1));
					}
				}
			}
		}
		if (row - 2 >= 0 && col - 1 >= 0) {
			if (board[row - 2][col - 1] == 'x' || contains(opponentPieces, board[row - 2][col - 1])) {
				Move interimMove = new Move(row, col, row - 2, col - 1);
				Game nextPosition = new Game(this, interimMove);
				if (sideToMove == 'w') {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 2, col - 1));
					}
				}
				if (sideToMove == 'b') {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						moves.add(new Move(row, col, row - 2, col - 1));
					}
				}
			}
		}
		return moves;
	}

	private boolean isKingInCheck(int row, int col, char color) {
		if (color == 'w') {
			return isWhiteKingInCheck(row, col);
		} else {
			return isBlackKingInCheck(row, col);
		}
	}

	private boolean isWhiteKingInCheck(int row, int col) {
		for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++, j--) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			} else if (board[i][j] == 'K') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			} else if (board[i][j] == 'K') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			} else if (board[i][j] == 'K') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			} else if (board[i][j] == 'K') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		// Rows and columns test
		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'q' || board[i][col] == 'r') {
				return true;
			} else if (board[i][col] == 'K') {
			} else if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'q' || board[i][col] == 'r') {
				return true;
			} else if (board[i][col] == 'K') {
			} else if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'q' || board[row][i] == 'r') {
				return true;
			} else if (board[row][i] == 'K') {
			} else if (board[row][i] != 'x') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'q' || board[row][i] == 'r') {
				return true;
			} else if (board[row][i] == 'K') {
			} else if (board[row][i] != 'x') {
				break;
			}
			// King and Pawn test
		}
		if (row + 1 <= 7 && col - 1 >= 0) {
			if (board[row + 1][col - 1] == 'k' || board[row + 1][col - 1] == 'p') {
				return true;
			}
		}
		if (row + 1 <= 7) {
			if (board[row + 1][col] == 'k') {
				return true;
			}
		}
		if (row + 1 <= 7 && col + 1 <= 7) {
			if (board[row + 1][col + 1] == 'k' || board[row + 1][col + 1] == 'p') {
				return true;
			}
		}
		if (col - 1 >= 0) {
			if (board[row][col - 1] == 'k') {
				return true;
			}
		}
		if (col + 1 <= 7) {
			if (board[row][col + 1] == 'k') {
				return true;
			}
		}
		if (row - 1 >= 0 && col - 1 >= 0) {
			if (board[row - 1][col - 1] == 'k') {
				return true;
			}
		}
		if (row - 1 >= 0) {
			if (board[row - 1][col] == 'k') {
				return true;
			}
		}
		if (row - 1 >= 0 && col + 1 <= 7) {
			if (board[row - 1][col + 1] == 'k') {
				return true;
			}
		}
		// Knight test
		if (row + 2 <= 7 && col + 1 <= 7) {
			if (board[row + 2][col + 1] == 'n') {
				return true;
			}
		}
		if (row + 2 <= 7 && col - 1 >= 0) {
			if (board[row + 2][col - 1] == 'n') {
				return true;
			}
		}
		if (row + 1 <= 7 && col + 2 <= 7) {
			if (board[row + 1][col + 2] == 'n') {
				return true;
			}
		}
		if (row + 1 <= 7 && col - 2 >= 0) {
			if (board[row + 1][col - 2] == 'n') {
				return true;
			}
		}
		if (row - 1 >= 0 && col + 2 <= 7) {
			if (board[row - 1][col + 2] == 'n') {
				return true;
			}
		}
		if (row - 1 >= 0 && col - 2 >= 0) {
			if (board[row - 1][col - 2] == 'n') {
				return true;
			}
		}
		if (row - 2 >= 0 && col + 1 <= 7) {
			if (board[row - 2][col + 1] == 'n') {
				return true;
			}
		}
		if (row - 2 >= 0 && col - 1 >= 0) {
			if (board[row - 2][col - 1] == 'n') {
				return true;
			}
		}

		return false;
	}

	private boolean isBlackKingInCheck(int row, int col) {
		// Diagonals test
		for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++, j--) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			} else if (board[i][j] == 'k') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			} else if (board[i][j] == 'k') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			} else if (board[i][j] == 'k') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			} else if (board[i][j] == 'k') {
			} else if (board[i][j] != 'x') {
				break;
			}
		}
		// Rows and columns test
		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'Q' || board[i][col] == 'R') {
				return true;
			} else if (board[i][col] == 'k') {
			} else if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'Q' || board[i][col] == 'R') {
				return true;
			} else if (board[i][col] == 'k') {
			} else if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'Q' || board[row][i] == 'R') {
				return true;
			} else if (board[row][i] == 'k') {
			} else if (board[row][i] != 'x') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'Q' || board[row][i] == 'R') {
				return true;
			} else if (board[row][i] == 'k') {
			} else if (board[row][i] != 'x') {
				break;
			}
			// King and Pawn test
		}
		if (row + 1 <= 7 && col - 1 >= 0) {
			if (board[row + 1][col - 1] == 'K') {
				return true;
			}
		}
		if (row + 1 <= 7) {
			if (board[row + 1][col] == 'K') {
				return true;
			}
		}
		if (row + 1 <= 7 && col + 1 <= 7) {
			if (board[row + 1][col + 1] == 'K') {
				return true;
			}
		}
		if (col - 1 >= 0) {
			if (board[row][col - 1] == 'K') {
				return true;
			}
		}
		if (col + 1 <= 7) {
			if (board[row][col + 1] == 'K') {
				return true;
			}
		}
		if (row - 1 >= 0 && col - 1 >= 0) {
			if (board[row - 1][col - 1] == 'K' || board[row - 1][col - 1] == 'P') {
				return true;
			}
		}
		if (row - 1 >= 0) {
			if (board[row - 1][col] == 'K') {
				return true;
			}
		}
		if (row - 1 >= 0 && col + 1 <= 7) {
			if (board[row - 1][col + 1] == 'K' || board[row - 1][col + 1] == 'P') {
				return true;
			}
		}
		// Knight test
		if (row + 2 <= 7 && col + 1 <= 7) {
			if (board[row + 2][col + 1] == 'N') {
				return true;
			}
		}
		if (row + 2 <= 7 && col - 1 >= 0) {
			if (board[row + 2][col - 1] == 'N') {
				return true;
			}
		}
		if (row + 1 <= 7 && col + 2 <= 7) {
			if (board[row + 1][col + 2] == 'N') {
				return true;
			}
		}
		if (row + 1 <= 7 && col - 2 >= 0) {
			if (board[row + 1][col - 2] == 'N') {
				return true;
			}
		}
		if (row - 1 >= 0 && col + 2 <= 7) {
			if (board[row - 1][col + 2] == 'N') {
				return true;
			}
		}
		if (row - 1 >= 0 && col - 2 >= 0) {
			if (board[row - 1][col - 2] == 'N') {
				return true;
			}
		}
		if (row - 2 >= 0 && col + 1 <= 7) {
			if (board[row - 2][col + 1] == 'N') {
				return true;
			}
		}
		if (row - 2 >= 0 && col - 1 >= 0) {
			if (board[row - 2][col - 1] == 'N') {
				return true;
			}
		}

		return false;
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				str = str + String.format("%5s", board[i][j]);
			}
			str = str + "\n";
		}
		return str;

	}

	private static char[][] copyBoard(char[][] board) {
		char[][] copy = new char[board.length][];
		for (int i = 0; i < board.length; i++) {
			copy[i] = board[i].clone();
		}
		return copy;
	}

	private static boolean contains(char[] array, char c) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == c) {
				return true;
			}
		}
		return false;
	}
}

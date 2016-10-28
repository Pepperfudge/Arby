package myPackage;

import java.util.ArrayList;
import java.util.Random;

public class Game {
	private char[][] board;

	private char sideToMove;

	private int[] whiteKingLocation = {0, 3};
	private int[] blackKingLocation = {7, 3};

	private boolean enPassant;
	private int enPassantTarget;

	private int whiteMaterialScore = 3900;
	private int blackMaterialScore = 3900;

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
		char pieceTaken = board[move.newRow][move.newColumn];
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

		if (pieceTaken == 'R' && move.newRow == 0) {
			if (move.newColumn == 0) {
				whiteKCastle = false;
			} else if (move.newColumn == 7) {
				whiteQCastle = false;
			}
		}

		if (pieceTaken == 'r' && move.newRow == 7) {
			if (move.newColumn == 0) {
				blackKCastle = false;
			} else if (move.newColumn == 7) {
				blackQCastle = false;
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
		
		if (piece == 'k' || piece == 'K') {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (board[i][j] == 'K') {
						whiteKingLocation[0] = i;
						whiteKingLocation[1] = j;
					}
					if (board[i][j] == 'k') {
						blackKingLocation[0] = i;
						blackKingLocation[1] = j;
					}
				}
			}
		}	
		else {
			whiteKingLocation[0] = prevPosition.whiteKingLocation[0];	
			whiteKingLocation[1] = prevPosition.whiteKingLocation[1];
			blackKingLocation[0] = prevPosition.blackKingLocation[0];
			blackKingLocation[1] = prevPosition.blackKingLocation[1];
		}

		// System.out.format("Turn to move: %s\n", sideToMove);

		if (prevPosition.board[move.newRow][move.newColumn] == 'Q') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 900;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'R') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 500;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'B') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 300;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'N') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 300;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'P') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 100;
		} else {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'Q') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 800;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'N') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 200;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'R') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 400;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'B') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 200;
		}

		if (prevPosition.board[move.newRow][move.newColumn] == 'q') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 900;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'r') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 500;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'b') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 300;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'n') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 300;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'p') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 100;
		} else {
			this.blackMaterialScore = prevPosition.blackMaterialScore;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'q') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 800;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'n') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 200;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'r') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 400;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'b') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 200;
		}
	}

	private void makeMove(Move move) {
		char piece = board[move.currRow][move.currColumn];
		if (piece == 'x') {
			String errorMessage = String.format("bad move %s. No piece found to move. \n %s", move.convertToUCIFormat(),
					this.toString());
			throw new IllegalArgumentException(errorMessage);
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
		parseEnPassant(fields[3]);

		String[] piecePlacement = fields[0].split("/");

		boolean foundWhiteKing = false;
		boolean foundBlackKing = false;
		// iterate through every rank of the board

		whiteMaterialScore = 0;
		blackMaterialScore = 0;
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
					if (piece == 'k') {
						foundBlackKing = true;
					} else if (piece == 'K') {
						foundWhiteKing = true;
					} else if (Utils.contains(whitePieces, piece)) {
						whiteMaterialScore += Utils.pieceValues.get(piece);
					} else if (Utils.contains(blackPieces, piece)) {
						blackMaterialScore += Utils.pieceValues.get(piece);
					} else {
						throw new RuntimeException("Bad piece");
					}
					board[rowIndex][colIndex] = piece;
					colIndex--;
				}
			}
		}
		if (!foundWhiteKing || !foundBlackKing) {
			throw new IllegalArgumentException("Illegal Position. Both kings must be on the board");
		}
	}

	private void parseEnPassant(String enPassantStr) {
		if (!enPassantStr.equals("-")) {
			enPassant = true;
			enPassantTarget = (new Square(enPassantStr)).col;
		} else {
			enPassant = false;
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

	// public Move findBestMove(int depth) {
	// if (sideToMove == 'w') {
	// return findBestMoveWhite(depth);
	// } else {
	// return findBestMoveBlack(depth);
	// }
	// }
	public Move findBestMove(int depth, boolean quiesce) {
		ArrayList<Move> moves = generateLegalMoves();

		int maxValue = -20000;
		Move bestMove = null;
		for (int i = 0; i < moves.size(); i++) {
			// for (int i = 0; i < 2; i++){
			Move move = moves.get(i);
			// Move move = moves.get(new Random().nextInt(moves.size()));
			int moveValue = -evaluateMove(move, depth - 1, -20000, -maxValue, quiesce);
			// int moveValue = -evaluateMove(move,depth - 1);
			if (moveValue >= maxValue) {
				maxValue = moveValue;
				bestMove = move;
			}
		}
		// System.out.println(bestMove.convertToUCIFormat());
		// System.out.printf("W, depth %d: %f\n", depth, maxValue);
		System.out.println(maxValue);
		System.out.println(evaluateBoard());
		return bestMove;
	}

	private int evaluateMove(Move move, int depth, int alpha, int beta, boolean quiesce) {

		Game newPosition = new Game(this, move);
		if (depth == 0) {
			// System.out.println(move.convertToUCIFormat());
			// System.out.println("quiesce");
			// System.out.println(newPosition);
			int score;
			if (quiesce){
				score = newPosition.quiesce(alpha, beta);
			} else {
				score = newPosition.evaluateBoard();
				if (newPosition.sideToMove == 'b'){
					score = -1*score;
				}
			}
			// System.out.printf("%s depth %d: %f\n", newPosition.sideToMove,
			// depth, score);
			return score;
		} else {
			// System.out.printf("depth %d, alpha %f, beta %f \n", depth, alpha,
			// beta);
			ArrayList<Move> opponentMoves = newPosition.generateLegalMoves();
			int maxValue = -20000;
			for (int i = 0; i < opponentMoves.size(); i++) {
				// for (int i = 0; i < 2; i++){
				Move opponentMove = opponentMoves.get(i);
				// Move opponentMove = opponentMoves.get(
				// new Random().nextInt(opponentMoves.size()));
				int moveValue = -newPosition.evaluateMove(opponentMove, depth - 1, -beta,
						-Math.max(alpha, maxValue), quiesce);
				if (moveValue > beta) {
					// System.out.println(move.convertToUCIFormat());
					// System.out.printf("Trim beta %f depth %d\n", beta,
					// depth);
					// System.out.printf("W, depth %d: %f\n", depth, moveValue);
					return moveValue;
				}
				if (moveValue > maxValue) {
					maxValue = moveValue;
				}
			}
			// System.out.println(move.convertToUCIFormat());
			// System.out.printf("%s, depth %d: %f\n",
			// newPosition.sideToMove, depth, maxValue);
			return maxValue;
		}
	}

	private int quiesce(int alpha, int beta) {
		int stand_pat = evaluateBoard();
		if (sideToMove == 'b') {
			stand_pat = -1 * stand_pat;
		}
		if (stand_pat >= beta) {
			return stand_pat;
		}
		if (stand_pat > alpha) {
			alpha = stand_pat;
		}
		ArrayList<Move> captures = findCaptures();

		int maxValue = -20000;
		for (int i = 0; i < captures.size(); i++) {
			// for (int i = 0; i < captures.size(); i++){
			Move capture = captures.get(i);
			// Move capture = captures.get(
			// (new Random()).nextInt(captures.size()));
			// delta pruning
			char piece = board[capture.currRow][capture.currColumn];
			Integer pieceValue;
			if ((piece == 'P' || piece == 'p') && Math.abs(capture.currColumn - capture.newColumn) == 1
					&& board[capture.newRow][capture.newColumn] == 'x'
					&& (capture.newRow == 2 || capture.newRow == 5)) {
				pieceValue = 1;
			} else {
				char pieceCaptured = board[capture.newRow][capture.newColumn];
				pieceValue = Utils.pieceValues.get(pieceCaptured);
				if (pieceValue == null) {
					String errorMessage = String.format(
							"%s not a valid piece to take. \n move %s" + "\n position \n %s", pieceCaptured,
							capture.convertToUCIFormat(), this);
					throw new RuntimeException(errorMessage);
				}
			}
			if (((int) pieceValue + stand_pat + 20) > alpha) {

				// System.out.println(pieceCaptured);
				Game newPosition = new Game(this, capture);
				int moveValue = -newPosition.quiesce(-beta, -alpha);
				if (moveValue >= beta) {
					return moveValue;
				}
				if (moveValue > maxValue) {
					maxValue = moveValue;
				}
				if (moveValue > alpha) {
					alpha = moveValue;
				}
			}
		}
		return Math.max(maxValue, stand_pat);

	}

	// public Move findBestMoveWhite(int depth) {
	// ArrayList<Move> moves = generateLegalMoves();
	//
	// int maxValue = Integer.MIN_VALUE;
	// Move bestMove = null;
	// for (int i = 0; i < moves.size(); i++) {
	// // for (int i = 0; i < 2; i++){
	// Move move = moves.get(i);
	// // Move move = moves.get(new Random().nextInt(moves.size()));
	// int moveValue = evaluateMoveWhite(move, depth - 1, maxValue,
	//INTEGER.MAX_VALUE);
	// if (moveValue > maxValue) {
	// maxValue = moveValue;
	// bestMove = move;
	// }
	// }
	// System.out.println(bestMove.convertToUCIFormat());
	// System.out.printf("W, depth %d: %f\n", depth, maxValue);
	// if (bestMove != null){
	// return bestMove;
	// } else {
	// return moves.get(0);
	// }
	// }
	//
	// public Move findBestMoveBlack(int depth) {
	// ArrayList<Move> moves = generateLegalMoves();
	//
	// int minValue = Integer.MAX_VALUE;
	// Move bestMove = null;
	// for (int i = 0; i < moves.size(); i++) {
	// // for (int i = 0; i < 2; i++){
	// Move move = moves.get(i);
	// // Move move = moves.get(new Random().nextInt(moves.size()));
	// int moveValue = evaluateMoveBlack(move, depth - 1,
	// Integer.MIN_VALUE, minValue);
	//
	// if (moveValue < minValue) {
	// minValue = moveValue;
	// bestMove = move;
	// }
	// }
	// System.out.println(bestMove.convertToUCIFormat());
	// System.out.printf("B, depth %d: %f\n", depth, minValue);
	// if (bestMove != null){
	// return bestMove;
	// } else {
	// return moves.get(0);
	// }
	// }
	//
	// private int evaluateMoveWhite(Move whiteMove, int depth, int alpha,
	// int beta) {
	// Game newPosition = new Game(this, whiteMove);
	// if (depth == 0) {
	// //System.out.println(whiteMove.convertToUCIFormat());
	// //System.out.printf("W depth %d: %f\n", depth,
	// newPosition.evaluateBoard());
	// return newPosition.evaluateBoard();
	// } else {
	// // to evaluate whites move we must evaluate black's response
	// // Black should pick the move with the minimum value
	// ArrayList<Move> blackMoves = newPosition.generateLegalMoves();
	// int minValue = INTEGER.MAX_VALUE;
	// for (int i = 0; i < blackMoves.size(); i++) {
	// // for (int i = 0; i < 2; i++){
	// Move blackMove = blackMoves.get(i);
	// // Move blackMove = blackMoves.get(new
	// // Random().nextInt(blackMoves.size()));
	// int moveValue = newPosition.evaluateMoveBlack(blackMove, depth - 1,
	// alpha, Math.min(minValue, beta));
	//
	// if (moveValue < minValue) {
	// minValue = moveValue;
	// }
	// if (moveValue < alpha) {
	// //System.out.printf("Trim alpha %f depth %d\n", alpha, depth);
	// break;
	// }
	//
	// }
	// //System.out.println(whiteMove.convertToUCIFormat());
	// //System.out.printf("W, depth %d: %f\n", depth, minValue);
	// return minValue;
	// }
	//
	// }
	//
	// private int evaluateMoveBlack(Move blackMove, int depth, int alpha,
	// int beta) {
	// Game newPosition = new Game(this, blackMove);
	// if (depth == 0) {
	// // if the max depth has been reached we simply return
	// // the value of the board
	// //System.out.println(blackMove.convertToUCIFormat());
	// //System.out.printf("B, depth %d: %f\n", depth,
	// newPosition.evaluateBoard());
	// return newPosition.evaluateBoard();
	// } else {
	// // to evaluate blacks move we must evaluate whites's response
	// // White should pick the move with the maximum value
	// ArrayList<Move> whiteMoves = newPosition.generateLegalMoves();
	// int maxValue = INTEGER.MIN_VALUE;
	// for (int i = 0; i < whiteMoves.size(); i++) {
	// // for (int i = 0; i < 2; i++){
	// Move whiteMove = whiteMoves.get(i);
	// // Move whiteMove = whiteMoves.get(new
	// // Random().nextInt(whiteMoves.size()));
	// int moveValue = newPosition.evaluateMoveWhite(whiteMove, depth - 1,
	// Math.max(alpha, maxValue), beta);
	// if (moveValue > maxValue) {
	// maxValue = moveValue;
	// }
	// if (moveValue > beta) {
	// //System.out.printf("Trim beta %f depth %d\n", beta, depth);
	// break;
	// }
	// }
	// //System.out.println(blackMove.convertToUCIFormat());
	// //System.out.printf("B, depth %d: %f\n", depth, maxValue);
	// return maxValue;
	// }
	//
	// }

	private int evaluateBoard(){
		int positionScore=0;
		int blackScore=0;
		int whiteScore=0;
		int blackKingSafety=0;
		int whiteKingSafety=0;
		int blackDevelopment=0;
		int whiteDevelopment=0;
		int blackPawnStructure=0;
		int whitePawnStructure=0;
		int whiteKnightActivity = 0;
		int blackKnightActivity = 0;
		int whiteRookActivity = 0;
		int blackRookActivity = 0;
		int whiteBishopActivity = 0;
		int blackBishopActivity = 0;
		int whitePieceActivity = 0;
		int blackPieceActivity = 0;
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'P'){
					//Encourage pawn chains
					if (i>=3 && j>0 && j<7 && (board[i-1][j-1] == 'P' || board[i-1][j+1] == 'P')){
						whitePawnStructure = whitePawnStructure +5;
					}
					//Encourage pawn pushing in endgame
					if (blackMaterialScore < 1500){
						if (i==4){whitePawnStructure = whitePawnStructure + 15;}
						if (i==5){whitePawnStructure = whitePawnStructure + 40;}
						if (i==6){whitePawnStructure = whitePawnStructure + 100;}	
					}
					//Discourage doubled pawns
					if (i<5){
						if (board[i+1][j]=='P' || board[i+2][j]== 'P' || board[i+3][j]== 'P'){
						whitePawnStructure = whitePawnStructure - 20;
						}
					}
					boolean whiteIsolatedPawn = true;
					for (int k = 0; k + i <=7; k++){					
						if (j-1>=0){	
							if (board [i+k][j-1] == 'P')  {
								whiteIsolatedPawn = false;
								break;
							}	
						}		
						if (j+1<=7){
							if	(board [i+k][j+1] == 'P'){
								whiteIsolatedPawn = false;
								break;
							}	
						}
					}	
					if (whiteIsolatedPawn == true){
						for (int k = -1; k + i >=0; k--){
							if (j-1>=0){	
								if (board [i+k][j-1] == 'P')  {
									whiteIsolatedPawn = false;
									break;
								}	
							}		
							if (j+1<=7){
								if	(board [i+k][j+1] == 'P'){
									whiteIsolatedPawn = false;
									break;
								}	
							}
						}	
					}
					if (whiteIsolatedPawn == true){whitePawnStructure = whitePawnStructure - 20;}
					/*boolean passedPawn = true;
					for (int k = 1; k + i <=7; k++){
						if (board [i+k][j-1] == 'p' || board [i+k][j] == 'p' || board [i+k][j+1] == 'p'){
							passedPawn = false;
							break;
						}
					}
					if (passedPawn == true){
						whitePawnStructure = whitePawnStructure + 20;
						if (blackMaterialScore < 1500) { whitePawnStructure = whitePawnStructure + 20;}
					}*/
				}
				if (board[i][j] == 'B') {
						// Light squared bishop
						if (i==2 && j==4 && board[3][3] != 'P' && board[1][4] != 'P'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==3 && j==5 && board[5][3] != 'p'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==2 && j==6 && board[5][3] != 'p'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==4 && j==6 && board[5][5] == 'n'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==3 && j==7 && board[5][5] == 'n'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==1 && j==1 && board[3][3] != 'P'){whiteBishopActivity =	whiteBishopActivity + 15; }
						// Dark squared bishop
						if (i==2 && j==3 && board[3][4] != 'P' && board[1][3] != 'P'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==3 && j==2 && board[5][4] != 'p'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==2 && j==1 && board[5][4] != 'p'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==4 && j==1 && board[5][2] == 'n'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==3 && j==0 && board[5][2] == 'n'){whiteBishopActivity =	whiteBishopActivity + 15; }
						if (i==1 && j==6 && board[3][4] != 'P'){whiteBishopActivity =	whiteBishopActivity + 15; }
				}
				if (board[i][j] == 'R') {
					if (i<4 && j>0 && j<7){
						if(board[i+1][j] !='P' && board[i+2][j] !='P' && board[i+3][j] !='P'){
						whiteRookActivity =	whiteRookActivity + 15;
						}
					}
					if (i==6){whiteRookActivity = whiteRookActivity + 30;}
				}
				
				//discourage knight on rim
				if (board[i][j] == 'N') {
					if(i==0 || i==7){ whiteKnightActivity = whiteKnightActivity - 10;}
					if(i==1 || i==6){ whiteKnightActivity = whiteKnightActivity - 5;}
					if(j==0 || j==7){ whiteKnightActivity = whiteKnightActivity - 10;}
					if(j==1 || j==6){ whiteKnightActivity = whiteKnightActivity - 5;}
				}	
				if (board[i][j] == 'b') {
						// dark squared bishop
						if (i==5 && j==4 && board[4][3] != 'p' && board[6][4] != 'p'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==4 && j==5 && board[2][3] != 'P'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==5 && j==6 && board[2][3] != 'P'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==3 && j==6 && board[2][5] == 'N'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==4 && j==7 && board[2][5] == 'N'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==6 && j==1 && board[4][3] != 'p'){blackBishopActivity =	blackBishopActivity + 15; }
						// light squared bishop
						if (i==5 && j==3 && board[4][4] != 'p' && board[6][3] != 'p'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==4 && j==2 && board[2][4] != 'P'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==5 && j==1 && board[2][4] != 'P'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==3 && j==1 && board[2][2] == 'N'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==4 && j==0 && board[2][2] == 'N'){blackBishopActivity =	blackBishopActivity + 15; }
						if (i==6 && j==6 && board[4][4] != 'p'){blackBishopActivity =	blackBishopActivity + 15; }
				}
				
				if (board[i][j] == 'r') {
					if (i>3 && j>0 && j<7){
						if(board[i-1][j] !='p' && board[i-2][j] !='p' && board[i-3][j] !='p'){
						blackRookActivity =	blackRookActivity + 15;
						}
					}
					if (i==1){blackRookActivity = blackRookActivity + 30;}
				}
				
				if (board[i][j] == 'p'){
					if (i<=4 && j>0 && j<7 && (board[i+1][j-1] == 'p' || board[i+1][j+1] == 'p')){
						blackPawnStructure = blackPawnStructure + 5;
					}
					if (whiteMaterialScore < 1500){
						if (i==3){blackPawnStructure = blackPawnStructure + 15;}
						if (i==2){blackPawnStructure = blackPawnStructure + 40;}
						if (i==1){blackPawnStructure = blackPawnStructure + 100;}	
					}
					if (i > 2){
						if (board[i-1][j]== 'p' || board[i-2][j]== 'p' || board[i-3][j]== 'p'){
						blackPawnStructure = blackPawnStructure - 20;
						}
					}
					boolean blackIsolatedPawn = true;
					for (int k = 0; k + i <=7; k++){					
						if (j-1>=0){	
							if (board [i+k][j-1] == 'p')  {
								blackIsolatedPawn = false;
								break;
							}	
						}		
						if (j+1<=7){
							if	(board [i+k][j+1] == 'p'){
								blackIsolatedPawn = false;
								break;
							}	
						}
					}	
					if (blackIsolatedPawn == true){
						for (int k = -1; k + i >=0; k--){
							if (j-1>=0){	
								if (board [i+k][j-1] == 'p')  {
									blackIsolatedPawn = false;
									break;
								}	
							}		
							if (j+1<=7){
								if	(board [i+k][j+1] == 'p'){
									blackIsolatedPawn = false;
									break;
								}	
							}
						}	
					}	
					
					if (blackIsolatedPawn == true){blackPawnStructure = blackPawnStructure - 20;}
			}
				if (board[i][j] == 'n') {
					if(i==0 || i==7){ blackKnightActivity = blackKnightActivity - 10;}
					if(i==1 || i==6){ blackKnightActivity = blackKnightActivity - 5;}
					if(j==0 || j==7){ blackKnightActivity = blackKnightActivity - 10;}
					if(j==1 || j==6){ blackKnightActivity = blackKnightActivity - 5;}
				}
			}	
		}			
		
		//blackKingSafety
		if (whiteMaterialScore > 1500){
			//discourage pawn pushing on kingside before castling
			if (board[6][1] != 'p' && board[5][1] != 'p' && blackKingLocation[1] <5 ){blackKingSafety = blackKingSafety - 10;}
			if (board[6][1] != 'p' && board[6][1] != 'b' && blackKingLocation[1] <5){blackKingSafety = blackKingSafety - 5;}
			if (board[6][2] != 'p' && blackKingLocation[1] <5){blackKingSafety = blackKingSafety - 10;}
			if (board[6][0] != 'p' && board[5][0] != 'p' && blackKingLocation[1] <5){blackKingSafety = blackKingSafety - 20;}
			if (blackKingLocation[0] != 7) {blackKingSafety = blackKingSafety - 10;} 
			// encourage kingside castling
			if ((board[7][1] == 'k' || board[7][0] == 'k') && board[7][0] != 'r'){ 
				blackKingSafety = blackKingSafety + 30;
				if (board[6][1] != 'p' && board[6][0] != 'p'){
					blackKingSafety = blackKingSafety - 10;
				}	
				if (board[6][1] !='p' && board[5][1] !='p'){
					blackKingSafety = blackKingSafety - 10;
				}
				if (board[6][0] !='p' && board[5][0] !='p'){
					blackKingSafety = blackKingSafety - 10;
				}
				if (board[7][2] != 'p'){blackKingSafety = blackKingSafety - 5;}
			}
			// encourage queenside castling
			if ((board[7][5] == 'k' || board[7][6] == 'k' || board[7][7] == 'k') && board[7][0] != 'r'){ 
				blackKingSafety = blackKingSafety + 20;
				if (board[7][5] == 'k') { 
					blackKingSafety = blackKingSafety - 5;
					if (board[6][5] != 'p' && board[5][5] != 'p'){blackKingSafety = blackKingSafety - 10;}
					if (board[6][5] != 'p' && board[6][6] != 'p'){blackKingSafety = blackKingSafety - 10;}
				}
				if (board[6][5] != 'p' && board[6][6] != 'p'){
					blackKingSafety = blackKingSafety - 10;
				}
				if (board[6][6] != 'p' && board[5][6] != 'p'){
					blackKingSafety = blackKingSafety - 10;
				}
				if (board[6][7] != 'p' && board[5][7] != 'p'){
					blackKingSafety = blackKingSafety - 10;
				}
			}
			// Discourage losing ability to castle
			if (blackKCastle == false && (blackKingLocation[1] == 3 || blackKingLocation[1] == 4
			|| blackKingLocation[1] == 2 || (blackKingLocation[1] == 5 && board[7][7]=='R') ) ) {
				blackKingSafety = blackKingSafety - 30;
			}
		}
		
		//whiteKingSafety
		if (blackMaterialScore > 1500){
			if (board[1][1] != 'P' && board[2][1] != 'P' && whiteKingLocation[1] <5){whiteKingSafety = whiteKingSafety - 10;}
			if (board[1][1] != 'P' && board[1][1] != 'B' && whiteKingLocation[1] <5){whiteKingSafety = whiteKingSafety - 5;}
			if (board[1][2] != 'P' && whiteKingLocation[1] <5){whiteKingSafety = whiteKingSafety - 10;}
			if (board[1][0] != 'P' && board[2][0] != 'P' && whiteKingLocation[1] <5){whiteKingSafety = whiteKingSafety - 20;}
			if (whiteKingLocation[0] != 0) {whiteKingSafety = whiteKingSafety - 10;}  
			if ((board[0][1] == 'K' || board[0][0] == 'K') && board[7][0] != 'R'){ 
				whiteKingSafety = whiteKingSafety + 30;	
				if (board[1][1] != 'P' && board[1][0] != 'P'){
					whiteKingSafety = whiteKingSafety - 10;
				}	
				if (board[1][1] != 'P' && board[2][1] != 'P'){
					whiteKingSafety = whiteKingSafety - 10;
				}
				if (board[1][0] != 'P' && board[2][0] != 'P'){
					whiteKingSafety = whiteKingSafety - 10;
				}
				if (board[1][2] != 'P'){whiteKingSafety = whiteKingSafety - 5;}
			}
			if ((board[0][5] == 'K' || board[0][6] == 'K' || board[0][7] == 'K') && board[7][0] != 'R'){ 
				whiteKingSafety = whiteKingSafety + 20;
				if (board[0][5] == 'K') { 
					whiteKingSafety = whiteKingSafety - 5;
					if (board[1][5] != 'P' && board[2][5] != 'P'){whiteKingSafety = whiteKingSafety - 10;}
					if (board[1][5] != 'P' && board[1][6] != 'P'){whiteKingSafety = whiteKingSafety - 10;}
				}
				
				if (board[1][6] != 'P' && board[1][7] != 'P'){
					whiteKingSafety = whiteKingSafety - 10;
				}
				if (board[1][6] != 'P' && board[2][6] != 'P'){
					whiteKingSafety = whiteKingSafety - 10;
				}
				if (board[1][7] != 'P' && board[2][7] != 'P'){
					whiteKingSafety = whiteKingSafety - 10;
				}
			}	
			if (whiteKCastle == false && ((whiteKingLocation[1] == 3 || whiteKingLocation[1] == 4 
			|| whiteKingLocation[1] == 2) || (whiteKingLocation[1] == 5 && board[0][7]=='R') ) ){
				whiteKingSafety = whiteKingSafety - 30;
			}	
		}
		
		//blackDevelopment
		if (board[7][1] != 'n') { blackDevelopment = blackDevelopment + 10; }
		if (board[7][2] != 'b') { blackDevelopment = blackDevelopment + 15; }
		if (board[7][5] != 'b')	{ blackDevelopment = blackDevelopment + 15; }
		if (board[7][6] != 'n')	{ blackDevelopment = blackDevelopment + 10; }
		if (board[7][1] != 'n' && board[7][2] != 'b' && board[7][5] != 'b' && board[7][6] != 'n'){
			if (board[7][4] != 'q'){blackDevelopment = blackDevelopment + 5; }
		}
		
		//whiteDevelopment
		if (board[0][1] != 'N') { whiteDevelopment = whiteDevelopment + 10; }
		if (board[0][2] != 'B') { whiteDevelopment = whiteDevelopment + 15; }
		if (board[0][5] != 'B')	{ whiteDevelopment = whiteDevelopment + 15; }
		if (board[0][6] != 'N')	{ whiteDevelopment = whiteDevelopment + 10; }
		if (board[0][1] != 'N' && board[0][2] != 'N' && board[0][5] != 'B' && board[0][6] != 'N'){
			if (board[0][4] != 'Q'){ whiteDevelopment = whiteDevelopment + 5; }
		}	
		//blackPawnStructure
		if (board[5][3]=='p') {
			blackPawnStructure = blackPawnStructure + 5;
		}
		if (board[5][4] =='p') {
			blackPawnStructure = blackPawnStructure + 5;
		}
		if (board[4][3]=='p' || board[3][3] =='p') {
			blackPawnStructure = blackPawnStructure + 10;
		}
		if (board[4][4] =='p' || board[3][4] == 'p') {
			blackPawnStructure = blackPawnStructure + 10;
		}
		
		//whitePawnStructure
		if (board[2][3]=='P') {
			whitePawnStructure = whitePawnStructure + 5;
		}
		if (board[2][4] =='P') {
			whitePawnStructure = whitePawnStructure + 5;
		}
		if (board[4][3]=='P' || board[3][3] =='P') {
			whitePawnStructure = whitePawnStructure + 10;
		}
		if (board[4][4] =='P' || board[3][4] == 'P') {
			whitePawnStructure = whitePawnStructure + 10;
		}
		whitePieceActivity = whiteKnightActivity + whiteRookActivity + whiteBishopActivity;
		blackPieceActivity = blackKnightActivity + blackRookActivity + blackBishopActivity;
		
		blackScore = blackMaterialScore + blackKingSafety + blackDevelopment + blackPawnStructure + blackPieceActivity  ;
		whiteScore = whiteMaterialScore + whiteKingSafety + whiteDevelopment + whitePawnStructure + whitePieceActivity;
		
		if (sideToMove == 'w'){
			/*if (isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == true){
				ArrayList<Move> checkmateMoves = generateLegalMoves();
				if(checkmateMoves.isEmpty()){ 
					positionScore = Integer.MIN_VALUE;
				}
			}*/
			positionScore = whiteScore - blackScore;
		}
		else{
			/*if (isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == true){
				ArrayList<Move> checkmateMoves = generateLegalMoves();
				if(checkmateMoves.isEmpty()){ 
					positionScore = Integer.MAX_VALUE;
				}
			}*/
			positionScore = whiteScore - blackScore;
		}	
		
		return positionScore;
	}

	public ArrayList<Move> findCaptures() {
		// System.out.println("generateLegalMoves");
		// System.out.println("positionScore is" + evaluateBoard());
		// System.out.println("whiteKcastle is" + whiteKCastle);
		ArrayList<Move> captures = new ArrayList<Move>();
		if (sideToMove == 'w') {
			captures.addAll(findWhiteCaptures());
		} else {
			captures.addAll(findBlackCaptures());
		}
		return captures;
	}

	private ArrayList<Move> findWhiteCaptures() {
		ArrayList<Move> moves = new ArrayList<>();
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
					moves.addAll(findWhitePawnCaptures(i, j));
				} else if (board[i][j] == 'K') {
					moves.addAll(findKingCaptures(i, j, blackPieces));
				} else if (board[i][j] == 'R') {
					moves.addAll(findRookCaptures(i, j, blackPieces));
				} else if (board[i][j] == 'B') {
					moves.addAll(findBishopCaptures(i, j, blackPieces));
				} else if (board[i][j] == 'Q') {
					moves.addAll(findQueenCaptures(i, j, blackPieces));
				} else if (board[i][j] == 'N') {
					moves.addAll(findKnightCaptures(i, j, blackPieces));
				}
			}
		}

		return moves;
	}

	private ArrayList<Move> findBlackCaptures() {
		ArrayList<Move> moves = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'p') {
					moves.addAll(findBlackPawnCaptures(i, j));
				} else if (board[i][j] == 'k') {
					moves.addAll(findKingCaptures(i, j, whitePieces));
				} else if (board[i][j] == 'r') {
					moves.addAll(findRookCaptures(i, j, whitePieces));
				} else if (board[i][j] == 'b') {
					moves.addAll(findBishopCaptures(i, j, whitePieces));
				} else if (board[i][j] == 'q') {
					moves.addAll(findQueenCaptures(i, j, whitePieces));
				} else if (board[i][j] == 'n') {
					moves.addAll(findKnightCaptures(i, j, whitePieces));
				}
			}
		}

		return moves;
	}

	public ArrayList<Move> generateLegalMoves() {
		// System.out.println("generateLegalMoves");
		// System.out.println("positionScore is" + evaluateBoard());
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

		moves.addAll(findBlackPawnCaptures(row, col));

		return moves;
	}

	private ArrayList<Move> findBlackPawnCaptures(int row, int col) {
		// System.out.println("generatePawnMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

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

		moves.addAll(findWhitePawnCaptures(row, col));
		return moves;
	}

	private ArrayList<Move> findWhitePawnCaptures(int row, int col) {
		ArrayList<Move> moves = new ArrayList<Move>();
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

	private ArrayList<Move> findKingCaptures(int row, int col, char[] opponentPieces) {
		// System.out.println("generateKingMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

		if (col + 1 <= 7) {
			if (contains(opponentPieces, board[row][col + 1])) {
				if (isKingInCheck(row, col + 1, sideToMove) == false) {
					moves.add(new Move(row, col, row, col + 1));
				}
			}
		}
		if (col - 1 >= 0) {
			if (contains(opponentPieces, board[row][col - 1])) {
				if (isKingInCheck(row, col - 1, sideToMove) == false) {
					moves.add(new Move(row, col, row, col - 1));
				}
			}
		}
		if (row + 1 <= 7) {
			if (contains(opponentPieces, board[row + 1][col])) {
				if (isKingInCheck(row + 1, col, sideToMove) == false) {
					moves.add(new Move(row, col, row + 1, col));
				}
			}
		}
		if (row - 1 >= 0) {
			if (contains(opponentPieces, board[row - 1][col])) {
				if (isKingInCheck(row - 1, col, sideToMove) == false) {
					moves.add(new Move(row, col, row - 1, col));
				}
			}
		}
		if (row + 1 <= 7 && col + 1 <= 7) {
			if (contains(opponentPieces, board[row + 1][col + 1])) {
				if (isKingInCheck(row + 1, col + 1, sideToMove) == false) {
					moves.add(new Move(row, col, row + 1, col + 1));
				}
			}
		}
		if (row + 1 <= 7 && col - 1 >= 0) {
			if (contains(opponentPieces, board[row + 1][col - 1])) {
				if (isKingInCheck(row + 1, col - 1, sideToMove) == false) {
					moves.add(new Move(row, col, row + 1, col - 1));
				}
			}
		}
		if (row - 1 >= 0 && col + 1 <= 7) {
			if (contains(opponentPieces, board[row - 1][col + 1])) {
				if (isKingInCheck(row - 1, col + 1, sideToMove) == false) {
					moves.add(new Move(row, col, row - 1, col + 1));
				}
			}
		}

		if (row - 1 >= 0 && col - 1 >= 0) {
			if (contains(opponentPieces, board[row - 1][col - 1])) {
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

	private ArrayList<Move> findRookCaptures(int row, int col, char[] opponentPieces) {
		// System.out.println("generateRookMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int i = row + 1; i <= 7; i++) {
			if (contains(opponentPieces, board[i][col])) {
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
			if (contains(opponentPieces, board[i][col])) {
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
			if (contains(opponentPieces, board[row][i])) {
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
			if (contains(opponentPieces, board[row][i])) {
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

	private ArrayList<Move> findBishopCaptures(int row, int col, char[] opponentPieces) {
		// System.out.println("generateBishopMoves");
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (contains(opponentPieces, board[i][j])) {
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
			if (contains(opponentPieces, board[i][j])) {
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
			if (contains(opponentPieces, board[i][j])) {
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
			if (contains(opponentPieces, board[i][j])) {
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

	private ArrayList<Move> findQueenCaptures(int row, int col, char[] opponentPieces) {
		ArrayList<Move> captures = new ArrayList<Move>();
		captures.addAll(findBishopCaptures(row, col, opponentPieces));
		captures.addAll(findRookCaptures(row, col, opponentPieces));
		return captures;

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

	private ArrayList<Move> findKnightCaptures(int row, int col, char[] opponentPieces) {
		// System.out.println("generateKnightMoves");
		ArrayList<Move> moves = new ArrayList<Move>();
		if (row + 2 <= 7 && col + 1 <= 7) {
			if (contains(opponentPieces, board[row + 2][col + 1])) {
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
			if (contains(opponentPieces, board[row + 2][col - 1])) {
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
			if (contains(opponentPieces, board[row + 1][col + 2])) {
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
			if (contains(opponentPieces, board[row + 1][col - 2])) {
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
			if (contains(opponentPieces, board[row - 1][col + 2])) {
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
			if (contains(opponentPieces, board[row - 1][col - 2])) {
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
			if (contains(opponentPieces, board[row - 2][col + 1])) {
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
			if (contains(opponentPieces, board[row - 2][col - 1])) {
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

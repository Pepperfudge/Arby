package myPackage;

import java.util.ArrayList;
import java.util.Random;

public class Game {
	private char[][] board;

	public char sideToMove;

	private int[] whiteKingLocation;
	private int[] blackKingLocation;

	private boolean enPassant;
	private int enPassantTarget;

	private int whiteMaterialScore = 4000;
	private int blackMaterialScore = 4000;

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
		whiteKingLocation = new int[] {0, 3};
		blackKingLocation = new int[] {7, 3};
	}
	
	public char getPieceAt(int row, int col){
		return board[row][col];
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
						whiteKingLocation = new int[] {i,j};
					}
					if (board[i][j] == 'k') {
						blackKingLocation = new int[] {i,j};
					}
				}
			}
		}	
		else {
			whiteKingLocation = new int[] {prevPosition.whiteKingLocation[0], 
										   prevPosition.whiteKingLocation[1]};	
			blackKingLocation = new int[] {prevPosition.blackKingLocation[0],
										   prevPosition.blackKingLocation[1]};
		}

		// System.out.format("Turn to move: %s\n", sideToMove);

		if (prevPosition.board[move.newRow][move.newColumn] == 'Q') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 900;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'R') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 500;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'B') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 325;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'N') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore - 325;
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
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 225;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'R') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 400;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'P'
				&& this.board[move.newRow][move.newColumn] == 'B') {
			this.whiteMaterialScore = prevPosition.whiteMaterialScore + 225;
		}

		if (prevPosition.board[move.newRow][move.newColumn] == 'q') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 900;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'r') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 500;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'b') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 325;
		} else if (prevPosition.board[move.newRow][move.newColumn] == 'n') {
			this.blackMaterialScore = prevPosition.blackMaterialScore - 325;
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
			this.blackMaterialScore = prevPosition.blackMaterialScore + 225;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'r') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 400;
		}
		if (prevPosition.board[move.currRow][move.currColumn] == 'p'
				&& this.board[move.newRow][move.newColumn] == 'b') {
			this.blackMaterialScore = prevPosition.blackMaterialScore + 225;
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
						blackKingLocation = new int[]{rowIndex,colIndex};
						foundBlackKing = true;
					} else if (piece == 'K') {
						whiteKingLocation = new int[]{rowIndex,colIndex};
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

	public int evaluateBoard(){
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
		int whiteAttackingPieces = 0;
		int blackAttackingPieces = 0;
		int whiteDefendingPieces = 0;
		int blackDefendingPieces = 0;
		int whiteTradeBonus = 0;
		int blackTradeBonus = 0;
		int whiteBishopCount = 0;
		int blackBishopCount = 0;
				
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'x'){}
				else if (board[i][j] == 'P'){
					//Encourage pawn chains
					if (i>=3 && j>0 && j<7 && (board[i-1][j-1] == 'P' || board[i-1][j+1] == 'P')){
						whitePawnStructure = whitePawnStructure +5;
					}
					//passed pawns
					boolean whitePassedPawn = true;
					for (int k = i+1; k <=6; k++){						
						if (j-1>=0){
							if (board [k][j-1] == 'p')  {
								whitePassedPawn = false;
								break;
							}
						}	
						if (board [k][j] == 'p')  {
							whitePassedPawn = false;
							break;
						}
						if (j+1<=7){
							if	(board [k][j+1] == 'p'){
								whitePassedPawn = false;
								break;
							}
						}	
					}
					if (whitePassedPawn == true){whitePawnStructure = whitePawnStructure + 20;}
					//isolated pawns
					boolean whiteIsolatedPawn = true;
					for (int k = 1; k <=6; k++){
						if (j-1>=0){
							if (board [k][j-1] == 'P')  {
								whiteIsolatedPawn = false;
								break;
							}
						}
						if (j+1<=7){
							if	(board [k][j+1] == 'P'){
								whiteIsolatedPawn = false;
								break;
							}
						}	
					}	
					if (whiteIsolatedPawn == true){whitePawnStructure = whitePawnStructure - 15;}
					//Encourage pawn pushing in endgame
					if (blackMaterialScore < 2000){
						if (i==3){
							whitePawnStructure = whitePawnStructure + 5;
							if (whitePassedPawn == true){whitePawnStructure = whitePawnStructure + 5;} 
						}
						else if (i==4){
							whitePawnStructure = whitePawnStructure + 10;}
							if (whitePassedPawn == true){whitePawnStructure = whitePawnStructure + 10;} 
						else if (i==5){
							whitePawnStructure = whitePawnStructure + 20;
							if (whitePassedPawn == true){whitePawnStructure = whitePawnStructure + 30;} 
						}
						else if (i==6){whitePawnStructure = whitePawnStructure + 100;}	
					}
					//Discourage doubled pawns
					for (int k = i+1; k <=7; k++){
						if (board[k][j]=='P') {
						whitePawnStructure = whitePawnStructure - 15;
						}
					}
				}
				else if (board[i][j] == 'p'){
					if (i<=4 && j>0 && j<7 && (board[i+1][j-1] == 'p' || board[i+1][j+1] == 'p')){
						blackPawnStructure = blackPawnStructure + 5;
					}
					boolean blackIsolatedPawn = true;
					for (int k = 6; k >=1; k--){						
						if (j-1>=0){
							if (board [k][j-1] == 'p')  {
								blackIsolatedPawn = false;
								break;
							}
						}	
						if (j+1<=7){
							if	(board [k][j+1] == 'p'){
								blackIsolatedPawn = false;
								break;
							}
						}	
					}	
					if (blackIsolatedPawn == true){blackPawnStructure = blackPawnStructure - 15;}
					
					boolean blackPassedPawn = true;
					for (int k = i-1; k >=1; k--){
						if (j-1>=0){
							if (board [k][j-1] == 'P')  {
								blackPassedPawn = false;
								break;
							}
						}	
						if (board [k][j] == 'P')  {
							blackPassedPawn = false;
							break;
						}	
						if (j+1<=7){
							if	(board [k][j+1] == 'P'){
								blackPassedPawn = false;
								break;
							}
						}	
					}
					if (blackPassedPawn == true){blackPawnStructure = blackPawnStructure + 20;}
					
					if (whiteMaterialScore < 2000){
						if (i==4){blackPawnStructure = blackPawnStructure + 5;
							if (blackPassedPawn == true){blackPawnStructure = blackPawnStructure + 5;}
						}
						else if (i==3){blackPawnStructure = blackPawnStructure + 10;
							if (blackPassedPawn == true){blackPawnStructure = blackPawnStructure + 10;}
						}
						else if (i==2){blackPawnStructure = blackPawnStructure + 20;
							if (blackPassedPawn == true){blackPawnStructure = blackPawnStructure + 30;}
						}
						else if (i==1){blackPawnStructure = blackPawnStructure + 100;}	
					}
					for (int k = i-1; k >= 1; k--){
						if (board[k][j]=='p') {
						blackPawnStructure = blackPawnStructure - 15;
						}
					}
					
			    }
				else if (board[i][j] == 'B') {
						int bishopMoves = 0;
						int bishopCenterSquares = 0;
						int bishopAttackSquares = 0;
						
						//two bishops
						whiteBishopCount = whiteBishopCount + 1;
						if (whiteBishopCount == 2) {whiteBishopActivity = whiteBishopActivity + 20;}
						
						for (int k = i + 1, l = j + 1; k <= 7 && l <= 7; k++, l++) {
							if (board[k][l] == 'x')  {
								bishopMoves = bishopMoves+1;
							}	
							if ((k == 3 || k == 4 || k == 5) && (l == 2 || l == 3 || l == 4 || l == 5)){
								bishopCenterSquares = bishopCenterSquares + 1;		
							}
							if (blackKingLocation[1] <=3 && whiteMaterialScore > 1600 && (k == 5 || k == 6 || k == 7) && 
							(l == 2 || l == 1 || l == 0)){
								bishopAttackSquares = bishopAttackSquares + 1;
							}	
							else if (blackKingLocation[1] >=5 && whiteMaterialScore > 1600 && (k == 5 || k == 6 || k == 7) && 
							(l == 5 || l == 6 || l == 7)){
								bishopAttackSquares = bishopAttackSquares + 1;	
							}
							if (board[k][l] != 'x' && board[k][l] != 'N')  { break;}
						}	
						for (int k = i + 1, l = j - 1; k <=7 && l >= 0; k++, l--) {
							if (board[k][l] == 'x')  {
								bishopMoves = bishopMoves+1;
							}	
							if ((k == 3 || k == 4 || k == 5) && (l == 2 || l == 3 || l == 4 || l == 5)){
								bishopCenterSquares = bishopCenterSquares + 1;		
							}
							if (blackKingLocation[1] <=3 && whiteMaterialScore > 1600 && (k == 5 || k == 6 || k == 7) && 
							(l == 2 || l == 1 || l == 0)){
								bishopAttackSquares = bishopAttackSquares + 1;		
							}
							else if (blackKingLocation[1] >=5 && whiteMaterialScore > 1600 && (k == 5 || k == 6 || k == 7) && 
							(l == 5 || l == 6 || l == 7)){
								bishopAttackSquares = bishopAttackSquares + 1;	
							}
							if (board[k][l] != 'x' && board[k][l] != 'N')  { break;}
						}
						
						whiteBishopActivity = whiteBishopActivity + bishopAttackSquares*5 + bishopCenterSquares*5 + bishopMoves;
						
						if (i==3 && j==7 && board[5][5] == 'n'){whiteBishopActivity = whiteBishopActivity + 10; }
						else if (i==4 && j==6 && board[5][5] == 'n'){whiteBishopActivity = whiteBishopActivity + 10; }
						else if (i==4 && j==1 && board[5][2] == 'n'){whiteBishopActivity = whiteBishopActivity + 10; }
						else if (i==3 && j==0 && board[5][2] == 'n'){whiteBishopActivity = whiteBishopActivity + 10; }
						
						if (bishopAttackSquares >= 1){whiteAttackingPieces = whiteAttackingPieces + 1;} 
						
						if (j<=3 && whiteKingLocation[1] <= 3){whiteDefendingPieces = whiteDefendingPieces + 1;}
						else if (j>=4 && whiteKingLocation[1] >= 5){whiteDefendingPieces = whiteDefendingPieces + 1;}  
						 
						//Avoid trapped bishop
						if (i == 6 && j == 7 && board[5][6] == 'p' && board[6][5] == 'p') {
							whiteBishopActivity = whiteBishopActivity - 100;
						}
						//penalize bad bishop
						if ((i + j)%2 == 1 && board[3][4] == 'P' && board[2][3] == 'P' && board[4][4] == 'p'){
							whiteBishopActivity = whiteBishopActivity - 10;
							if (j > 2){whiteBishopActivity = whiteBishopActivity - 10;}
						}
						else if ((i + j)%2 == 0 && board[2][4] == 'P' && board[3][3] == 'P' && board[4][3] == 'p' ){
							whiteBishopActivity = whiteBishopActivity - 10;
							if (j < 5){whiteBishopActivity = whiteBishopActivity - 10;}	
						}
				}
				else if (board[i][j] == 'R') {
					boolean friendlyOpenFile = true;
					boolean enemyOpenFile = true;
					for (int k = i; k <=6; k++){
						if (board[k][j]=='P' ) {
							friendlyOpenFile = false;
						}
						if (board[k][j]=='p' ) {
							enemyOpenFile = false;
						}
					}
					if (friendlyOpenFile == true) {
						whiteRookActivity = whiteRookActivity + 15;
						if (enemyOpenFile == true) {whiteRookActivity = whiteRookActivity + 15;}
						if (j<=2 && blackKingLocation[1] <= 3){
							whiteAttackingPieces = whiteAttackingPieces + 1;
							blackKingSafety = blackKingSafety - 20;
							}
						else if (j>=5 && blackKingLocation[1] >= 5){
							whiteAttackingPieces = whiteAttackingPieces + 1;
							blackKingSafety = blackKingSafety - 20;
							}
					}	
					if (i==6){whiteRookActivity = whiteRookActivity + 20;}
				}
				
				else if (board[i][j] == 'N') {
					//discourage knight on rim
					if(i==0 || i==7){ whiteKnightActivity = whiteKnightActivity - 10;}
					else if(i==1 || i==6){ whiteKnightActivity = whiteKnightActivity - 5;}
					if(j==0 || j==7){ whiteKnightActivity = whiteKnightActivity - 10;}
					else if(j==1 || j==6){ whiteKnightActivity = whiteKnightActivity - 5;}
					
					if (Math.abs(i - blackKingLocation[0]) <= 3 && Math.abs(j - blackKingLocation[1]) <= 2){
						whiteAttackingPieces = whiteAttackingPieces + 1;
					} 
					
					if (Math.abs(i - whiteKingLocation[0]) <= 4 && Math.abs(j - whiteKingLocation[1]) <= 2){
					whiteDefendingPieces = whiteDefendingPieces + 1;
					}
					
					//Encourage support points
				}	
				else if (board[i][j] == 'Q') {
					if (whiteMaterialScore > 2500){ 
						//Discourage early queen expeditions
						if (board[0][1] == 'N' || board[0][2] == 'B' || board[0][5] == 'B' || board[0][6] == 'N'){
							if (i > 1){ whiteDevelopment = whiteDevelopment - 10; }
						}	
						//Encourage queen development
						if (board[0][1] != 'N' && board[0][2] != 'B' && board[0][5] != 'B' && board[0][6] != 'N'){
							if (i != 0){ whiteDevelopment = whiteDevelopment + 10; }
						}
					}	
					//is queen attacking king?
					if (j<=2 && i>=2 && blackKingLocation[1] <= 3){whiteAttackingPieces = whiteAttackingPieces + 1;}
					else if (j>=5 && i>=2 && blackKingLocation[1] >=5 ){whiteAttackingPieces = whiteAttackingPieces + 1;} 
					// is queen defending king?
					if (j<=3 && whiteKingLocation[1] <= 3){whiteDefendingPieces = whiteDefendingPieces + 1;}
					else if (j>=4 && whiteKingLocation[1] >= 5){whiteDefendingPieces = whiteDefendingPieces + 1;} 
				}	
				else if (board[i][j] == 'b') {
					int bishopMoves = 0;
					int bishopCenterSquares = 0;
					int bishopAttackSquares = 0;
					
					blackBishopCount = blackBishopCount + 1;
					if (blackBishopCount == 2) {blackBishopActivity = blackBishopActivity + 20;}
					
					for (int k = i - 1, l = j + 1; k >= 0 && l <= 7; k--, l++) {
						if (board[k][l] == 'x')  {
							bishopMoves = bishopMoves+1;
						}	
						if ((k == 3 || k == 4 || k == 2) && (l == 2 || l == 3 || l == 4 || l == 5)){
							bishopCenterSquares = bishopCenterSquares + 1;		
						}
						if (whiteKingLocation[1] <=3 && blackMaterialScore > 1600 && (k == 2 || k == 1 || k == 0) && 
						(l == 2 || l == 1 || l == 0)){
							bishopAttackSquares = bishopAttackSquares + 1;
						}	
						else if (whiteKingLocation[1] >=5 && blackMaterialScore > 1600 && (k == 2 || k == 1 || k == 0) && 
						(l == 5 || l == 6 || l == 7)){
							bishopAttackSquares = bishopAttackSquares + 1;	
						}
						if (board[k][l] != 'x' && board[k][l] != 'n')  { break;}
					}	
					for (int k = i - 1, l = j - 1; k >=0 && l >= 0; k--, l--) {
						if (board[k][l] == 'x')  {
							bishopMoves = bishopMoves+1;
						}	
						if ((k == 3 || k == 4 || k == 2) && (l == 2 || l == 3 || l == 4 || l == 5)){
							bishopCenterSquares = bishopCenterSquares + 1;		
						}
						if (whiteKingLocation[1] <=3 && blackMaterialScore > 1600 && (k == 2 || k == 1 || k == 0) && 
						(l == 2 || l == 1 || l == 0)){
							bishopAttackSquares = bishopAttackSquares + 1;		
						}
						else if (whiteKingLocation[1] >=5 && blackMaterialScore > 1600 && (k == 2 || k == 1 || k == 0) && 
						(l == 5 || l == 6 || l == 7)){
							bishopAttackSquares = bishopAttackSquares + 1;	
						}
						if (board[k][l] != 'x' && board[k][l] != 'n')  { break;}
					}
					blackBishopActivity = blackBishopActivity + bishopAttackSquares*5 + bishopCenterSquares*5 + bishopMoves;
					
					if (i==3 && j==6 && board[2][5] == 'N'){blackBishopActivity =	blackBishopActivity + 10; }
					else if (i==4 && j==7 && board[2][5] == 'N'){blackBishopActivity =	blackBishopActivity + 10; }
					else if (i==3 && j==1 && board[2][2] == 'N'){blackBishopActivity =	blackBishopActivity + 10; }
					else if (i==4 && j==0 && board[2][2] == 'N'){blackBishopActivity =	blackBishopActivity + 10; }
					
					if (bishopAttackSquares >= 1){blackAttackingPieces = blackAttackingPieces + 1;} 
					
					if (j<=3 && blackKingLocation[1] <= 3){blackDefendingPieces = blackDefendingPieces + 1;}
					else if (j>=4 && blackKingLocation[1] >= 5){blackDefendingPieces = blackDefendingPieces + 1;}
						
					//avoid trapped bishop
					if (i == 1 && j == 7 && board[2][6] == 'P' && board[1][5] == 'P') {
						blackBishopActivity = blackBishopActivity - 100;
					}
					//Penalize bad bishop
						if ((i + j)%2 == 1 && board[4][3] == 'p' && board[5][4] == 'p' && board[3][3] == 'P'){
							blackBishopActivity = blackBishopActivity - 10;
							if (j < 5){blackBishopActivity = blackBishopActivity - 10;}
						}
						else if ((i + j)%2 == 0 && board[5][3] == 'p' && board[4][4] == 'p' && board[3][4] == 'P' ){
							blackBishopActivity = blackBishopActivity - 10;
							if (j > 2){blackBishopActivity = blackBishopActivity - 10;}	
						}			
				}
				
				else if (board[i][j] == 'r') {
					boolean friendlyOpenFile = true;
					boolean enemyOpenFile = true;
					for (int k = i; k >=1; k--){
						if (board[k][j]=='p' ) {
							friendlyOpenFile = false;
						}
						if (board[k][j]=='P' ) {
							enemyOpenFile = false;
						}
					}
					if (friendlyOpenFile == true) {
						blackRookActivity = blackRookActivity + 15;
						if (enemyOpenFile == true) {blackRookActivity = blackRookActivity + 15;}
						if (j<=2 && whiteKingLocation[1] <= 3){
							blackAttackingPieces = blackAttackingPieces + 1;
							whiteKingSafety = whiteKingSafety - 20;
						}
						if (j>=5 && whiteKingLocation[1] >= 5){
							blackAttackingPieces = blackAttackingPieces + 1;
							whiteKingSafety = whiteKingSafety - 20;
						}
					}	
					if (i==1){blackRookActivity = blackRookActivity + 20;}
				}
				
				
				else if (board[i][j] == 'n') {
					if(i==0 || i==7){ blackKnightActivity = blackKnightActivity - 10;}
					else if(i==1 || i==6){ blackKnightActivity = blackKnightActivity - 5;}
					if(j==0 || j==7){ blackKnightActivity = blackKnightActivity - 10;}
					else if(j==1 || j==6){ blackKnightActivity = blackKnightActivity - 5;}
					
					//is knight attacking king?
					if (Math.abs(i - whiteKingLocation[0]) <= 3 && Math.abs(j - whiteKingLocation[1]) <= 2){
						blackAttackingPieces = blackAttackingPieces + 1;
					} 
					//is knight defending king?
					if (Math.abs(i - blackKingLocation[0]) <= 4 && Math.abs(j - blackKingLocation[1]) <= 2){
						blackDefendingPieces = blackDefendingPieces + 1;
					}
				}
				else if (board[i][j] == 'q') {
					if (blackMaterialScore > 2500){ 
						//Discourage early queen expeditions
						if (board[7][1] == 'n' || board[7][2] == 'b' || board[7][5] == 'b' || board[7][6] == 'N'){
							if (i < 6){ blackDevelopment = blackDevelopment - 10; }
						}	
						//Encourage queen development
						if (board[7][1] != 'n' && board[7][2] != 'b' && board[7][5] != 'b' && board[7][6] != 'n'){
							if (i != 7){ blackDevelopment = blackDevelopment + 10; }
						}
					}	
					//is queen attacking king?
					if (j<=2 && i<=5 && whiteKingLocation[1] <= 3){blackAttackingPieces = blackAttackingPieces + 1;}
					else if (j>=5 && i<=5 && whiteKingLocation[1] >= 5){blackAttackingPieces = blackAttackingPieces + 1;} 
					// is queen defending king?
					if (j<=3 && blackKingLocation[1] <= 3){blackDefendingPieces = blackDefendingPieces + 1;}
					else if (j>=4 && blackKingLocation[1] >= 5){blackDefendingPieces = blackDefendingPieces + 1;}
				}	
			}	
		}			
		
		//blackKingSafety
		if (whiteMaterialScore > 1600){
			//discourage pawn pushing on kingside before castling
			if (blackKingLocation[1] <4){
				if (board[6][1] != 'p' && board[5][1] != 'p'){blackKingSafety = blackKingSafety - 20;}
				if (board[6][1] != 'p' && board[6][1] != 'b'){blackKingSafety = blackKingSafety - 5;}
				if (board[6][2] != 'p'){blackKingSafety = blackKingSafety - 10;}
				if (board[6][0] != 'p' && board[5][0] != 'p'){blackKingSafety = blackKingSafety - 10;}
			}
			if (blackKingLocation[0] == 6) {blackKingSafety = blackKingSafety - 10;} 
			if (blackKingLocation[0] < 6) {blackKingSafety = blackKingSafety - 40;} 
			// encourage kingside castling
			if (blackKingLocation[1] < 2 && board[7][0] != 'r'){ 
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
				if (board[6][2] != 'p'){blackKingSafety = blackKingSafety - 5;}
			}
			// encourage queenside castling
			else if (blackKingLocation[1] > 4 && board[7][6] != 'r' && board[7][7] != 'r'){ 
				blackKingSafety = blackKingSafety + 30;
				if (blackKingLocation[1]==5) { 
					blackKingSafety = blackKingSafety - 5;
					if (board[6][5] != 'p' && board[5][5] != 'p'){blackKingSafety = blackKingSafety - 20;}
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
			|| blackKingLocation[1] == 2 || (blackKingLocation[1] == 5 && (board[7][7]=='r' || board[7][6]=='r') ) ) ) {
				blackKingSafety = blackKingSafety - 30;
				if (blackQCastle == false) {blackKingSafety = blackKingSafety - 30;}
			}
			if (whiteAttackingPieces >=3 && whiteAttackingPieces > blackDefendingPieces){
				blackKingSafety = blackKingSafety - 20*(whiteAttackingPieces - blackDefendingPieces);
			}
			if (whiteMaterialScore < 2400){ blackKingSafety = blackKingSafety/2;}
		}
		
		//whiteKingSafety
		if (blackMaterialScore > 1600){
			if (whiteKingLocation[1] <4){
				if (board[1][1] != 'P' && board[2][1] != 'P'){whiteKingSafety = whiteKingSafety - 20;}
				if (board[1][1] != 'P' && board[1][1] != 'B'){whiteKingSafety = whiteKingSafety - 5;}
				if (board[1][2] != 'P' ){whiteKingSafety = whiteKingSafety - 10;}
				if (board[1][0] != 'P' && board[2][0] != 'P'){whiteKingSafety = whiteKingSafety - 10;}
			}	
			if (whiteKingLocation[0] == 1) {whiteKingSafety = whiteKingSafety - 10;}  
			if (whiteKingLocation[0] > 1) {whiteKingSafety = whiteKingSafety - 40;}  
			if (whiteKingLocation[1] < 2 && board[0][0] != 'R'){ 
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
			else if (whiteKingLocation[1] > 4 && board[0][6] != 'R' && board[0][7] != 'R'){ 
				whiteKingSafety = whiteKingSafety + 30;
				if (whiteKingLocation[1] == 5) { 
					whiteKingSafety = whiteKingSafety - 5;
					if (board[1][5] != 'P' && board[2][5] != 'P'){whiteKingSafety = whiteKingSafety - 20;}
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
			|| whiteKingLocation[1] == 2) || (whiteKingLocation[1] == 5 && (board[0][7]=='R' || board[0][6] == 'R') ) ) ){
				whiteKingSafety = whiteKingSafety - 30;
				if (whiteQCastle == false) {whiteKingSafety = whiteKingSafety - 30;}
			}
			if (blackAttackingPieces >=3 && blackAttackingPieces > whiteDefendingPieces){
				whiteKingSafety = whiteKingSafety - 20*(blackAttackingPieces - whiteDefendingPieces);
			}
			if (blackMaterialScore < 2400){ whiteKingSafety = whiteKingSafety/2;}
		}
		
		//blackDevelopment
		if (blackMaterialScore > 2500){
			if (board[7][1] != 'n') { blackDevelopment = blackDevelopment + 10; }
			if (board[7][2] != 'b') { blackDevelopment = blackDevelopment + 15; }
			if (board[7][5] != 'b')	{ blackDevelopment = blackDevelopment + 15; }
			if (board[7][6] != 'n')	{ blackDevelopment = blackDevelopment + 10; }
			if (board[6][4] == 'p' && board[5][4] == 'b') { blackDevelopment = blackDevelopment - 15; }   
		}
		//whiteDevelopment
		if (whiteMaterialScore > 2500){
			if (board[0][1] != 'N') { whiteDevelopment = whiteDevelopment + 10; }
			if (board[0][2] != 'B') { whiteDevelopment = whiteDevelopment + 15; }
			if (board[0][5] != 'B')	{ whiteDevelopment = whiteDevelopment + 15; }
			if (board[0][6] != 'N')	{ whiteDevelopment = whiteDevelopment + 10; }
			if (board[1][4] == 'P' && board[2][4] == 'B'){ whiteDevelopment = whiteDevelopment - 15; }
		}
			
		//blackPawnStructure
		if (board[5][3]=='p') {
			blackPawnStructure = blackPawnStructure + 5;
		}
		if (board[5][4] =='p') {
			blackPawnStructure = blackPawnStructure + 5;
		}
		if (board[4][5] =='p') {
			blackPawnStructure = blackPawnStructure + 5;
		}
		if (board[4][3]=='p' || board[3][3] =='p' || board[2][3] =='p') {
			blackPawnStructure = blackPawnStructure + 10;
		}
		if (board[4][4] =='p' || board[3][4] == 'p' || board[2][4] == 'p') {
			blackPawnStructure = blackPawnStructure + 10;
		}
		if (board[3][3] =='p' && board[4][4] == 'p') {
			blackPawnStructure = blackPawnStructure + 20;
		}
		if (board[3][4] =='p' && (board[4][3] == 'p' ||  board[4][5] == 'p' )) {
			blackPawnStructure = blackPawnStructure + 10;
		}
		if (board[2][3] =='p' && board[3][4] == 'p') {
			blackPawnStructure = blackPawnStructure + 20;
		}
		if (board[2][4] =='p' && (board[3][3] == 'p' ||  board[3][5] == 'p' )) {
			blackPawnStructure = blackPawnStructure + 10;
		}
		
		//whitePawnStructure
		if (board[2][3]=='P') {
			whitePawnStructure = whitePawnStructure + 5;
		}
		if (board[2][4] =='P') {
			whitePawnStructure = whitePawnStructure + 5;
		}
		if (board[3][5] =='P') {
			whitePawnStructure = whitePawnStructure + 5;
		}
		if (board[4][3]=='P' || board[3][3] =='P' || board[5][3] =='P') {
			whitePawnStructure = whitePawnStructure + 10;
		}
		if (board[4][4] =='P' || board[3][4] == 'P' || board[5][4] == 'P') {
			whitePawnStructure = whitePawnStructure + 10;
		}
		if (board[4][3] =='P' && board[3][4] == 'P') {
			whitePawnStructure = whitePawnStructure + 20;
		}
		if (board[4][4] =='P' && (board[3][3] == 'P' ||  board[3][5] == 'P' )) {
			whitePawnStructure = whitePawnStructure + 10;
		}
		if (board[5][3] =='P' && board[4][4] == 'P') {
			whitePawnStructure = whitePawnStructure + 40;
		}
		if (board[5][4] =='P' && (board[4][3] == 'P' ||  board[4][5] == 'P' )) {
			whitePawnStructure = whitePawnStructure + 20;
		}
		
		whitePieceActivity = whiteKnightActivity + whiteRookActivity + whiteBishopActivity;
		blackPieceActivity = blackKnightActivity + blackRookActivity + blackBishopActivity;
		
		if (whiteMaterialScore - blackMaterialScore  >= 300) {whiteTradeBonus = (4000 - blackMaterialScore)/25;}
		if (blackMaterialScore - whiteMaterialScore  >= 300) {blackTradeBonus = (4000 - whiteMaterialScore)/25;}
		
		
		
		blackScore = blackMaterialScore + blackKingSafety + blackDevelopment + blackPawnStructure + blackPieceActivity + blackTradeBonus;
		whiteScore = whiteMaterialScore + whiteKingSafety + whiteDevelopment + whitePawnStructure + whitePieceActivity + whiteTradeBonus;
		
		//bareKingMating
		if (blackMaterialScore == 0){
			if (blackKingLocation[0] <= 2 || blackKingLocation[0] >= 5 || blackKingLocation[1] <= 2 || blackKingLocation[1] >= 5){
				whiteScore = whiteScore + 20;
			}
			if (blackKingLocation[0] <= 1 || blackKingLocation[0] >= 6 || blackKingLocation[1] <= 1 || blackKingLocation[1] >= 6){
				whiteScore = whiteScore + 20;
			}
			if (blackKingLocation[0] == 0 || blackKingLocation[0] == 7 || blackKingLocation[1] == 0 || blackKingLocation[1] == 7){
				whiteScore = whiteScore + 20;
			}
		}
		if (whiteMaterialScore == 0){
			if (whiteKingLocation[0] <= 2 || whiteKingLocation[0] >= 5 || whiteKingLocation[1] <= 2 || whiteKingLocation[1] >= 5){
				blackScore = blackScore + 20;
			}
			if (whiteKingLocation[0] <= 1 || whiteKingLocation[0] >= 6 || whiteKingLocation[1] <= 1 || whiteKingLocation[1] >= 6){
				blackScore = blackScore + 20;
			}
			if (whiteKingLocation[0] == 0 || whiteKingLocation[0] == 7 || whiteKingLocation[1] == 0 || whiteKingLocation[1] == 7){
				blackScore = blackScore + 20;
			}
		}
		
		positionScore = whiteScore - blackScore;
		
		return positionScore;
	}

	public ArrayList<Move> findCaptures() {
		// System.out.println("generateLegalMoves");
		//System.out.println("positionScore is " + evaluateBoard());
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
		// System.out.println("positionScore is " + evaluateBoard());
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
		ArrayList<Move> quietMoves = new ArrayList<Move>();
		
		moves.addAll(generateCastleMoves());

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'x'){}
				else if (board[i][j] == 'P') {
					generateWhitePawnMoves(i, j, moves, quietMoves);
				}
				else if (board[i][j] == 'R') {
					generateRookMoves(i, j, blackPieces, moves, quietMoves);
				}
				else if (board[i][j] == 'B') {
					generateBishopMoves(i, j, blackPieces, moves, quietMoves);
				}
				else if (board[i][j] == 'N') {
					generateKnightMoves(i, j, blackPieces, moves, quietMoves);
				}
				else if (board[i][j] == 'K') {
					generateKingMoves(i, j, blackPieces, moves, quietMoves);
				}
				else if (board[i][j] == 'Q') {
					generateQueenMoves(i, j, blackPieces, moves, quietMoves);
				}
			}
		}
		moves.addAll(generateCastleMoves());
		moves.addAll(quietMoves);
		
		return moves;
	}

	private ArrayList<Move> generateBlackMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		ArrayList<Move> quietMoves = new ArrayList<Move>();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 'x'){}
				else if (board[i][j] == 'p') {
					generateBlackPawnMoves(i, j, moves, quietMoves);
				}
				else if (board[i][j] == 'r') {
					generateRookMoves(i, j, whitePieces, moves, quietMoves);
				}
				else if (board[i][j] == 'b') {
					generateBishopMoves(i, j, whitePieces, moves, quietMoves);
				}
				else if (board[i][j] == 'n') {
					generateKnightMoves(i, j, whitePieces, moves, quietMoves);
				}
				else if (board[i][j] == 'k') {
					generateKingMoves(i, j, whitePieces, moves, quietMoves);
				}
				else if (board[i][j] == 'q') {
					generateQueenMoves(i, j, whitePieces, moves, quietMoves);
				}
			}
		}
		moves.addAll(generateCastleMoves());
		moves.addAll(quietMoves);

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

	private void generateBlackPawnMoves(int row, int col, ArrayList<Move> moves, ArrayList<Move> quietMoves) {
		// System.out.println("generatePawnMoves");
		
		if (row == 6 && board[5][col] == 'x' && board[4][col] == 'x') {
			Move interimMove = new Move(6, col, 4, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
				quietMoves.add(interimMove);
			}
		}

		if (board[row - 1][col] == 'x') {
			Move interimMove = new Move(row, col, row - 1, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
				if (row >=2 && col >=1 && whiteKingLocation[0] == row - 2 && whiteKingLocation[1] == col -1){
					quietMoves.add(0, interimMove);
				}
				else if (row >=2 && col <=6 && whiteKingLocation[0] == row -2 && whiteKingLocation[1] == col +1){
					quietMoves.add(0, interimMove);
				}
				else{
					quietMoves.add(interimMove);
				}
			}
		}

		moves.addAll(0, findBlackPawnCaptures(row, col));

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

	private void generateWhitePawnMoves(int row, int col, ArrayList<Move> moves, ArrayList<Move> quietMoves) {
		// System.out.println("generatePawnMoves");
		if (row == 1 && board[2][col] == 'x' && board[3][col] == 'x') {
			Move interimMove = new Move(1, col, 3, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
				quietMoves.add(new Move(1, col, 3, col));
			}
		}

		if (board[row + 1][col] == 'x') {
			Move interimMove = new Move(row, col, row + 1, col);
			Game nextPosition = new Game(this, interimMove);
			if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
				if (row <=5 && col >=1 && blackKingLocation[0] == row + 2 && blackKingLocation[1] == col - 1){
						quietMoves.add(0, interimMove);
				}	
				else if (row <=5 && col <=6 && blackKingLocation[0] == row +2 && blackKingLocation[1] == col +1){
						quietMoves.add(0, interimMove);
				}	
				else{
					quietMoves.add(interimMove);
				}
			}	
		}
		moves.addAll(0, findWhitePawnCaptures(row, col));
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

	private void generateKingMoves(int row, int col, char[] opponentPieces, ArrayList<Move> moves, ArrayList<Move> quietMoves) {
		// System.out.println("generateKingMoves");
		if (col + 1 <= 7) {
			if (board[row][col + 1] == 'x') {
				if (isKingInCheck(row, col + 1, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row, col + 1));
				}
			}
			else if (contains(opponentPieces, board[row][col + 1])) {
				if (isKingInCheck(row, col + 1, sideToMove) == false) {
					moves.add(0, new Move(row, col, row, col + 1));
				}
			}	
		}
		if (col - 1 >= 0) {
			if (board[row][col - 1] == 'x') {
				if (isKingInCheck(row, col - 1, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row, col - 1));
				}
			}
			else if (contains(opponentPieces, board[row][col - 1])) {
				if (isKingInCheck(row, col - 1, sideToMove) == false) {
					moves.add(0, new Move(row, col, row, col - 1));
				}
			}
		}
		if (row + 1 <= 7) {
			if (board[row + 1][col] == 'x') {
				if (isKingInCheck(row + 1, col, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row + 1, col));
				}
			}
			else if (contains(opponentPieces, board[row + 1][col])) {
				if (isKingInCheck(row + 1, col, sideToMove) == false) {
					moves.add(0, new Move(row, col, row + 1, col));
				}
			}	
		}
		if (row - 1 >= 0) {
			if (board[row - 1][col] == 'x') {
				if (isKingInCheck(row - 1, col, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row - 1, col));
				}
			}
			else if (contains(opponentPieces, board[row - 1][col])) {
				if (isKingInCheck(row - 1, col, sideToMove) == false) {
					moves.add(0, new Move(row, col, row - 1, col));
				}
			}	
		}
		if (row + 1 <= 7 && col + 1 <= 7) {
			if (board[row + 1][col + 1] == 'x') {
				if (isKingInCheck(row + 1, col + 1, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row + 1, col + 1));
				}
			}
			else if (contains(opponentPieces, board[row + 1][col + 1])) {
				if (isKingInCheck(row + 1, col + 1, sideToMove) == false) {
					moves.add(0, new Move(row, col, row + 1, col + 1));
				}
			}
			
		}
		if (row + 1 <= 7 && col - 1 >= 0) {
			if (board[row + 1][col - 1] == 'x') {
				if (isKingInCheck(row + 1, col - 1, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row + 1, col - 1));
				}
			}
			else if (contains(opponentPieces, board[row + 1][col - 1])) {
				if (isKingInCheck(row + 1, col - 1, sideToMove) == false) {
					moves.add(0, new Move(row, col, row + 1, col - 1));
				}
			}	
		}
		if (row - 1 >= 0 && col + 1 <= 7) {
			if (board[row - 1][col + 1] == 'x') {
				if (isKingInCheck(row - 1, col + 1, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row - 1, col + 1));
				}
			}
			else if (contains(opponentPieces, board[row - 1][col + 1])) {
				if (isKingInCheck(row - 1, col + 1, sideToMove) == false) {
					moves.add(0, new Move(row, col, row - 1, col + 1));
				}
			}	
		}

		if (row - 1 >= 0 && col - 1 >= 0) {
			if (board[row - 1][col - 1] == 'x') {
				if (isKingInCheck(row - 1, col - 1, sideToMove) == false) {
					quietMoves.add(new Move(row, col, row - 1, col - 1));
				}
			}
			else if (contains(opponentPieces, board[row - 1][col - 1])) {
				if (isKingInCheck(row - 1, col - 1, sideToMove) == false) {
					moves.add(0, new Move(row, col, row - 1, col - 1));
				}
			}
			
		}
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

	private void generateRookMoves(int row, int col, char[] opponentPieces, ArrayList<Move> moves, ArrayList<Move> quietMoves) {
		// System.out.println("generateRookMoves");
		int kingRow = 0; int kingColumn = 0; int targetKingRow = 0; int targetKingColumn = 0;
		if (sideToMove == 'w'){
			kingRow = whiteKingLocation[0]; kingColumn = whiteKingLocation[1];
			targetKingRow = blackKingLocation[0]; targetKingColumn = blackKingLocation[1];
		}
		else {
			kingRow = blackKingLocation[0]; kingColumn = blackKingLocation[1];
			targetKingRow = whiteKingLocation[0]; targetKingColumn = whiteKingLocation[1];
		}
		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'x') {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}	
			}
			else if (contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][col] == 'Q' || board[i][col] == 'q' || board[i][col] == 'R' || board[i][col] == 'r') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'x') {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][col] == 'Q' || board[i][col] == 'q' || board[i][col] == 'R' || board[i][col] == 'r') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'x') {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row][i] == 'Q' || board[row][i] == 'q' || board[row][i] == 'R' || board[row][i] == 'r') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[row][i] != 'x') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'x') {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row][i] == 'Q' || board[row][i] == 'q' || board[row][i] == 'R' || board[row][i] == 'r') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
				
			if (board[row][i] != 'x') {
				break;
			}
		}
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
						if (board[i][col] ==  'q' || board[i][col] == 'r') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}	
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][col] == 'Q' || board[i][col] == 'R') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}	
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
						if (board[i][col] == 'q' || board[i][col] == 'r') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}	
					}
				}
				else{
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][col] == 'Q' || board[i][col] == 'R') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row][i] == 'q' || board[row][i] == 'r') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else{
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row][i] == 'Q' || board[row][i] == 'R') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row][i] == 'q' || board[row][i] == 'r') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row][i] == 'Q' || board[row][i] == 'R') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
			}
			if (board[row][i] != 'x') {
				break;
			}
		}

		return moves;
	}

	private void generateBishopMoves(int row, int col, char[] opponentPieces, ArrayList<Move> moves, ArrayList<Move> quietMoves) {
		// System.out.println("generateBishopMoves");
		int kingRow = 0; int kingColumn = 0; int targetKingRow = 0; int targetKingColumn = 0;
		if (sideToMove == 'w'){
			kingRow = whiteKingLocation[0]; kingColumn = whiteKingLocation[1];
			targetKingRow = blackKingLocation[0]; targetKingColumn = blackKingLocation[1];
		}
		else {
			kingRow = blackKingLocation[0]; kingColumn = blackKingLocation[1];
			targetKingRow = whiteKingLocation[0]; targetKingColumn = whiteKingLocation[1];
		}
		
		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] != 'P' && board[i][j] != 'p' ) { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++, j--) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] != 'P' && board[i][j] != 'p' ) { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] != 'P' && board[i][j] != 'p' ) { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] != 'P' && board[i][j] != 'p' ) { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][j] != 'x') {
				break;
			}
		}
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
						if (board[i][j] != 'p' ) { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[i][j] != 'p' ) { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[i][j] != 'p' ) { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[i][j] != 'p' ) { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}

		return moves;
	}
	
	private void generateQueenMoves(int row, int col, char[] opponentPieces, ArrayList<Move> moves, ArrayList<Move> quietMoves) {
		// System.out.println("generateQueenMoves");
		int kingRow = 0; int kingColumn = 0; int targetKingRow = 0; int targetKingColumn = 0;
		if (sideToMove == 'w'){
			kingRow = whiteKingLocation[0]; kingColumn = whiteKingLocation[1];
			targetKingRow = blackKingLocation[0]; targetKingColumn = blackKingLocation[1];
		}
		else {
			kingRow = blackKingLocation[0]; kingColumn = blackKingLocation[1];
			targetKingRow = whiteKingLocation[0]; targetKingColumn = whiteKingLocation[1];
		}
		
		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] == 'Q' && board[i][j] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++, j--) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] == 'Q' && board[i][j] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] == 'Q' && board[i][j] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'x') {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (Math.abs(targetKingRow - i) == Math.abs(targetKingColumn - j)){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][j] == 'Q' && board[i][j] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][j] != 'x') {
				break;
			}
		}
		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'x') {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}	
			}
			else if (contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][col] == 'Q' || board[i][col] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'x') {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[i][col] == 'Q' || board[i][col] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
		
			if (board[i][col] != 'x') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'x') {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row][i] == 'Q' || board[row][i] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}
			
			if (board[row][i] != 'x') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'x') {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (targetKingRow == i || targetKingColumn == col){
						quietMoves.add(0, interimMove);
					}
					else {quietMoves.add(interimMove);
					}
				}
			}
			else if (contains(opponentPieces, board[row][i])) {
				Move interimMove = new Move(row, col, row, i);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row][i] == 'Q' || board[row][i] == 'q') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
				
			if (board[row][i] != 'x') {
				break;
			}
		}
	}

	private ArrayList<Move> findQueenCaptures(int row, int col, char[] opponentPieces) {
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int i = row + 1; i <= 7; i++) {
			if (contains(opponentPieces, board[i][col])) {
				Move interimMove = new Move(row, col, i, col);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						if (board[i][col] ==  'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}	
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][col] == 'Q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}	
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
						if (board[i][col] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}	
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][col] == 'Q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row][i] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row][i] == 'Q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row][i] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row][i] == 'Q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
			}
			if (board[row][i] != 'x') {
				break;
			}
		}
		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (contains(opponentPieces, board[i][j])) {
				Move interimMove = new Move(row, col, i, j);
				Game nextPosition = new Game(this, interimMove);
				if (opponentPieces == blackPieces) {
					if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
						if (board[i][j] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] == 'Q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[i][j] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] == 'Q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[i][j] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] == 'Q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[i][j] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[i][j] == 'q') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
			}
			if (board[i][j] != 'x') {
				break;
			}
		}
		return moves;
	}

	private void generateKnightMoves(int row, int col, char[] opponentPieces, ArrayList<Move> moves, ArrayList<Move> quietMoves) {
		// System.out.println("generateKnightMoves");
		int kingRow = 0; int kingColumn = 0; int targetKingRow = 0; int targetKingColumn = 0;
		if (sideToMove == 'w'){
			kingRow = whiteKingLocation[0]; kingColumn = whiteKingLocation[1];
			targetKingRow = blackKingLocation[0]; targetKingColumn = blackKingLocation[1];
		}
		else {
			kingRow = blackKingLocation[0]; kingColumn = blackKingLocation[1];
			targetKingRow = whiteKingLocation[0]; targetKingColumn = whiteKingLocation[1];
		}
		if (row + 2 <= 7 && col + 1 <= 7) {
			if (board[row + 2][col + 1] == 'x') {
				Move interimMove = new Move(row, col, row + 2, col + 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row + 2) - kingRow) == 2 && Math.abs((col+1) - kingColumn) == 1) 
					|| (Math.abs((row + 2) - kingRow) == 1 && Math.abs((col+1) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
				}
			}
			else if (contains(opponentPieces, board[row + 2][col + 1])) {
				Move interimMove = new Move(row, col, row + 2, col + 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row+2][col+1] != 'P' && board[row+2][col+1] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
		}
		if (row + 2 <= 7 && col - 1 >= 0) {
			if (board[row + 2][col - 1] == 'x') {
				Move interimMove = new Move(row, col, row + 2, col - 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row + 2) - kingRow) == 2 && Math.abs((col-1) - kingColumn) == 1) 
					|| (Math.abs((row + 2) - kingRow) == 1 && Math.abs((col-1) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
					}
			}
			else if (contains(opponentPieces, board[row + 2][col - 1])) {
				Move interimMove = new Move(row, col, row + 2, col - 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row+2][col-1] != 'P' && board[row+2][col-1] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}	
			}	
		}
		if (row + 1 <= 7 && col + 2 <= 7) {
			if (board[row + 1][col + 2] == 'x') {
				Move interimMove = new Move(row, col, row + 1, col + 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row + 1) - kingRow) == 2 && Math.abs((col+2) - kingColumn) == 1) 
					|| (Math.abs((row + 1) - kingRow) == 1 && Math.abs((col+2) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
					}
			}
			else if (contains(opponentPieces, board[row + 1][col + 2])) {
				Move interimMove = new Move(row, col, row + 1, col + 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row+1][col+2] != 'P' && board[row+1][col+2] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
		}
		if (row + 1 <= 7 && col - 2 >= 0) {
			if (board[row + 1][col - 2] == 'x') {
				Move interimMove = new Move(row, col, row + 1, col - 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row + 1) - kingRow) == 2 && Math.abs((col-2) - kingColumn) == 1) 
					|| (Math.abs((row + 1) - kingRow) == 1 && Math.abs((col-2) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
					}
			}
			else if (contains(opponentPieces, board[row + 1][col - 2])) {
				Move interimMove = new Move(row, col, row + 1, col - 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row+1][col-2] != 'P' && board[row+1][col-2] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
		}
		if (row - 1 >= 0 && col + 2 <= 7) {
			if (board[row - 1][col + 2] == 'x') {
				Move interimMove = new Move(row, col, row - 1, col + 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row - 1) - kingRow) == 2 && Math.abs((col+2) - kingColumn) == 1) 
					|| (Math.abs((row - 1) - kingRow) == 1 && Math.abs((col+2) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
					}
			}
			else if (contains(opponentPieces, board[row - 1][col + 2])) {
				Move interimMove = new Move(row, col, row - 1, col + 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row-1][col+2] != 'P' && board[row-1][col+2] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						quietMoves.add(interimMove);
					}
				}
			}
			
		}
		if (row - 1 >= 0 && col - 2 >= 0) {
			if (board[row - 1][col - 2] == 'x') {
				Move interimMove = new Move(row, col, row -1, col - 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row - 1) - kingRow) == 2 && Math.abs((col-2) - kingColumn) == 1) 
					|| (Math.abs((row - 1) - kingRow) == 1 && Math.abs((col-2) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
					}
			}
			else if (contains(opponentPieces, board[row - 1][col - 2])) {
				Move interimMove = new Move(row, col, row - 1, col - 2);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row-1][col-2] != 'P' && board[row-1][col-2] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
		}
		if (row - 2 >= 0 && col + 1 <= 7) {
			if (board[row - 2][col + 1] == 'x') {
				Move interimMove = new Move(row, col, row - 2, col + 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row - 2) - kingRow) == 2 && Math.abs((col+1) - kingColumn) == 1) 
					|| (Math.abs((row - 2) - kingRow) == 1 && Math.abs((col+1) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
					}
			}
			else if (contains(opponentPieces, board[row - 2][col + 1])) {
				Move interimMove = new Move(row, col, row - 2, col + 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row-2][col+1] != 'P' && board[row-2][col+1] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
		}
		if (row - 2 >= 0 && col - 1 >= 0) {
			if (board[row - 2][col - 1] == 'x') {
				Move interimMove = new Move(row, col, row - 2, col - 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if ((Math.abs((row - 2) - kingRow) == 2 && Math.abs((col-1) - kingColumn) == 1) 
					|| (Math.abs((row - 2) - kingRow) == 1 && Math.abs((col-1) - kingColumn) == 2)){	
						quietMoves.add(0, interimMove);
					}	
					else {quietMoves.add(interimMove);}
					}
			}
			else if (contains(opponentPieces, board[row - 2][col - 1])) {
				Move interimMove = new Move(row, col, row - 2, col - 1);
				Game nextPosition = new Game(this, interimMove);
				if (nextPosition.isKingInCheck(kingRow, kingColumn, sideToMove) == false) {
					if (board[row-2][col-1] != 'P' && board[row-2][col-1] != 'p') { 	
						moves.add(0, interimMove);
					}
					else {
						moves.add(interimMove);
					}
				}
			}	
		}
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
						if (board[row+2][col+1] != 'p') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row+2][col+1] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row+2][col-1] != 'p') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row+2][col-1] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row+1][col+2] != 'p') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row+1][col+2] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row+1][col-2] != 'p') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row+1][col-2] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row-1][col+2] != 'p') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row-1][col+2] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row-1][col-2] != 'p') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
							if (board[row-1][col-2] != 'P') { 	
								moves.add(0, interimMove);
							}
							else {
								moves.add(interimMove);
							}
						}
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
						if (nextPosition.isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]) == false) {
							if (board[row-2][col+1] != 'p') { 	
								moves.add(0, interimMove);
							}
							else {
								moves.add(interimMove);
							}
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row-2][col+1] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
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
						if (board[row-2][col-1] != 'p') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
				else {
					if (nextPosition.isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]) == false) {
						if (board[row-2][col-1] != 'P') { 	
							moves.add(0, interimMove);
						}
						else {
							moves.add(interimMove);
						}
					}
				}
			}
		}
		return moves;
	}

	public boolean isKingInCheck(){
		if (sideToMove == 'w'){
			return isWhiteKingInCheck(whiteKingLocation[0], whiteKingLocation[1]);
		} else {
			return isBlackKingInCheck(blackKingLocation[0], blackKingLocation[1]);
		}
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
			}  
			else if (board[i][j] != 'x' && board[i][j] != 'K') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			}  
			else if (board[i][j] != 'x' && board[i][j] != 'K') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			}  
			else if (board[i][j] != 'x' && board[i][j] != 'K') {
				break;
			}
		}
		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'q' || board[i][j] == 'b') {
				return true;
			}  
			else if (board[i][j] != 'x' && board[i][j] != 'K') {
				break;
			}
		}
		// Rows and columns test
		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'q' || board[i][col] == 'r') {
				return true;
			}  
			else if (board[i][col] != 'x' && board[i][col] != 'K') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'q' || board[i][col] == 'r') {
				return true;
			} 
			else if (board[i][col] != 'x' && board[i][col] != 'K') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'q' || board[row][i] == 'r') {
				return true;
			}  
			else if (board[row][i] != 'x' && board[row][i] != 'K') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'q' || board[row][i] == 'r') {
				return true;
			} 
			else if (board[row][i] != 'x' && board[row][i] != 'K') {
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
			} 
			else if (board[i][j] != 'x' && board[i][j] != 'k') {
				break;
			}
		}
		for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--, j++) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			} 
			else if (board[i][j] != 'x' && board[i][j] != 'k') {
				break;
			}
		}
		for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			}  
			else if (board[i][j] != 'x' && board[i][j] != 'k') {
				break;
			}
		}
		for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++, j++) {
			if (board[i][j] == 'Q' || board[i][j] == 'B') {
				return true;
			}  
			else if (board[i][j] != 'x' && board[i][j] != 'k') {
				break;
			}
		}
		// Rows and columns test
		for (int i = row + 1; i <= 7; i++) {
			if (board[i][col] == 'Q' || board[i][col] == 'R') {
				return true;
			} 
			else if (board[i][col] != 'x' && board[i][col] != 'k') {
				break;
			}
		}
		for (int i = row - 1; i >= 0; i--) {
			if (board[i][col] == 'Q' || board[i][col] == 'R') {
				return true;
			} 
			else if (board[i][col] != 'x' && board[i][col] != 'k') {
				break;
			}
		}
		for (int i = col + 1; i <= 7; i++) {
			if (board[row][i] == 'Q' || board[row][i] == 'R') {
				return true;
			} 
			else if (board[row][i] != 'x' && board[row][i] != 'k') {
				break;
			}
		}
		for (int i = col - 1; i >= 0; i--) {
			if (board[row][i] == 'Q' || board[row][i] == 'R') {
				return true;
			} 
			else if (board[row][i] != 'x' && board[row][i] != 'k') {
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

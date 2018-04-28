import java.util.Random;
import java.util.Scanner;

public class Game {

	// GAME OPTIONS
	private static final int ROWSXCOLS = 4;
	private static final int SIZE = ROWSXCOLS * ROWSXCOLS;
	private static final boolean RANDOMGENERATOR = true;
	private static final String GAMEOVER = "*************\n"
										 + "* GAME OVER *\n"
										 + "*************";
	
	private Node[][] board;
	private Random random;
	private Scanner keyboard;
	private boolean isMoving;
	
	public Game() {
		gameInit();
		gameLoop();
	}
	
	public static void main(String[] args) {
		new Game();
	}
	
	/* Prepares the game to run before the player
	 * takes their first turn
	 */
	public void gameInit() {
		board = new Node[ROWSXCOLS][ROWSXCOLS];
		keyboard = new Scanner(System.in);
		random = new Random();
		
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				board[i][j] = new Node();
			}
		}
		
		//Initialize first Node to the game board
		initializeNewNode();
		
	}
	
	/* This method checks for the Game Over state, calls the method for
	 * initializing a new Node each turn, and prints the board.
	 */
	public void gameLoop() {
		while(true) {
			if(initializeNewNode() || canMove()) {
				printBoard();
				playerTurn();
			} else {
				System.out.println(GAMEOVER);
				break;
			}
		}
	}
	
	/* Prints the current state of the game board
	 */
	public void printBoard() {
		for(int i = 0; i < ROWSXCOLS; i++) {
			String line = "";
			for(int j = 0; j < ROWSXCOLS; j++) {
				if(board[i][j].getValue() != -1) {
					line += String.format("[%4d]", board[i][j].getValue());
				} else {
					line += "[    ]";
				}
			}
			
			System.out.println(line);
			
		}
	}
	
	/* Determines the direction to move the Nodes and calls the proper move
	 * method.
	 */
	public void playerTurn() {
		String move = keyboard.nextLine();
		switch(move.trim().toUpperCase()) {
			case "U":
			case "UP":
				moveUP();
				break;
			case "D":
			case "DOWN":
				moveDOWN();
				break;
			case "L":
			case "LEFT":
				moveLEFT();
				break;
			case "R":
			case "RIGHT":
				moveRIGHT();
				break;
		}
		
	}
	
	/* Moves the Nodes to the top of the board and merges like Nodes
	 */
	public void moveUP() {
		do {
			isMoving = false; //Assume no moves have been made
			
			for(int i = ROWSXCOLS - 1; i > 0; i--) {
				for(int j = 0; j < ROWSXCOLS; j++) {
					
					//If two adjacent Nodes have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Nodes
					   board[i][j].getValue() == board[i - 1][j].getValue()) {
						board[i - 1][j].merge(board[i][j]);
						isMoving = true;
					} 
					
					//If an active Node is adjacent to an empty Node
					//the active Node switches places with the empty Node
					else if(board[i][j].getValue() > 0 &&
							  board[i - 1][j].getValue() == -1){
						board[i - 1][j].setValue(board[i][j].getValue());
						board[i][j].reset();
						isMoving = true;
					}
				}
			}
		} 
		//If a move was made there may be more moves that can be made
		//due to new Nodes becoming empty.
		while (isMoving);
	}
	
	/* Moves the Nodes to the bottom of the board and merges like Nodes
	 */
	public void moveDOWN() {
		do {
			isMoving = false; //Assume no moves have been made
			for(int i = 0; i < ROWSXCOLS - 1; i++) {
				for(int j = 0; j < ROWSXCOLS; j++) {
					
					//If two adjacent Nodes have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Nodes
					   board[i][j].getValue() == board[i + 1][j].getValue()) {
						board[i + 1][j].merge(board[i][j]);
						isMoving = true;
					} 
					
					//If an active Node is adjacent to an empty Node
					//the active Node switches places with the empty Node
					else if(board[i][j].getValue() > 0 &&
							  board[i + 1][j].getValue() == -1){
						board[i + 1][j].setValue(board[i][j].getValue());
						board[i][j].reset();
						isMoving = true;
					}
				}
			}
		} 
		//If a move was made there may be more moves that can be made
		//due to new Nodes becoming empty.
		while (isMoving);
	}
	
	/* Moves the Nodes to the left of the board and merges like Nodes
	 */
	public void moveLEFT() {
		do {
			isMoving = false; //Assume no moves have been made
			for(int i = 0; i < ROWSXCOLS; i++) {
				for(int j = ROWSXCOLS - 1; j > 0; j--) {
					
					//If two adjacent Nodes have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Nodes
					   board[i][j].getValue() == board[i][j - 1].getValue()) {
						board[i][j - 1].merge(board[i][j]);
						isMoving = true;
					} 
					
					//If an active Node is adjacent to an empty Node
					//the active Node switches places with the empty Node
					else if(board[i][j].getValue() > 0 &&
							  board[i][j - 1].getValue() == -1){
						board[i][j - 1].setValue(board[i][j].getValue());
						board[i][j].reset();
						isMoving = true;
					}
				}
			}
		} 
		//If a move was made there may be more moves that can be made
		//due to new Nodes becoming empty.
		while (isMoving);
	}
	
	/* Moves the Nodes to the right of the board and merges like Nodes
	 */
	public void moveRIGHT() {
		do {
			isMoving = false; //Assume no moves have been made
			for(int i = 0; i < ROWSXCOLS; i++) {
				for(int j = 0; j < ROWSXCOLS - 1; j++) {
					
					//If two adjacent Nodes have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Nodes
					   board[i][j].getValue() == board[i][j + 1].getValue()) {
						board[i][j + 1].merge(board[i][j]);
						isMoving = true;
					} 
					
					//If an active Node is adjacent to an empty Node
					//the active Node switches places with the empty Node
					else if(board[i][j].getValue() > 0 &&
							  board[i][j + 1].getValue() == -1){
						board[i][j + 1].setValue(board[i][j].getValue());
						board[i][j].reset();
						isMoving = true;
					}
				}
			}
		} 
		//If a move was made there may be more moves that can be made
		//due to new Nodes becoming empty.
		while (isMoving);
	}
	
	/* Checks to see if there is at least one move where 
	 * at least one Node will call reset()
	 */
	public boolean canMove() {
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				//Check if there is an empty Node at board[i][j]
				if(board[i][j].getValue() == -1) {
					return true;
				}
				//Check if Node at board[i][j] will call reset()
				if((i + 1 < ROWSXCOLS) && (j + 1 < ROWSXCOLS) && //Don't check outer edge Nodes
					(board[i][j].getValue() > 0) &&				 //Don't check empty Nodes
					(board[i][j].getValue() == board[i + 1][j].getValue() ||
					 board[i][j].getValue() == board[i][j + 1].getValue())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/* Randomly initializes a new Node in one of
	 * the available spaces on the game board
	 */
	public boolean initializeNewNode() {
		Node newNode;
		int locx, locy, selection, empty = 0;
		int[] emptyNodes = new int[SIZE];
		
		// Make a list of all of the empty nodes
		// Nodes are numbered from left to right, and top to bottom
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				if(board[i][j].getValue() == -1) {
					emptyNodes[empty++] = (i * ROWSXCOLS) + j;
				}
			}
		}
		
		// Verify that there is space for a new Node
		if(empty > 1) {

			// If there is space for a new Node and there
			// is more than one space for the new Node,
			// select a random Node from the list of empty Nodes
			selection = emptyNodes[random.nextInt(empty)];
			
		}else if(empty == 1) {
			
			// If there is space for a new Node but
			// there is only one space left the
			// selection becomes the only available space
			selection = emptyNodes[0];
		}
		else {
			// Return false, no space for new Node
			return false;
		}

		// Get the x location and y location from the selection
		locy = selection % ROWSXCOLS;
		locx = selection / ROWSXCOLS;
		
		// Initialize a new Node at (x,y)
		if(RANDOMGENERATOR) {
			newNode = new Node((random.nextInt(2) + 1) * 2);
		} else {
			newNode = new Node(2);
		}
		
		// Add the new Node to the board
		board[locx][locy] = newNode;
		
		// Return true, new Node initialized successfully
		return true;
	}
}
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Game extends Application{

	// GAME OPTIONS
	private static final int ROWSXCOLS = 4;
	private static final int SIZE = ROWSXCOLS * ROWSXCOLS;
	private static final int WIDTH = 300;
	private static final int HEIGHT = 200;
	private static final boolean RANDOMGENERATOR = true;
	private static final String GAMEOVER = "*************\n"
										 + "* GAME OVER *\n"
										 + "*************";
	
	// Game Variables
	private Node[][] board;
	private Random random;
	private boolean isMoving;
	
	// Scene Variables
	private Text boardText;
	private Button upButton,
		   		   downButton,
		   		   leftButton,
		   		   rightButton;
	Font f = new Font("Courier New", 16);
	
	@Override
	/* Prepares the game for play by calling the sceneInit and
	 * gameInit methods.
	 */
	public void start(Stage arg0) throws Exception {
		sceneInit(arg0);
		gameInit();
	}
	
	/* Not required but provided as a fail safe
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/* This method simply initializes all of the required components
	 * to make the GUI representation of the game.
	 */
	public void sceneInit(Stage stage) {
		stage = new Stage();
		stage.setTitle("2048");
		
		boardText = new Text("Board");
		boardText.setFont(f);
		
		upButton = new Button("^");
		upButton.setOnAction(e -> {moveUP();});
		
		downButton = new Button("v");
		downButton.setOnAction(e -> {moveDOWN();});
		
		leftButton = new Button("<");
		leftButton.setOnAction(e -> {moveLEFT();});
		
		rightButton = new Button(">");
		rightButton.setOnAction(e -> {moveRIGHT();});
		
		upButton.setMinSize(30, 30);
		downButton.setMinSize(30, 30);
		leftButton.setMinSize(30, 30);
		rightButton.setMinSize(30, 30);
		
		HBox horizontalHolder = new HBox();
		VBox verticalHolder = new VBox();
		
		verticalHolder.getChildren().addAll(upButton, downButton);
		horizontalHolder.getChildren().addAll(leftButton, verticalHolder, rightButton);
		horizontalHolder.setAlignment(Pos.CENTER);
		
		VBox root = new VBox();
		root.getChildren().addAll(boardText, horizontalHolder);
		root.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(root, WIDTH, HEIGHT);
		scene.setOnKeyPressed(e -> {
			switch(e.getCode()) {
				case DOWN:
					moveDOWN();
					break;
				case UP:
					moveUP();
					break;
				case LEFT:
					moveLEFT();
					break;
				case RIGHT:
					moveRIGHT();
					break;
				default:
					break;
			}
		});
		
		stage.setScene(scene);
		stage.show();
	}
	
	/* Prepares the game to run before the player
	 * takes their first turn
	 */
	public void gameInit() {
		board = new Node[ROWSXCOLS][ROWSXCOLS];
		random = new Random();
		
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				board[i][j] = new Node();
			}
		}
		
		//Initialize first Node to the game board
		initializeNewNode();
		initializeNewNode();
		printBoard();
	}
	
	/* This method replaces the gameLoop since the game has changed to 
	 * a GUI representation. This method is called after each of the
	 * move methods. If a new node can be initialized or a move
	 * can be made the game continues. Otherwise the GAMEOVER message
	 * is printed to the screen.
	 */
	public void checkIfGameOver() {
		if(initializeNewNode() || canMove()) {
			printBoard();
		} else {
			boardText.setText(GAMEOVER);
		}
	}
	
	/* Prints the current state of the game board
	 */
	public void printBoard() {
		String fullBoard = "";
		for(int i = 0; i < ROWSXCOLS; i++) {
			String line = "";
			for(int j = 0; j < ROWSXCOLS; j++) {
				if(board[i][j].getValue() != -1) {
					line += String.format("[%4d]", board[i][j].getValue());
				} else {
					line += "[    ]";
				}
			}
			
			fullBoard += line + "\n";
			
		}
		
		boardText.setText(fullBoard);
		
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
		checkIfGameOver();
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
		checkIfGameOver();
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
		checkIfGameOver();
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
		checkIfGameOver();
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
			
		} else if(empty == 1) {
			
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
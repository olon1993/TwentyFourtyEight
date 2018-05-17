import java.net.URL;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Game extends Application{

	// GAME OPTIONS
	private static final int ROWSXCOLS = 4;
	private static final int SIZE = ROWSXCOLS * ROWSXCOLS;
	private static final boolean RANDOMGENERATOR = true;
	private static final String GAMEOVER = "GAME OVER";
	private static final int OFFSET = 37;
	
	// Sizing Options
	private static final int WIDTH = 300;
	private static final int HEIGHT = 400;
	private static final int CANVAS_WIDTH = 250;
	private static final int CANVAS_HEIGHT = 250;
	private static final int TILE_WIDTH = 50;
	private static final int TILE_HEIGHT = 50;
	private static final int TILE_PADDING = 10;
	
	// Color Options
	private static final Color[] c = 
			new Color[] {Color.web("#FFFFCC"),
						 Color.web("#FFFF66"),
						 Color.web("#FFFF00"),
						 Color.web("#FFCC00"),
						 Color.web("#FF9900"),
						 Color.web("#FF4D00"),
						 Color.web("#FF0000"),
						 Color.web("#E60080"),
						 Color.web("#CC00FF"),
						 Color.web("#8033FF"),
						 Color.web("#3366FF"),
						 Color.web("#33B3B3"),
						 Color.web("#33FF66")};
	
	// Sound Options
	private static final String scoreSoundPath = "audio/score.wav";
	private static final String gameoverSoundPath = "audio/gameover.wav";
	
	// Game Variables
	private Tile[][] board;
	private Random random;
	private boolean isMoving;
	private int scoreValue;
	private URL scoreSound,
				gameoverSound;
	private AudioClip scoreClip,
					  gameoverClip;
	
	// Scene Variables
	private Canvas canvas;
	private GraphicsContext gc;
	private Text score;
	private Button upButton,
		   		   downButton,
		   		   leftButton,
		   		   rightButton;
	
	@Override
	/* Prepares the game for play by calling the sceneInit and
	 * gameInit methods.
	 */
	public void start(Stage arg0) throws Exception {
		gameInit();
		sceneInit(arg0);
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
		
		upButton = new Button("^");
		upButton.setOnAction(e -> { moveUP(); });
		
		downButton = new Button("v");
		downButton.setOnAction(e -> { moveDOWN(); });
		
		leftButton = new Button("<");
		leftButton.setOnAction(e -> { moveLEFT(); });
		
		rightButton = new Button(">");
		rightButton.setOnAction(e -> { moveRIGHT(); });
		
		upButton.setMinSize(30, 30);
		downButton.setMinSize(30, 30);
		leftButton.setMinSize(30, 30);
		rightButton.setMinSize(30, 30);
		
		canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
		gc = canvas.getGraphicsContext2D();
		paintBoard();
		
		score = new Text("Score: " + String.valueOf(scoreValue));
		
		HBox horizontalButtonHolder = new HBox();
		VBox verticalButtonHolder = new VBox();
		
		verticalButtonHolder.getChildren().addAll(upButton, downButton);
		horizontalButtonHolder.getChildren().addAll(leftButton, verticalButtonHolder, rightButton);
		horizontalButtonHolder.setAlignment(Pos.CENTER);
		
		VBox root = new VBox();
		root.getChildren().addAll(score, canvas, horizontalButtonHolder);
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
		board = new Tile[ROWSXCOLS][ROWSXCOLS];
		random = new Random();
		scoreValue = 0;
		
		scoreSound = getClass().getResource(scoreSoundPath);
		scoreClip = new AudioClip(scoreSound.toString());
		
		gameoverSound = getClass().getResource(gameoverSoundPath);
		gameoverClip = new AudioClip(gameoverSound.toString());
		
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				board[i][j] = new Tile(i ,j);
			}
		}
		
		//Initialize first Tile to the game board
		initializeNewTile();
		initializeNewTile();
	}
	
	/* This method replaces the gameLoop since the game has changed to 
	 * a GUI representation. This method is called after each of the
	 * move methods. If a new tile can be initialized or a move
	 * can be made the game continues. Otherwise the GAMEOVER message
	 * is printed to the screen.
	 */
	public void checkIfGameOver() {
		if(initializeNewTile() || canMove()) {
			score.setText("Score: " + String.valueOf(scoreValue));
			paintBoard();
		} else {
			gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
			gc.strokeText(GAMEOVER, (CANVAS_WIDTH / 2) - OFFSET, CANVAS_HEIGHT / 2);
			gameoverClip.play();
		}
	}
	
	public void paintBoard() {
		gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				
				//Get the x and y location to paint the tile
				double x = board[i][j].getLocX() * (TILE_WIDTH + TILE_PADDING) + TILE_PADDING;
				double y = board[i][j].getLocY() * (TILE_HEIGHT + TILE_PADDING) + TILE_PADDING;
				gc.setFill(c[(int)(Math.log(board[i][j].getValue()) / Math.log(2))]);
				gc.fillRect(x, y, TILE_WIDTH, TILE_HEIGHT);
				gc.setStroke(Color.BLACK);
				
				//Draw Tile Value if greater than 0
				if(board[i][j].getValue() > 0) {
					gc.strokeText(String.valueOf(board[i][j].getValue()), 
							x + (TILE_WIDTH / 2), y + (TILE_WIDTH / 2));
				}
			}
		}
	}
	
	/* Moves the Tiles to the top of the board and merges like Tiles
	 */
	public void moveUP() {
		do {
			isMoving = false; //Assume no moves have been made
			for(int i = 0; i < ROWSXCOLS; i++) {
				for(int j = ROWSXCOLS - 1; j > 0; j--) {
					
					//If two adjacent Tiles have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Tiles
					   board[i][j].getValue() == board[i][j - 1].getValue()) {
						board[i][j - 1].merge(board[i][j]);
						incrementScore(board[i][j - 1].getValue());
						isMoving = true;
					} 
					
					//If an active Tile is adjacent to an empty Tile
					//the active Tile switches places with the empty Tile
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
		//due to new Tiles becoming empty.
		while (isMoving);
		checkIfGameOver();
	}
	
	/* Moves the Tiles to the bottom of the board and merges like Tiles
	 */
	public void moveDOWN() {
		do {
			isMoving = false; //Assume no moves have been made
			for(int i = 0; i < ROWSXCOLS; i++) {
				for(int j = 0; j < ROWSXCOLS - 1; j++) {
					
					//If two adjacent Tiles have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Tiles
					   board[i][j].getValue() == board[i][j + 1].getValue()) {
						board[i][j + 1].merge(board[i][j]);
						incrementScore(board[i][j + 1].getValue());
						isMoving = true;
					} 
					
					//If an active Tile is adjacent to an empty Tile
					//the active Tile switches places with the empty Tile
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
		//due to new Tiles becoming empty.
		while (isMoving);
		checkIfGameOver();
	}
	
	/* Moves the Tiles to the left of the board and merges like Tiles
	 */
	public void moveLEFT() {
		do {
			isMoving = false; //Assume no moves have been made
			
			for(int i = ROWSXCOLS - 1; i > 0; i--) {
				for(int j = 0; j < ROWSXCOLS; j++) {
					
					//If two adjacent Tiles have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Tiles
					   board[i][j].getValue() == board[i - 1][j].getValue()) {
						board[i - 1][j].merge(board[i][j]);
						incrementScore(board[i - 1][j].getValue());
						isMoving = true;
					} 
					
					//If an active Tile is adjacent to an empty Tile
					//the active Tile switches places with the empty Tile
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
		//due to new Tiles becoming empty.
		while (isMoving);
		checkIfGameOver();
	}
	
	/* Moves the Tiles to the right of the board and merges like Tiles
	 */
	public void moveRIGHT() {
		do {
			isMoving = false; //Assume no moves have been made
			for(int i = 0; i < ROWSXCOLS - 1; i++) {
				for(int j = 0; j < ROWSXCOLS; j++) {
					
					//If two adjacent Tiles have the same value they merge
					if(board[i][j].getValue() > 0 && //Don't check empty Tiles
					   board[i][j].getValue() == board[i + 1][j].getValue()) {
						board[i + 1][j].merge(board[i][j]);
						incrementScore(board[i + 1][j].getValue());
						isMoving = true;
					} 
					
					//If an active Tile is adjacent to an empty Tile
					//the active Tile switches places with the empty Tile
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
		//due to new Tiles becoming empty.
		while (isMoving);
		checkIfGameOver();
	}
	
	public void incrementScore(int value) {
		scoreClip.play();
		scoreValue += value;
	}
	
	/* Checks to see if there is at least one move where 
	 * at least one Tile will call reset()
	 */
	public boolean canMove() {
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				//Check if there is an empty Tile at board[i][j]
				if(board[i][j].getValue() == -1) {
					return true;
				}
				//Check if Tile at board[i][j] will call reset()
				if((i + 1 < ROWSXCOLS) && (j + 1 < ROWSXCOLS) && //Don't check outer edge Tiles
					(board[i][j].getValue() > 0) &&				 //Don't check empty Tiles
					(board[i][j].getValue() == board[i + 1][j].getValue() ||
					 board[i][j].getValue() == board[i][j + 1].getValue())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/* Randomly initializes a new Tile in one of
	 * the available spaces on the game board
	 */
	public boolean initializeNewTile() {
		Tile newTile;
		int locx, locy, selection, empty = 0;
		int[] emptyTiles = new int[SIZE];
		
		// Make a list of all of the empty tiles
		// Tiles are numbered from left to right, and top to bottom
		for(int i = 0; i < ROWSXCOLS; i++) {
			for(int j = 0; j < ROWSXCOLS; j++) {
				if(board[i][j].getValue() == -1) {
					emptyTiles[empty++] = (i * ROWSXCOLS) + j;
				}
			}
		}
		
		// Verify that there is space for a new Tile
		if(empty > 1) {

			// If there is space for a new Tile and there
			// is more than one space for the new Tile,
			// select a random Tile from the list of empty Tiles
			selection = emptyTiles[random.nextInt(empty)];
			
		} else if(empty == 1) {
			
			// If there is space for a new Tile but
			// there is only one space left the
			// selection becomes the only available space
			selection = emptyTiles[0];
		}
		else {
			// Return false, no space for new Tile
			return false;
		}

		// Get the x location and y location from the selection
		locy = selection % ROWSXCOLS;
		locx = selection / ROWSXCOLS;
		
		// Initialize a new Tile at (x,y)
		if(RANDOMGENERATOR) {
			newTile = new Tile(((random.nextInt(2) + 1) * 2), locx, locy);
		} else {
			newTile = new Tile(2, locx, locy);
		}
		
		// Add the new Tile to the board
		board[locx][locy] = newTile;
		
		// Return true, new Tile initialized successfully
		return true;
	}
	
}
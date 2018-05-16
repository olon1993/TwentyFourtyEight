import javafx.scene.layout.StackPane;

public class Tile extends StackPane{	
	private int value, 
				locx, 
				locy;
	
	public Tile(int locx, int locy) {
		this.value = -1;
		this.locx = locx;
		this.locy = locy;
	}
	
	public Tile(int value, int locx, int locy) {
		this.value = value;
		this.locx = locx;
		this.locy = locy;
	}
	
	public void merge(Tile t2) {
		this.setValue(t2.getValue() * 2);
		t2.reset();
	}
	
	public void reset() {
		this.value = -1;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int number) {
		this.value = number;
	}
	
	public int getLocX() {
		return this.locx;
	}
	
	public void setLocX(int number) {
		this.locx = number;
	}
	
	public int getLocY() {
		return this.locy;
	}
	
	public void setLocY(int number) {
		this.locy = number;
	}
}

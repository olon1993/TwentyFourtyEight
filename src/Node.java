
public class Node {
	private int value;
	
	public Node() {
		this.value = -1;
	}
	
	public Node(int value) {
		this.value = value;
	}
	
	public void merge(Node n2) {
		this.setValue(n2.getValue() * 2);
		n2.reset();
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
}

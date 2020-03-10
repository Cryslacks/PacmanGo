
public class Edge {
	private Coordinate a;
	private Coordinate b;
	
	public Edge(Coordinate a, Coordinate b) {
		this.a = a;
		this.b = b;
	}
	
	public Coordinate getKey() {
		return this.a;
	}
	
	public Coordinate getValue() {
		return this.b;
	}
}

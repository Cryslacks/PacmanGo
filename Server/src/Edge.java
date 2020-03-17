/**
 * Represents an edge which two coordinates connects.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */
public class Edge {
	private Coordinate a;
	private Coordinate b;
	/**
	 * Creates a new edge which is connected by the two coordinates.
	 * @param a The first coordinate which is to be connected.
	 * @param b The second coordinate which is to be connected.
	 */
	public Edge(Coordinate a, Coordinate b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Gets the first coordinate which connects the edge.
	 * @return The first coordinate.
	 */
	public Coordinate getKey() {
		return this.a;
	}

	/**
	 * Gets the second coordinate which connects the edge.
	 * @return The second coordinate.
	 */	
	public Coordinate getValue() {
		return this.b;
	}
}

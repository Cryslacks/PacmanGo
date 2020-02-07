
public class MapValues {
	private String name;
	private int[][] adj_mat;
	private Coordinate[] pos_mat;
	
	public MapValues(String name, int[][] adj_mat, Coordinate[] pos_mat){
		this.name = name;
		this.adj_mat = adj_mat;
		this.pos_mat = pos_mat;
	}
	public String getName(){
		return this.name;
	}
	public int[][] getAdj_mat(){
		return this.adj_mat;
	}
	public Coordinate[] getPos_mat(){
		return this.pos_mat;
	}

}

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents the database data provided as map.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */
public class MapData {
	private Edge[] edgeList;
	private Coordinate[] posList;
	private int distCoin = 50;
	private int[][]	adjMat;
	private int[][] oldAdj;
	private Coordinate[] coinList;
	private int coins;
	private int verts;
	private String mapName;
	
	/**
	 * Creates a new map from provided data and name.
	 * @param mapdata The database mapdata as a JSONObject.
	 * @param name The name which the map has.
	 */
	public MapData(JSONObject mapdata, String name) {
		this.verts = 0;
		this.setupPos(mapdata);
		this.setupAdj(mapdata);
		this.setupEdges();
		this.setupCoins();
		this.mapName = name;
		System.out.println("Coins: "+coins);
		System.out.println("Verts: "+verts);
	}

	/**
	 * Sets up the position list from provided mapdata.
	 * @param mapdata The data of the map which is to be setup.
	 */
	private void setupPos(JSONObject mapdata) {
		JSONArray pos_mat = mapdata.getJSONArray("pos_list");
		this.posList = new Coordinate[pos_mat.length()];
		
		for(int i = 0; i < pos_mat.length(); i++) {
			JSONArray pos = pos_mat.getJSONArray(i);
			this.posList[i] = new Coordinate(pos.getDouble(0), pos.getDouble(1));
		}
	}

	/**
	 * Sets up the adjency matrix from provided mapdata.
	 * @param mapdata The data of the map which is to be setup.
	 */
	private void setupAdj(JSONObject mapdata) {
		JSONArray adjencyMatrix = mapdata.getJSONArray("adj_list");
		
		int nrOfLinks = adjencyMatrix.length();
		this.adjMat = new int[this.posList.length][this.posList.length];
		this.oldAdj = new int[this.posList.length][this.posList.length];
		
		for(int i = 0; i < nrOfLinks; i++) {
			JSONArray row = adjencyMatrix.getJSONArray(i);
			this.oldAdj[i] = new int[row.length()];
			for(int c = 0; c < row.length(); c++) {
				/*if(i == row.getInt(c))      Fråga inte varför detta är utkommenterat, det är svårt att läsa en api.
					continue;*/
				this.oldAdj[i][c] = row.getInt(c);
				this.adjMat[i][row.getInt(c)] = 1;
				this.verts++;
			}
		}
	}
	
	/**
	 * Sets up the edges between each connected node.
	 */
	private void setupEdges() {
		this.edgeList = new Edge[this.verts];
		int vertNr = 0;
		
		for(int i = 0; i < this.adjMat.length; i++) {
			for(int c = 0; c < this.adjMat.length; c++) {
				if(this.adjMat[i][c] == 1) {
					this.edgeList[vertNr] = new Edge(posList[i], posList[c]);
					this.coins += Math.floor(posList[i].distanceToM(posList[c])/this.distCoin);
					vertNr++;
				}
			}
		}
		
		this.coins += this.posList.length;
	}
	
	/**
	 * Sets up the coins which are located on each edge and node.
	 */
	private void setupCoins() {
		this.coinList = new Coordinate[this.coins];
		int currCoin = 0;
		System.out.println("[MapData] Coins to be generated: "+this.coins);
		for(int i = 0; i < this.edgeList.length; i++) {
			Coordinate a = this.edgeList[i].getKey();
			Coordinate b = this.edgeList[i].getValue();

			Coordinate[] coords = generateCoins(a, b, this.distCoin);
			for (Coordinate curr : coords) {
				this.coinList[currCoin] = curr;
				currCoin++;
			}
		}
		
		System.out.println("[MapData] Adding the last "+this.posList.length+" coins on each node!");
		for(int i = 0; i < this.posList.length; i++) {
			this.coinList[currCoin] = this.posList[i];
			currCoin++;
		}
	}
	
	/**
	 * Generates a coin between two coordinates with a specified distance.
	 * @param a The first coordinate of the edge.
	 * @param b The second coordinate of the edge.
	 * @param dist The distance between each coin which shall be added.
	 * @return A coordinate list where each new coin is located on the edge.
	 */
	static Coordinate[] generateCoins(Coordinate a, Coordinate b, int dist) {
		double d = a.distanceToM(b);				
		int numbCoins = (int) Math.floor(d/dist);
		Coordinate[] coords = new Coordinate[numbCoins];
		
		double dRatio, lon, lat;
		double[] oldA, oldB; 
		d = a.distanceTo(b);
		for(int i = 0; i < numbCoins; i++) {
			dRatio = ((d/(numbCoins+1))*(i+1))/d;
			oldA = a.getCoord();
			oldB = b.getCoord();
			lon = oldA[0] + dRatio * (oldB[0]-oldA[0]);
			lat = oldA[1] + dRatio * (oldB[1]-oldA[1]);

			coords[i] = new Coordinate(lon, lat);
		}		
		
		return coords;
	}
	
	/**
	 * Gets the list of edges.
	 * @return A list of edges.
	 */
	public Edge[] getEdgeList(){
		return this.edgeList;
	}
	
	/**
	 * Gets all of the coins on the map. 
	 * @return A list of all coins which are located on the map.
	 */
	public Coordinate[] getCoins() {
		return this.coinList;
	}

	/**
	 * Removes a coin which is located on the map.
	 * @param id The specified id of the coin which is to be removed.
	 */
	public void removeCoin(int id) {
		this.coinList[id] = null;
	}
	
	/**
	 * Converts the map into a JSON format.
	 * @param pacman Boolean if the player is pacman, due to ghosts not being able to see coins.
	 * @return A JSONObject containing all details of the map.
	 */
	public JSONObject toJSON(boolean pacman) {
		JSONObject a = new JSONObject();
		
		a.append("data", this.oldAdj);
		double[][] b = new double[this.posList.length][2];
		for(int i = 0; i < b.length; i++) {
			b[i][0] = this.posList[i].getCoord()[0];
			b[i][1] = this.posList[i].getCoord()[1];
		}
			
		a.append("data", b);

		if(pacman) {
			b = new double[this.coinList.length][2];
			for(int i = 0; i < b.length; i++) {
				b[i][0] = this.coinList[i].getCoord()[0];
				b[i][1] = this.coinList[i].getCoord()[1];
			}
				
			a.append("data", b);
		}else
			a.append("data", new int[0]);
		
		System.out.println(a.toString());
		return a;
	}
}

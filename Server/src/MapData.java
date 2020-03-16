import org.json.JSONArray;
import org.json.JSONObject;

import javafx.util.Pair;

public class MapData {
	private Edge[] edgeList;
	private Coordinate[] posList;
	private int[][]	adjMat;
	private int[][] oldAdj;
	private Coordinate[] coinList;
	private int coins;
	private int verts;
	private String mapName;
	
	public MapData(JSONObject mapdata, String name) {
		this.json = mapdata;
		this.verts = 0;
		this.setupPos(mapdata);
		this.setupAdj(mapdata);
		this.setupEdges();
		this.setupCoins();
		this.mapName = name;
		System.out.println("Coins: "+coins);
		System.out.println("Verts: "+verts);
	}
		
	private void setupPos(JSONObject mapdata) {
		JSONArray pos_mat = mapdata.getJSONArray("pos_list");
		this.posList = new Coordinate[pos_mat.length()];
		
		for(int i = 0; i < pos_mat.length(); i++) {
			JSONArray pos = pos_mat.getJSONArray(i);
			this.posList[i] = new Coordinate(pos.getDouble(0), pos.getDouble(1));
		}
	}

	private void setupAdj(JSONObject mapdata) {
		JSONArray adjencyMatrix = mapdata.getJSONArray("adj_list");
		
		int nrOfLinks = adjencyMatrix.length();
		this.adjMat = new int[this.posList.length][this.posList.length];
		this.oldAdj = new int[this.posList.length][this.posList.length];
		
		for(int i = 0; i < nrOfLinks; i++) {
			JSONArray row = adjencyMatrix.getJSONArray(i);
			this.oldAdj[i] = new int[row.length()];
			for(int c = 0; c < row.length(); c++) {
				if(i == row.getInt(c))
					continue;
				this.oldAdj[i][c] = row.getInt(c);
				this.adjMat[i][row.getInt(c)] = 1;
				this.verts++;
			}
		}
	}
	
	private void setupEdges() {
		this.edgeList = new Edge[this.verts];
		int vertNr = 0;
		
		for(int i = 0; i < this.adjMat.length; i++) {
			for(int c = 0; c < this.adjMat.length; c++) {
				if(this.adjMat[i][c] == 1) {
					this.edgeList[vertNr] = new Edge(posList[i], posList[c]);
					this.coins += Math.floor(posList[i].distanceTo(posList[c])/5);
					vertNr++;
				}
			}
		}
		
		this.coins += this.posList.length;
	}
	
	private void setupCoins() {
		this.coinList = new Coordinate[this.coins];
		int currCoin = 0;
		
		for(int i = 0; i < this.edgeList.length; i++) {
			Coordinate a = this.edgeList[i].getKey();
			Coordinate b = this.edgeList[i].getValue();
			
			for(int c = 0; c < Math.floor(a.distanceTo(b)/5); c++) {
				Coordinate[] coords = generateCoins(a, b, 15);
				for (Coordinate curr : coords) {
					this.coinList[currCoin] = curr;
					currCoin++;
				}
			}
		}
		
		for(int i = 0; i < this.posList.length; i++) {
			this.coinList[currCoin] = this.posList[i];
			currCoin++;
		}
	}
	
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
	
	public Edge[] getEdgeList(){
		return this.edgeList;
	}
	
	public double[][] getCoinPos()
	{
		double[][] coinpos = new double[coinList.length][];
		for(int i = 0; i<coinpos.length; i++)
			coinpos[i] = coinList[i].getCoord();
		return coinpos;
	}
	
	public Coordinate[] getCoins() {
		return this.coinList;
	}

	public JSONObject toJSON() {
		JSONObject a = new JSONObject();
		
		a.append("data", this.oldAdj);
		double[][] b = new double[this.posList.length][2];
		for(int i = 0; i < b.length; i++) {
			b[i][0] = this.posList[i].getCoord()[0];
			b[i][1] = this.posList[i].getCoord()[1];
		}
			
		a.append("data", b);

		b = new double[this.coinList.length][2];
		for(int i = 0; i < b.length; i++) {
			b[i][0] = this.coinList[i].getCoord()[0];
			b[i][1] = this.coinList[i].getCoord()[1];
		}
			
		a.append("data", b);
		
		System.out.println(a.toString());
		return a;
	}
	
	public JSONArray posList()
	{
		return this.json.getJSONArray("pos_list");
	}
	
	public JSONArray adjList() 
	{
		return this.json.getJSONArray("adj_list");
	}
	
	
}

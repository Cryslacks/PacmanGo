import org.json.JSONArray;
import org.json.JSONObject;

import javafx.util.Pair;

public class MapData {
	private Pair<Coordinate, Coordinate>[] edgeList;
	private Coordinate[] posList;
	private int[][]	adjMat;
	private Coordinate[] coinList;
	private int coins;
	private int verts;
	private String mapName;
	
	public MapData(JSONObject mapdata, String name) {
		this.verts = 0;
		this.setupPos(mapdata);
		this.setupAdj(mapdata);
		this.setupEdges();
		this.setupCoins();
		this.mapName = name;
	}
		
	private void setupPos(JSONObject mapdata) {
		JSONArray pos_mat = mapdata.getJSONArray("pos_mat");
		this.posList = new Coordinate[pos_mat.length()];
		
		for(int i = 0; i < pos_mat.length(); i++) {
			JSONArray pos = pos_mat.getJSONArray(i);
			this.posList[i] = new Coordinate(pos.getDouble(0), pos.getDouble(1));
		}
	}

	private void setupAdj(JSONObject mapdata) {
		JSONArray adjencyMatrix = mapdata.getJSONArray("adj_mat");
		int nrOfLinks = adjencyMatrix.length();
		this.adjMat = new int[nrOfLinks][nrOfLinks];
		
		for(int i = 0; i < nrOfLinks; i++) {
			JSONArray row = adjencyMatrix.getJSONArray(i);
			for(int c = i; c < nrOfLinks; c++) {
				this.adjMat[i][c] = row.getInt(c);
				if(this.adjMat[i][c] == 1)
					this.verts++;
			}
		}
	}
	
	private void setupEdges() {
		this.edgeList = new Pair[this.verts];
		int vertNr = 0;
		
		for(int i = 0; i < this.adjMat.length; i++) {
			for(int c = 0; c < this.adjMat.length; c++) {
				if(this.adjMat[i][c] == 1) {
					this.edgeList[vertNr] = new Pair<Coordinate, Coordinate>(posList[i], posList[c]);
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
	
	public Pair<Coordinate, Coordinate>[] getEdgeList(){
		return this.edgeList;
	}
	
	public Coordinate[] getCoins() {
		return this.coinList;
	}

	public String toString() {
		JSONObject a = new JSONObject();
		
		return "";
	}
}

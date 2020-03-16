import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.util.Pair;

public class Game {
	private ArrayList<Player> players;
	private Coordinate[] coins;
	private int gameId;
	private GameState gameState;
	private int collisionRadius = 10;
	private ConnectionHandler ch;
	private MapData map;
	private PlayerType winner;
	private int remCoin;
	private String ruleBreak;
	public boolean[] collectedCoins;
	
	public Game(ConnectionHandler ch, int gameId, String name, InputStream ois, OutputStream oos) {
		this.ch = ch;
		this.gameState = GameState.Lobby;
		this.players = new ArrayList<Player>();
		this.players.add(new Player(this, PlayerType.Pacman, name, ois, oos));
		new Thread(this.players.get(this.players.size()-1)).start();
		System.out.println("Game: Game started with gameId <"+gameId+">");
		this.winner = null;
		this.remCoin = -1;
		this.ruleBreak = "";
	}
	
	public JSONObject startGame(Player p, int mapId) {
		JSONObject j = new JSONObject();
		
		if(p.getType() == PlayerType.Pacman) {
			this.gameState = GameState.InProgress;
			System.out.println("Game: Game has now started and is in progress");
			j.put("protocol", "MAP_DATA");
			// GET MAP DATA from DatabaseHandler.loadMap()																//Need to add map ID;
			this.map = DatabaseHandler.loadMap(mapId); 
			j.put("data", this.map.toJSON().get("data"));
			System.out.println("Sending MAP_DATA => \n\t"+j.toString());
			
			if(ServerFunc.debugMode){
				System.out.println(j.toString());
			}
			JSONObject mapData = new JSONObject();
			j.put("data", mapData);
			mapData.put("adj_list", this.map.adjList());
			mapData.put("pos_list", this.map.posList());
			for(int i = 0; i < this.players.size(); i++) {
				if(!this.players.get(i).getName().equals(p.getName())) {
					this.players.get(i).sendMapData(j);
				}
			}
			mapData.put("coins", this.map.getCoinPos());
		}else {
			j.put("protocol", "START_GAME");
			j.put("data", false);
		}
		
		return j;
	}
	
	public int isCompleted() {
		if(this.gameState != GameState.Completed)
			return 0;
		
		return this.winner == PlayerType.Pacman ? 1 : 2;
	}

	public boolean inProgress() {
		return this.gameState == GameState.InProgress;
	}
	
	public boolean boundsDetection(Coordinate player) {
		Edge[] edges = this.map.getEdgeList();
		
		for(int i = 0; i < edges.length; i++) {
			if(player.collideArea(edges[i].getKey(), edges[i].getValue(), 5))
				return false;
		}
		
		return true;
	}
	
	public int isOnCoin(Player p) {
		if(p.getType() != PlayerType.Pacman)
			return -1;
		
		Coordinate[] coins = this.map.getCoins();
		for(int i = 0; i < coins.length; i++)
			if(!collectedCoins[i]&&collisionDetection(p.getCoord(), coins[i]))
				return i;
		
		return -1;
	}
	
	public boolean isAttacked(Player p) {
		if(p.getType() != PlayerType.Pacman) 
			return false;
			
		for(int i = 0; i < this.players.size(); i++) 
			if(!this.players.get(i).equals(p)) 
				if(collisionDetection(p.getCoord(), this.players.get(i).getCoord()))
					return true;
	
		return false;
	}
	
	public boolean collisionDetection(Coordinate a, Coordinate b) {
		double dist = a.distanceToM(b);
		if(ServerFunc.debugMode)
			System.out.println((dist < this.collisionRadius) + " = Collision");
		return dist < this.collisionRadius;
		//double[] am = a.toMeters();
		//double[] bm = b.toMeters();
		
		//return (am[0]-bm[0]) * (am[0]-bm[0]) + (am[1]-bm[1]) * (am[1]-bm[1]) < (this.collisionRadius*2) * (this.collisionRadius*2);
	}
	
	public Coordinate[] updatePlayer(Player p) {
		int pId = this.players.indexOf(p);
		ArrayList<Player> pList = p.getUpdate();

		for(int i = 0; i < this.players.size(); i++)
			if(pId != i)
				this.players.get(i).addUpdate(p);
			
		if(boundsDetection(p.getCoord())) 
			this.ruleBreak = "OUT_OF_BOUNDS";
		
		if(isAttacked(p)) {
			this.gameState = GameState.Completed;
			this.winner = PlayerType.Ghost;
		}
		
		int coinCollected = isOnCoin(p);
		if(coinCollected != -1) {
			this.remCoin = coinCollected;
			collectedCoins[coinCollected] = true;
			boolean pacmanWins = false;
			for(boolean b : collectedCoins) {
				if(!b) {
					pacmanWins = b;
					break;
				}
				else pacmanWins = b;
			}
			if(pacmanWins) 
			{
				this.gameState = GameState.Completed;
				this.winner = PlayerType.Pacman;
			}
				
					
		}
		
		
		if(pList.size() == 0)
			return null;
		
		Coordinate[] coords = new Coordinate[pList.size()];

		for(int i = 0; i < pList.size(); i++)
			if(pList.get(i) != null)
				coords[i] = this.players.get(this.players.indexOf(pList.get(i))).getCoord();
		
		return coords;
	}
	
	public boolean addPlayer(String name, InputStream ois, OutputStream oos) {
		if(this.players.size() >= 5)
			return false;
		
		this.players.add(new Player(this, PlayerType.Ghost, name, ois, oos));
		new Thread(this.players.get(this.players.size()-1)).start();
		
		for(int i = 0; i < this.players.size()-1; i++)
			players.get(i).lobbyUpdate(name, true);
		
		return true;
	}
	
	public void removePlayer(Player p) {
		boolean changeHost = p.getType() == PlayerType.Pacman;
		String name = this.players.get(this.players.indexOf(p)).getName();
		this.players.remove(p);
		
		if(this.players.size() == 0) {
			this.ch.removeGame(this);
			return;
		}
		
		if(changeHost) {
			this.players.get(0).changeType(PlayerType.Pacman);
			System.out.println("Game: Host disconnected "+this.players.get(0).getName()+" is the new pacman");
		}		
		
		for(int i = 0; i < this.players.size(); i++)
			players.get(i).lobbyUpdate(name, false);
	}
	
	public String[] getPlayers() {
		String[] names = new String[this.players.size()];
		for(int i = 0; i < this.players.size(); i++) {
			names[i] = this.players.get(i).getName();
		}
		
		return names;
	}
	
	public int hasCollectedCoin() {
		int temp = this.remCoin;

		if(this.remCoin != -1)
			this.remCoin = -1;
		
		return temp;
	}
	
	public String hasBrokenRule() {
		String temp = this.ruleBreak;
		
		if(!this.ruleBreak.equals(""))
			this.ruleBreak = "";
		
		return temp;
	}
}

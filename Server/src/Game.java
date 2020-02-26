import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import javafx.util.Pair;

public class Game {
	private ArrayList<Player> players;
	private Coordinate[] coins;
	private int gameId;
	private GameState gameState;
	private int collisionRadius = 2;
	private ConnectionHandler ch;
	private MapData map;
	
	public Game(ConnectionHandler ch, int gameId, String name, InputStream ois, OutputStream oos) {
		this.ch = ch;
		this.gameState = GameState.Lobby;
		this.players = new ArrayList<Player>();
		this.players.add(new Player(this, PlayerType.Pacman, name, ois, oos));
		new Thread(this.players.get(this.players.size()-1)).start();
		System.out.println("Game: Game started with gameId <"+gameId+">");
	}
	
	public JSONObject startGame(Player p, int mapId) {
		JSONObject j = new JSONObject();
		
		if(p.getType() == PlayerType.Pacman) {
			this.gameState = GameState.InProgress;
			System.out.println("Game: Game has now started and is in progress");
			j.put("protocol", "MAP_DATA");
			// GET MAP DATA from DatabaseHandler.loadMap()																//Need to add map ID;
			this.map = DatabaseHandler.loadMap(mapId); 
			
			if(ServerFunc.debugMode){
				System.out.println(j.toString());
			}
			
			for(int i = 0; i < this.players.size(); i++) {
				if(!this.players.get(i).getName().equals(p.getName())) {
					this.players.get(i).sendMapData(j);
				}
			}
		}else {
			j.put("protocol", "START_GAME");
			j.put("data", false);
		}
		
		return j;
	}
	
	public boolean isCompleted() {
		return this.gameState == GameState.Completed;
	}

	public boolean inProgress() {
		return this.gameState == GameState.InProgress;
	}
	
	public boolean boundsDetection(Coordinate a) {
		Pair<Coordinate, Coordinate>[] edges = this.map.getEdgeList();
		
		for(int i = 0; i < edges.length; i++) {
			if(a.collideArea(edges[i].getKey(), edges[i].getValue(), 5))
				return false;
		}
		
		return true;
	}
	
	public boolean collisionDetection(Coordinate a, Coordinate b) {
		double[] am = a.toMeters();
		double[] bm = b.toMeters();
	
		return (am[0]-bm[0]) * (am[0]-bm[0]) + (am[1]-bm[1]) * (am[1]-bm[1]) < (this.collisionRadius*2) * (this.collisionRadius*2);
	}
	public boolean outOfBoundsDetection(Coordinate player, Coordinate edge1, Coordinate edge2){				
		return false;
		
	}
	
	public Coordinate[] updatePlayer(Player p) {
		int pId = this.players.indexOf(p);
		ArrayList<Player> pList = p.getUpdate();

		for(int i = 0; i < this.players.size(); i++)
			if(pId != i)
				this.players.get(i).addUpdate(p);
			
		if(pList.size() == 0)
			return null;
		
		Coordinate[] coords = new Coordinate[pList.size()];

		for(int i = 0; i < pList.size(); i++)
			coords[i] = this.players.get(this.players.indexOf(pList.get(i))).getCoord();
		
		return coords;
	}
	
	public boolean addPlayer(String name, InputStream ois, OutputStream oos) {
		if(this.players.size() >= 5)
			return false;
		
		this.players.add(new Player(this, PlayerType.Monster, name, ois, oos));
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
}

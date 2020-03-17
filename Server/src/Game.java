import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import javafx.util.Pair;
/**
 * Represents a game on the server.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */
public class Game {
	private ArrayList<Player> players;
	private Coordinate[] coins;
	private int gameId;
	private GameState gameState;
	private int collisionRadius = 4;
	private ConnectionHandler ch;
	private MapData map;
	private PlayerType winner;
	private int remCoin;
	private String ruleBreak;
	
	/**
	 * Creates a new game with the specified arguments.
	 * @param ch The connection handler which the game has been called from.
	 * @param gameId The id which the game has.
	 * @param name The name of the first connected user.
	 * @param ois The input stream of the first connected user.
	 * @param oos The output stream of the first connected user.
	 */
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
	
	/**
	 * Starts the game on a specified map if the player is pacman.
	 * @param p The player which is attempting to start the game.
	 * @param mapId The map which the game shall be played on.
	 * @return The data provided from the map chosen.
	 */
	public JSONObject startGame(Player p, int mapId) {
		JSONObject j = new JSONObject();
		
		if(this.gameState == GameState.InProgress) {
			j.put("protocol", "START_GAME");
			j.put("data", false);
			return j;
		}

		if(p.getType() == PlayerType.Pacman) {
			this.gameState = GameState.InProgress;
			System.out.println("Game: Game has now started and is in progress");
			JSONObject monsterJ = new JSONObject();
			monsterJ.put("protocol", "MAP_DATA");
			j.put("protocol", "MAP_DATA");
			// GET MAP DATA from DatabaseHandler.loadMap()																//Need to add map ID;
			this.map = DatabaseHandler.loadMap(mapId); 
			j.put("data", this.map.toJSON(true).get("data"));
			monsterJ.put("data", this.map.toJSON(false).get("data"));
			System.out.println("Sending MAP_DATA => \n\t"+j.toString());
			
			
			if(ServerFunc.debugMode){
				System.out.println(j.toString());
			}
			
			for(int i = 0; i < this.players.size(); i++) {
				if(!this.players.get(i).getName().equals(p.getName())) {
					this.players.get(i).sendMapData(monsterJ);
				}
			}
		}else {
			j.put("protocol", "START_GAME");
			j.put("data", false);
		}
		
		return j;
	}
	
	/**
	 * Checks if the game is completed or is running currently.
	 * @return If the game is completed returns the type which won.
	 */
	public int isCompleted() {
		if(this.gameState != GameState.Completed)
			return 0;
		
		return this.winner == PlayerType.Pacman ? 1 : 2;
	}
	
	/**
	 * Checks if the state of the game is in progress.
	 * @return Returns true if in progress otherwise false.
	 */
	public boolean inProgress() {
		return this.gameState == GameState.InProgress;
	}
	
	/**
	 * Checks if the player is on any edge.
	 * @param player The coordinates of the player which is to be checked.
	 * @return Returns true if the player is out of bounds.
	 */
	public boolean boundsDetection(Coordinate player) {
		Edge[] edges = this.map.getEdgeList();
		
		for(int i = 0; i < edges.length; i++) {
			if(player.collideArea(edges[i].getKey(), edges[i].getValue(), 5))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a specified player is standing on a coin.
	 * @param p The player to be checked.
	 * @return Returns the coin id if the player is standing on a coin, otherwise -1.
	 */
	public int isOnCoin(Player p) {
		if(p.getType() != PlayerType.Pacman)
			return -1;
		
		Coordinate[] coins = this.map.getCoins();
		for(int i = 0; i < coins.length; i++)
			if(coins[i] != null && collisionDetection(p.getCoord(), coins[i]))
				return i;

		return -1;
	}
	
	/**
	 * Checks if pacman is attacked by one of the ghosts.
	 * @param p The player to be checked.
	 * @return Returns true if the player is standing on a ghost.
	 */
	public boolean isAttacked(Player p) {
		if(p.getType() != PlayerType.Pacman) 
			return false;
			
		for(int i = 0; i < this.players.size(); i++) 
			if(!this.players.get(i).equals(p)) 
				if(collisionDetection(p.getCoord(), this.players.get(i).getCoord()))
					return true;
	
		return false;
	}
	
	/**
	 * Checks if two coordinates collides with each other.
	 * @param a The first coordinate.
	 * @param b The second coordinate.
	 * @return Boolean of which the two coordinates collides or not.
	 */
	public boolean collisionDetection(Coordinate a, Coordinate b) {
		double dist = a.distanceToM(b);

		if(dist < this.collisionRadius*2)
			return true;

		return false;
	}
	
	/**
	 * Updates the specified player and tells the other players that this player has updated.
	 * @param p The player which is getting updated.
	 * @return A list of all the players which the provided player has outdated coordinates of.
	 */
	public Coordinate[] updatePlayer(Player p) {
		int pId = this.players.indexOf(p);
		ArrayList<Player> pList = p.getUpdate();

		for(int i = 0; i < this.players.size(); i++)
			if(pId != i)
				this.players.get(i).addUpdate(p);
			
		if(boundsDetection(p.getCoord())) {
			System.out.println("OUT OF BOUNDS");			
			this.ruleBreak = "OUT_OF_BOUNDS";
		}
		if(isAttacked(p)) {
			this.gameState = GameState.Completed;
			this.winner = PlayerType.Ghost;
		}
		
		int coinCollected = isOnCoin(p);
		if(coinCollected != -1) {
			System.out.println("PICKED UP COIN NR: "+coinCollected);
			this.remCoin = coinCollected;
		}
		
		if(pList.size() == 0)
			return null;
		
		Coordinate[] coords = new Coordinate[pList.size()];

		for(int i = 0; i < pList.size(); i++)
			if(pList.get(i) != null && this.players.get(i) != null)
				coords[i] = this.players.get(this.players.indexOf(pList.get(i))).getCoord();
		
		return coords;
	}
	
	/**
	 * Adds a new player into the game.
	 * @param name The name of the player.
	 * @param ois The input stream of the player.
	 * @param oos The output stream of the player.
	 * @return Boolean of if the action was permitted or the lobby was full.
	 */
	public boolean addPlayer(String name, InputStream ois, OutputStream oos) {
		if(this.players.size() >= 5)
			return false;
		
		this.players.add(new Player(this, PlayerType.Ghost, name, ois, oos));
		new Thread(this.players.get(this.players.size()-1)).start();
		
		for(int i = 0; i < this.players.size()-1; i++)
			players.get(i).lobbyUpdate(name, true);
		
		return true;
	}
	
	/**
	 * Removes a player from the current game.
	 * @param p The player which is suppose to be removed
	 */
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
	
	/**
	 * Gets all the player names within the current game.
	 * @return String list of all the player names.
	 */
	public String[] getPlayers() {
		String[] names = new String[this.players.size()];
		for(int i = 0; i < this.players.size(); i++) {
			names[i] = this.players.get(i).getName();
		}
		
		return names;
	}
	
	/**
	 * Checks if a coin has been collected since last collision check.
	 * @return The coin id if a coin has been collected.
	 */
	public int hasCollectedCoin() {
		int temp = this.remCoin;
		
		if(this.remCoin != -1) {
			this.map.removeCoin(this.remCoin);
			this.remCoin = -1;
		}
		
		return temp;
	}
	
	/**
	 * Checks if a rule has been broken since last collision check.
	 * @return The rule which has been broken.
	 */
	public String hasBrokenRule() {
		String temp = this.ruleBreak;
		
		if(!this.ruleBreak.equals(""))
			this.ruleBreak = "";
		
		return temp;
	}
}

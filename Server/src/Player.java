import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Represents a player in the game.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */
public class Player implements Runnable{
	private PlayerType type;
	private String name;
	private Coordinate coord;
	private InputStream is;
	private OutputStream os;
	private Game game;
	private boolean isAlive;
	private ArrayList<Player> update;
	private byte[] readIn;
	private ReentrantLock mutex;
	
	/**
	 * Creates a new player from specified data.
	 * @param game Which game the player is connected to.
	 * @param type The type the player is, pacman or ghost.
	 * @param name The name of the player.
	 * @param is The input stream of the player.
	 * @param os The output stream of the player.
	 */
	public Player(Game game, PlayerType type, String name, InputStream is, OutputStream os){
		System.out.println("Player: "+type.toString()+" joined with the name "+name);
		this.type = type;
		this.name = name;
		this.coord = new Coordinate(0,0);
		this.is = is;
		this.os = os;
		this.game = game;
		this.isAlive = true;
		this.update = new ArrayList<Player>();
		this.readIn = new byte[1024];
		this.mutex =  new ReentrantLock();
		
	}
	
	/**
	 * Gets the name of the player.
	 * @return The name of the player.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the coordinate of the player.
	 * @return The coordinate of the player.
	 */
	public Coordinate getCoord(){
		return this.coord;
	}
	
	/**
	 * Gets the list which players this player hasn't gotten update from.
	 * @return An list containing all players which have updated their data.
	 */
	public ArrayList<Player> getUpdate(){
		System.out.println("update: <"+this.name+"> "+this.update.size());
		this.mutex.lock();
		ArrayList<Player> p = this.update;
		
		this.update = new ArrayList<Player>();
		this.mutex.unlock();
		return p;
	}
	
	/**
	 * Adds a new player to this players list of not updated players.
	 * @param p The player to be updated.
	 */
	public void addUpdate(Player p){
		this.mutex.lock();
		this.update.add(p);
		this.mutex.unlock();
	}
	
	/**
	 * Sends an update message if someone has joined or left a lobby.
	 * @param name The name of the player who has made an action.
	 * @param joined An boolean containing if the player joined or left the lobby.
	 */
	public void lobbyUpdate(String name, boolean joined) {
		JSONObject j = new JSONObject();
		if(joined)
			j.put("protocol", "JOINED_LOBBY");
		else
			j.put("protocol", "LEFT_LOBBY");
			
		j.put("data", name);
		try {
			ServerFunc.sendMsg(this.os, j);
		} catch (SocketException e) {
			System.out.println("Player: Player "+this.name+" disconnected!");
			this.game.removePlayer(this);
			this.isAlive = false;
		}
	}
	
	/**
	 * Sends data regarding the map to the player.
	 * @param j The data of the map in a JSON format.
	 */
	public void sendMapData(JSONObject j) {
		try {
			ServerFunc.sendMsg(this.os, j);
		} catch (SocketException e) {
			System.out.println("Player: Player "+this.name+" disconnected!");
			this.game.removePlayer(this);
			this.isAlive = false;
		}
	}

	/**
	 * Changes the type which the player has.
	 * @param pt The new playertype the player shall have.
	 */
	public void changeType(PlayerType pt) {
		this.type = pt;
	}
	
	/**
	 * Gets the type which the player has.
	 * @return The type which the player has.
	 */
	public PlayerType getType() {
		return this.type;
	}

	/**
	 * The main thread which waits for input from the user and later sends out corresponding response.
	 */
	@Override
	public void run() {
		while(isAlive){
			try {
				this.is.read(this.readIn);
				
				JSONObject response = new JSONObject(new String(this.readIn));
				JSONObject j = new JSONObject();
				
				System.out.println("Player: Got input from '"+this.name+"': ");
				if(ServerFunc.debugMode)
					System.out.println("\t"+response.toString());
				
				if(response.getString("protocol").equals("START_GAME")) {
						j = this.game.startGame(this, 9);
				}else if(response.getString("protocol").equals("UPDATE_POSITION")){
					this.coord.setCoord((double)response.getJSONArray("data").get(0), (double)response.getJSONArray("data").get(1));

					Coordinate[] c = this.game.updatePlayer(this);
					
					if(this.game.isCompleted() > 1) {
						j.put("protocol", "FINISH_GAME");
						j.put("data", (this.game.isCompleted() == 1 ? "Pacman" : "Ghost"));
					}

					if(c != null) {						
						System.out.println("Player: <"+this.name+"> Getting updated coords:<"+c.length+">");
						double[][] dd = new double[c.length][2];
						for(int i = 0; i < c.length; i++)
							dd[i] = c[i].getCoord();
						
						j.append("data", dd);
					}else
						j.append("data", new int[0]);
				 		

					int coin = this.game.hasCollectedCoin();
					if(coin > -1)
						j.append("data", coin);
					else
						j.append("data", new int[0]);

					String rule = this.game.hasBrokenRule();
					if(!rule.equals(""))
						j.append("data", rule);
					else
						j.append("data", "");						
					
					
					j.put("protocol", "POSITIONS");
				}else {
					j.put("protocol", "ERR");
					j.put("data", "ERR_UNKNOWN_PROTOCOL");
				}
				

				ServerFunc.sendMsg(this.os, j);
			} catch (SocketException e) {
				System.out.println("Player: Player "+this.name+" disconnected!");
				this.game.removePlayer(this);
				this.isAlive = false;
			} catch (JSONException e) {
				if(ServerFunc.debugMode)
					e.printStackTrace();
				System.out.println("Player: Player "+this.name+" disconnected!");
				this.game.removePlayer(this);
				this.isAlive = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}

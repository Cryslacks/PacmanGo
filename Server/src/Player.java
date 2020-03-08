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
	
	public String getName() {
		return this.name;
	}
	
	public Coordinate getCoord(){
		return this.coord;
	}
	
	public ArrayList<Player> getUpdate(){
		System.out.println("update: <"+this.name+"> "+this.update.size());
		this.mutex.lock();
		ArrayList<Player> p = this.update;
		
		this.update = new ArrayList<Player>();
		this.mutex.unlock();
		return p;
	}
	
	public void addUpdate(Player p){
		this.mutex.lock();
		this.update.add(p);
		this.mutex.unlock();
	}
	
	public void lobbyUpdate(String name, boolean joined) {
		JSONObject j = new JSONObject();
		if(joined)
			j.put("protocol", "JOINED_LOBBY");
		else
			j.put("protocol", "LEFT_LOBBY");
			
		j.put("data", name);
		ServerFunc.sendMsg(this.os, j);
	}
	
	public void sendMapData(JSONObject j) {
		ServerFunc.sendMsg(this.os, j);
	}

	public void changeType(PlayerType pt) {
		this.type = pt;
	}
	
	public PlayerType getType() {
		return this.type;
	}


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
					j = this.game.startGame(this, 0); //TODO: MAP_ID IMPLEMENTATION
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
						
						j.put("data", dd);
					}else
						j.put("data", new int[0]);


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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}

<<<<<<< refs/remotes/origin/Efiila
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;

import org.json.JSONException;
=======
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

>>>>>>> Player object and value handling
import org.json.JSONObject;

public class Player implements Runnable{
	private PlayerType type;
	private String name;
<<<<<<< refs/remotes/origin/Efiila
	private Coordinate coord;
	private InputStream is;
	private OutputStream os;
	private Game game;
	private boolean isAlive;
	private ArrayList<Player> update;
	private byte[] readIn;
	
	
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
	}
	
<<<<<<< refs/remotes/origin/Efiila
	public String getName() {
		return this.name;
	}
	
	public Coordinate getCoord(){
		return this.coord;
=======
	
	public Coordinate getCoords(){
		return this.coords;
>>>>>>> Player object and value handling
	}
	
	public ArrayList<Player> getUpdate(){
		System.out.println("update: <"+this.name+"> "+this.update.size());
		ArrayList<Player> p = this.update;
		
		this.update = new ArrayList<Player>();
		return p;
	}
	
	public void addUpdate(Player p){
		this.update.add(p);
	}
	
<<<<<<< refs/remotes/origin/Efiila
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
<<<<<<< refs/remotes/origin/Efiila
			try {
				this.is.read(this.readIn);
				
				JSONObject response = new JSONObject(new String(this.readIn));
				JSONObject j = new JSONObject();
				
				System.out.println("Player: Got input from '"+this.name+"': ");
				if(ServerFunc.debugMode)
					System.out.println("\t"+response.toString());
				
				if(response.getString("protocol").equals("START_GAME")) {
					j = this.game.startGame(this);
				}else if(response.getString("protocol").equals("UPDATE_POSITION")){
					this.coord.setCoord((double)response.getJSONArray("data").get(0), (double)response.getJSONArray("data").get(1));

					Coordinate[] c = this.game.updatePlayer(this);

					if(c != null) {						
						System.out.println("Player: <"+this.name+"> Getting updated coords:<"+c.length+">");
						double[][] dd = new double[c.length][2];
						for(int i = 0; i < c.length; i++)
							dd[i] = c[i].getCoord();
						
						j.put("data", dd);
					}else
						j.put("data", "");

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
=======
			JSONObject response = new JSONObject((String)in.readObject());
>>>>>>> Player object and value handling
			
		}
		
	}

}

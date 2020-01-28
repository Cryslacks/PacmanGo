<<<<<<< refs/remotes/origin/Efiila
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
=======
	private Coordinate coords;
>>>>>>> Player object and value handling
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Game game;
	private boolean isAlive;
	private ArrayList<Integer> update;
	
	
<<<<<<< refs/remotes/origin/Efiila
	public Player(Game game, PlayerType type, String name, ObjectInputStream ois, ObjectOutputStream oos){
		this.type = type;
		this.name = name;
		this.coord = new Coordinate(0,0);
=======
	public Player(PlayerType type, String name, Coordinate coords, ObjectInputStream ois, ObjectOutputStream oos, Game game){
		this.type = type;
		this.name = name;
		this.coords = coords;
>>>>>>> Player object and value handling
		this.ois = ois;
		this.oos = oos;
		this.game = game;
		this.isAlive = true;
		this.update = new ArrayList<Integer>();
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
	
	public ArrayList<Integer> getUpdate(){
		return this.update;
	}
	public void addUpdate(int a){
		this.update.add(a);
	}
	
<<<<<<< refs/remotes/origin/Efiila
	public void lobbyUpdate(String name, boolean joined) {
		try {
			JSONObject j = new JSONObject();
			if(joined)
				j.put("protocol", "JOINED_LOBBY");
			else
				j.put("protocol", "LEFT_LOBBY");
				
			j.put("data", name);
			this.oos.writeObject(j.toString());
			this.oos.flush();
		} catch (IOException e) {
			System.out.println("Player: ERR_IO");
			e.printStackTrace();
		}
	}
	
=======
>>>>>>> Player object and value handling
	public Coordinate[] update(){
		
		
		
		
	}



	@Override
	public void run() {
		while(isAlive){
<<<<<<< refs/remotes/origin/Efiila
			try {
					JSONObject response = new JSONObject((String)this.ois.readObject());
			} catch (SocketException e) {
				this.game.removePlayer(this);
				System.out.println("ERR_DISCONNECTED");
				this.isAlive = false;
			}  catch (JSONException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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

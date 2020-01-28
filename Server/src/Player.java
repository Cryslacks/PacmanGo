import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.json.JSONObject;

public class Player implements Runnable{
	private PlayerType type;
	private String name;
	private Coordinate coords;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Game game;
	private boolean isAlive;
	private ArrayList<Integer> update;
	
	
	public Player(PlayerType type, String name, Coordinate coords, ObjectInputStream ois, ObjectOutputStream oos, Game game){
		this.type = type;
		this.name = name;
		this.coords = coords;
		this.ois = ois;
		this.oos = oos;
		this.game = game;
		this.isAlive = true;
		this.update = new ArrayList<Integer>();
	}
	
	
	public Coordinate getCoords(){
		return this.coords;
	}
	
	public ArrayList<Integer> getUpdate(){
		return this.update;
	}
	public void addUpdate(int a){
		this.update.add(a);
	}
	
	public Coordinate[] update(){
		
		
		
		
	}



	@Override
	public void run() {
		while(isAlive){
			JSONObject response = new JSONObject((String)in.readObject());
			
		}
		
	}

}

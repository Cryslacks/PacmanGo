import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Game {
	private ArrayList<Player> players;
	private Coordinate[] coins;
	private int gameId;
	private GameState gameState;
	private int collisionRadius = 2;
	
	public Game(int gameId, String name, ObjectInputStream ois, ObjectOutputStream oos) {
		this.gameState = GameState.Lobby;
		this.players = new ArrayList<Player>();
		this.players.add(new Player(this, PlayerType.Pacman, name, ois, oos));
		new Thread(this.players.get(this.players.size()-1)).start();
		System.out.println("Game: Game started with gameId <"+gameId+">");
	}
	
	public boolean isCompleted() {
		return this.gameState == GameState.Completed;
	}

	public boolean inProgress() {
		return this.gameState == GameState.InProgress;
	}
	
	public boolean colisionDetection(Coordinate a, Coordinate b) {
		double[] am = a.toMeters();
		double[] bm = b.toMeters();
	
		return (am[0]-bm[0]) * (am[0]-bm[0]) + (am[1]-bm[1]) * (am[1]-bm[1]) < (this.collisionRadius*2) * (this.collisionRadius*2);
	}
	
	public Coordinate[] updatePlayer(Player p) {
		Coordinate[] coords = new Coordinate[p.getUpdate().size()];
		for(int i = 0; i < p.getUpdate().size(); i++) {
			coords[i] = this.players.get(p.getUpdate().get(i)).getCoord();
		}
		
		return coords;
	}
	
	public boolean addPlayer(String name, ObjectInputStream ois, ObjectOutputStream oos) {
		if(this.players.size() >= 5)
			return false;
		
		this.players.add(new Player(this, PlayerType.Monster, name, ois, oos));
		new Thread(this.players.get(this.players.size()-1)).start();
		
		for(int i = 0; i < this.players.size(); i++)
			players.get(i).lobbyUpdate(name, true);
		
		return true;
	}
	
	public void removePlayer(Player p) {
		String name = this.players.get(this.players.indexOf(p)).getName();
		this.players.remove(p);
		
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

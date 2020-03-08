import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.util.Pair;

public class ConnectionHandler implements Runnable{
	private Socket con;
	private ServerSocket serverSocket;
	private byte[] readIn;
	private boolean isAlive = true;
	private ArrayList<Game> games = new ArrayList<Game>();
	
	private JSONObject info;
    
	public ConnectionHandler(int port){
		try {
			this.readIn = new byte[1024];
			this.serverSocket = new ServerSocket(port);
			
			System.out.println("Handler: Server up and running at "+InetAddress.getByName(new URL("https://cryslacks.win").getHost()).getHostAddress()+":"+port);
			DatabaseHandler.db = new DBFunc();
			this.info = new JSONObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(this.isAlive) {
			try {
				System.out.println("Handler: Waiting for connection");
				this.con = serverSocket.accept();
				System.out.println("Handler: Got connection");

				InputStream is = this.con.getInputStream();
				OutputStream os = this.con.getOutputStream();
				
				int actualRead = is.read(this.readIn);			
				
				System.out.println("Handler: Getting request");
				JSONObject response = new JSONObject(new String(this.readIn));
				if(ServerFunc.debugMode)
					System.out.println("\t"+response);

				System.out.println("Handler: Sending return for the request");
				switch(response.getString("protocol")) {
					case "LOGIN_USER":
						this.info.put("protocol", "LOGIN_USER");
						this.info.put("data", DatabaseHandler.loginAccount(response.getJSONArray("data").getString(0), response.getJSONArray("data").getString(1), response.getJSONArray("data").getString(2)));
						break;
						
					case "CREATE_USER":
						this.info.put("protocol", "CREATE_USER");
						this.info.put("data", DatabaseHandler.createAccount(response.getJSONArray("data").getString(0), response.getJSONArray("data").getString(1), response.getJSONArray("data").getString(2)));
						break;
						
					case "CREATE_GAME":
						this.info.put("protocol", "CREATE_GAME");
						this.games.add(new Game(this, this.games.size(), DatabaseHandler.getNameFromHWID(response.getString("hwid")), is, os));
						this.info.put("data", this.games.get(this.games.size()-1).getPlayers());
						break;
						
					case "JOIN_GAME":
						this.info.put("protocol", "JOIN_GAME");
						try {
							if(this.games.get(response.getInt("data")).addPlayer(DatabaseHandler.getNameFromHWID(response.getString("hwid")), is, os))
								this.info.put("data", this.games.get(response.getInt("data")).getPlayers());						
							else
								this.info.put("data", "ERR_LOBBY_FULL");
						}catch (IndexOutOfBoundsException e) {
							this.info.put("data", "ERR_NO_EXIST");
						}
						break;
						
					case "SAVE_MAP":
						this.info.put("protocol", "SAVE_MAP");
						if(DatabaseHandler.saveMap(response.getJSONObject("data")))
							this.info.put("data", 1);
						else
							this.info.put("data", 0);
							
						break;
						
					case "GET_MAPS":
						this.info.put("protocol", "GET_MAPS");
						Pair<Integer, String>[] data = DatabaseHandler.getMaps();
						String[] names = new String[data.length];
						Integer[] ids = new Integer[data.length];
						for(int i = 0; i < data.length; i++) {
//							System.out.println("\t i="+i+" => \n\t\tk="+data[i].getKey()+" v="+data[i].getValue());
							names[i] = data[i].getValue();
							ids[i] = data[i].getKey();
						}
						
						this.info.append("data", ids);
						this.info.append("data", names);
						break;
						
					default:
						this.info.put("protocol", response.getString("protocol"));
						this.info.put("data", "ERROR_UNSUPPORTED_PROTOCOL");
						break;
				}
				
				ServerFunc.sendMsg(os, this.info);
				
				if(!(this.info.getString("protocol").equals("CREATE_GAME") ^ this.info.getString("protocol").equals("JOIN_GAME"))) {
					System.out.println("Handler: Ending connection");
					this.con.close();					
				}
				this.readIn = new byte[1024];
				this.info = new JSONObject();
			} catch (SocketException e) {
				System.out.println("ERR_DISCONNECTED");
			} catch (JSONException e) {
				System.out.println("ERR_JSON_PARSE");
				System.out.println("Handler: Tried to parse:");
				System.out.println("\t'"+new String(this.readIn)+"'");
				System.out.println("Handler: Ending connection");
				try {
					this.con.close();
				} catch (IOException e1) {
					System.out.println("Handler: ERR_IO_IN_JSON");
					e1.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("ERR_IO");
				e.printStackTrace();
			}
		}
	}
	
	public void removeGame(Game g) {
		System.out.println("Handler: Removing game <"+this.games.indexOf(g)+">");
		this.games.remove(g);
	}
	
	public void terminate() {
		this.isAlive = false;
	}

}

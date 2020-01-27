import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;

public class ConnectionHandler implements Runnable{
	private Socket con;
	private ServerSocket serverSocket;
	private boolean isAlive = true;
	
	private JSONObject info;
    
	public ConnectionHandler(int port){
		try {
			this.serverSocket = new ServerSocket(port);
			AccountHandler.db = new DBFunc();
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

				ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.con.getOutputStream()));
				out.flush();
				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(this.con.getInputStream()));
				
				System.out.println("Handler: Getting request");
				JSONObject response = new JSONObject((String)in.readObject());
				if(ServerFunc.debugMode)
					System.out.println("\t"+response);

				System.out.println("Handler: Sending return for the request");
				switch(response.getString("protocol")) {
					case "LOGIN_USER":
						this.info.put("protocol", "LOGIN_USER");
						this.info.put("data", AccountHandler.loginAccount(response.getJSONArray("data").getString(0), response.getJSONArray("data").getString(1), response.getJSONArray("data").getString(2)));
						ServerFunc.sendMsg(out, this.info);
						break;
						
					case "CREATE_USER":
						this.info.put("protocol", "CREATE_USER");
						this.info.put("data", AccountHandler.createAccount(response.getJSONArray("data").getString(0), response.getJSONArray("data").getString(1), response.getJSONArray("data").getString(2)));
						ServerFunc.sendMsg(out, this.info);
						break;
						
					default:
						this.info.put("protocol", response.getString("protocl"));
						this.info.put("data", "ERROR_UNSUPPORTED_PROTOCOL");
						ServerFunc.sendMsg(out, this.info);
						break;
				}
				if(ServerFunc.debugMode)
					System.out.println("\t"+this.info.toString());
				this.info = new JSONObject();

				System.out.println("Handler: Ending connection");
				this.con.close();
			} catch (SocketException e) {
				System.out.println("ERR_DISCONNECTED");
			} catch (IOException e) {
				System.out.println("ERR_IO");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("ERR_CLASS");
				e.printStackTrace();
			}
		}
	}
	
	
	public void terminate() {
		this.isAlive = false;
	}

}

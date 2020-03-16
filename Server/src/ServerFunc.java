import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;

public class ServerFunc {
	public static boolean debugMode;

	static public boolean sendMsg(OutputStream s, JSONObject j) throws SocketException {
		try {
			if(ServerFunc.debugMode)
				System.out.println("ServerFunc: Sending "+j.toString());
			s.write(j.toString().getBytes());
			s.flush();
		} catch (IOException e) {
			System.out.println("ERR_WRITE_MSG");
			throw new SocketException();
		}
		return true;
	}
}

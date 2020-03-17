import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;
/**
 * Provides easy access of sending data from the server to the client.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */
public class ServerFunc {
	public static boolean debugMode;
	/**
	 * Sends a specified message to a specified client.
	 * @param s The specified client to send the information to.
	 * @param j The specified information which the client shall recieve.
	 * @return A boolean if the message was sent successfully.
	 * @throws SocketException If the client has disconnected.
	 */
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

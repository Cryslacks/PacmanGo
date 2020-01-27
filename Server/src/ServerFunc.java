import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.json.JSONObject;

public class ServerFunc {
	public static boolean debugMode;

	static public boolean sendMsg(ObjectOutputStream s, JSONObject j) {
		try {
			s.writeObject(j.toString());
			s.flush();
		} catch (IOException e) {
			System.out.println("ERR_WRITE_MSG");
			return false;
		}
		return true;
	}
}

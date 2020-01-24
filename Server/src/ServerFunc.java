import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.json.JSONObject;

public class ServerFunc {
	static public boolean sendMsg(ObjectOutputStream s, JSONObject j) {
		try {
			s.writeObject(j.toString());
			s.flush();
			System.out.println("sending info");
		} catch (IOException e) {
			System.out.println("ERR_WRITE_MSG");
			return false;
		}
		return true;
	}

	public static String generateKey() {
		return "0xdeadbeef";
	}
}

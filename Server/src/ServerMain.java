
public class ServerMain {

	public static void main(String[] args) {
			new Thread(new ConnectionHandler()).start();
	}

}

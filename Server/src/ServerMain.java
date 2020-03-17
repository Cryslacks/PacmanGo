/**
 * The main class which the server is started with.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */
public class ServerMain {

	/**
	 * The function which javaw.exe calls on start.
	 * @param args The arguments which are provided from the process start.
	 */
	public static void main(String[] args) {
		if(args.length > 0 && args[0].equals("debug")) {
			ServerFunc.debugMode = true;
			System.out.println("Server debug mode enabled!");
		}
		new Thread(new ConnectionHandler(8080)).start();
	}

}


public class ServerMain {

	public static void main(String[] args) {
		if(args.length > 0 && args[0].equals("debug")) {
			ServerFunc.debugMode = true;
			System.out.println("Server debug mode enabled!");
		}
		new Thread(new ConnectionHandler(8080)).start();
	}

}

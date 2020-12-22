
public class StartServer {

	private int port;
	private Server server;

	public StartServer(int port) {
		this.port = port;
		server = new Server(port);
		System.out.println(port);
	}

	/**
	 * MAIN
	 */
	public static void main(String[] args) {
		int port;

		if (args.length != 1) {
			System.out.println("You are missing a port argument!");
			return;
		}
		port = Integer.parseInt(args[0]);
		new StartServer(port);
	}

}

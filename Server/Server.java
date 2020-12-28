import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class Server implements Runnable {

	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<>();

	private int port;
	private DatagramSocket socket;
	private boolean serverIsRunning = false;
	private Thread run;
	private Thread manageClients;
	private Thread send;
	private Thread receive;

	private final int MAX_ATTEMPTS = 10;
	private final int DATA_SIZE = 1024;

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}

		run = new Thread(this, "Server Runs Thread");
		run.start();
	}

	public void run() {
		serverIsRunning = true;
		System.out.println("Server is running on port: " + port + " line 38 in Server.java"); // can delete later
		clientsManager();
		clientDataReceiver();
	}

	private void clientsManager() {
		manageClients = new Thread("Manage Thread") {
			public void run() {
				while (serverIsRunning) {
					allClientsSender("/inf/server");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (!clientResponse.contains(c.getID())) {
							if (c.attempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							} else {
								c.attempt++;
							}
						} else {
							clientResponse.remove(Integer.valueOf(c.getID()));
							c.attempt = 0;
						}
					}
				}
			}
		};
		manageClients.start();
	}

	private void clientDataReceiver() {
		receive = new Thread("Receive Thread") {
			public void run() {
				while (serverIsRunning) {
					byte[] data = new byte[DATA_SIZE];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					inputDataProcessor(packet);
				}
			}
		};
		receive.start();
	}

	private void allClientsSender(String message) {
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}
	}

	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send Thread") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	private void send(String message, InetAddress address, int port) { // for command starting with "/con/"
		message = message + "/end/";
		send(message.getBytes(), address, port);

	}

	private void inputDataProcessor(DatagramPacket packet) {
		String string = new String(packet.getData());
		if (string.startsWith("/con/")) {
			int id = UniqueIdGenerator.getIdentifier();
			String name = string.split("/con/|/end/")[1];
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			String ID = "/con/" + id;
			send(ID, packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/mes/")) {
			allClientsSender(string);
		} else if (string.startsWith("/dsc/")) {
			String id = string.split("/dsc/|/end/")[1];
			disconnect(Integer.parseInt(id), true);
		} else if (string.startsWith("/inf/")) {
			clientResponse.add(Integer.parseInt(string.split("/inf/|/end/")[1]));
		} else {
			System.out.println(string + " ERROR: inputDataProcessor else statemenet executed Line 134 Server.java");
		}
	}

	private void disconnect(int id, boolean status) {
		ServerClient c = null;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				break;
			}
		}
		String statusMessage = "";
		if (status) {
			statusMessage = "Client " + c.name + " (ID:" + c.getID() + ") " + " Address: " + c.address.toString() + ":"
					+ c.port + " got disconected";

		} else {
			statusMessage = "Client " + c.name + " (ID:" + c.getID() + ") " + " Address: " + c.address.toString() + ":"
					+ c.port + " timed out";

		}
		System.out.println(statusMessage);
		statusMessage = "/mes/" + statusMessage + "\n" + "/end/";
		allClientsSender(statusMessage);
	}

	
	//generates unique IDs for each client
	static class UniqueIdGenerator {

		private static List<Integer> ids = new ArrayList<Integer>();
		private static final int RANGE = 1000;

		private static int index = 0;

		static {
			for (int i = 0; i < RANGE; i++) {
				ids.add(i);
			}
			Collections.shuffle(ids);
		}

		private UniqueIdGenerator() {
		}

		public static int getIdentifier() {
			if (index > ids.size() - 1)
				index = 0;
			return ids.get(index++);
		}

	}
	
	//ServerClient Class holds the information all connected clients
	public static class ServerClient {

		public String name;
		public InetAddress address;
		public int port;
		public final int ID;
		public int attempt = 0;

		public ServerClient(String name, InetAddress address, int port, final int ID) {
			this.name = name;
			this.address = address;
			this.port = port;
			this.ID = ID;
		}

		public int getID() {
			return ID;
		}

	}

}// end server

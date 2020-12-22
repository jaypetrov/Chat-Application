import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;

	private String name;
	private String ipAddress;
	private int port;

	private DatagramSocket socket;
	private InetAddress ip;
	private Thread send;

	private final int DATA_SIZE = 1024;

	private int ID = -1;

	public Client(String name, String ipaddress, int port) {
		this.name = name;
		this.ipAddress = ipaddress;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpaddress() {
		return ipAddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipAddress = ipaddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	// handles if the client connects to the server
	public boolean startConnection(String address) {
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String receive() {
		byte[] data = new byte[DATA_SIZE];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String receivedData = new String(packet.getData());
		return receivedData;
	}

	public void sendData(final byte[] data) {
		send = new Thread("Send Data") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		send.start();
	}

	public void close() {
		new Thread() {
			public void run() {
				synchronized (socket) {
					socket.close();
				}
			}
		}.start();
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public int getID() {
		return ID;
	}

//	//may delete later
//	private void userJoinInfo() {
//		chatHistory.append(name + " has joined the chat!\n");
//	}

}

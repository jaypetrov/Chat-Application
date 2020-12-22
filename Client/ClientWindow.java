import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea chatHistory;
	private GridBagLayout gridContentPane;
	private GridBagConstraints gridTxtrChathistory;
	private GridBagConstraints gridTxtMessage;
	private JButton buttonSend;
	private JScrollPane scroll;
	private GridBagConstraints gridBtnSend;

	private DefaultCaret caret;
	private Thread listen;
	private Thread run;
	private Client client;

	private boolean running = false;

	public ClientWindow(String name, String address, int port) {
		client = new Client(name, address, port);
		boolean connected = client.startConnection(address);
		if (!connected) {
			System.out.println("Failed to connect to address");
		}
		mainChatFrame();
		consoleWriter(name + " attempted to connect to address: " + address + " & port: " + port + "\n");
		String connection = "/con/" + name + "/end/";
		client.sendData(connection.getBytes());
		running = true;
		run = new Thread(this, "Running");
		run.start();

	}

	public void mainChatFrame() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setTitle("Jay's Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(860, 620);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		gridContentPane = new GridBagLayout();
		gridContentPane.columnWidths = new int[] { 10, 735, 80, 10 };
		gridContentPane.rowHeights = new int[] { 50, 530, 40 };
		gridContentPane.columnWeights = new double[] { 1.0, 1.0 };
		gridContentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gridContentPane);

		chatHistory = new JTextArea();
		chatHistory.setEditable(false);
		scroll = new JScrollPane(chatHistory);
		caret = (DefaultCaret) chatHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		gridTxtrChathistory = new GridBagConstraints();
		gridTxtrChathistory.insets = new Insets(0, 0, 5, 5);
		gridTxtrChathistory.fill = GridBagConstraints.BOTH;
		gridTxtrChathistory.gridx = 0;
		gridTxtrChathistory.gridy = 0;
		gridTxtrChathistory.gridwidth = 3;
		gridTxtrChathistory.gridheight = 2;
		gridTxtrChathistory.insets = new Insets(10, 0, 0, 0);
		contentPane.add(scroll, gridTxtrChathistory);

		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					messageSender(txtMessage.getText(), true);
				}
			}
		});

		gridTxtMessage = new GridBagConstraints();
		gridTxtMessage.insets = new Insets(0, 0, 0, 5);
		gridTxtMessage.fill = GridBagConstraints.HORIZONTAL;
		gridTxtMessage.gridx = 0;
		gridTxtMessage.gridy = 2;
		gridTxtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gridTxtMessage);
		txtMessage.setColumns(10);

		buttonSend = new JButton("Send");
		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				messageSender(txtMessage.getText(), true);
			}
		});

		gridBtnSend = new GridBagConstraints();
		gridBtnSend.anchor = GridBagConstraints.EAST;
		gridBtnSend.insets = new Insets(0, 0, 0, 5);
		gridBtnSend.gridx = 2;
		gridBtnSend.gridy = 2;
		contentPane.add(buttonSend, gridBtnSend);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String disconnect = "/dsc/" + client.getID() + "/end/";
				messageSender(disconnect, false);
				running = false;
				client.close();
			}
		});

		txtMessage.requestFocusInWindow();

		getContentPane().setBackground(new Color(0, 250, 154));
	}

	public void run() {
		serverListener();
	}

	private void messageSender(String msg, boolean isMessage) {
		if (msg.equals(""))
			return;
		if (isMessage) {
			msg = "/mes/" + client.getName() + ": " + msg + "\n";
			txtMessage.setText("");
			chatHistory.setCaretPosition(chatHistory.getDocument().getLength()); // maybe delete check if it keeps focus
		}
		client.sendData(msg.getBytes());
		txtMessage.requestFocusInWindow();
	}

	public void serverListener() {
		listen = new Thread("Listen Thread from server") {
			public void run() {
				while (running) {
					String receivedData = client.receive();
					if (receivedData.startsWith("/con/")) {
						client.setID(Integer.parseInt(receivedData.split("/con/|/end/")[1]));
						consoleWriter("You connected to the server with ID: " + client.getID() + "\n");
					} else if (receivedData.startsWith("/m")) {
						String msg = receivedData.split("/mes/|/end/")[1];
						chatHistory.append(msg);
					} else if (receivedData.startsWith("/inf/")) {
						String text = "/inf/" + client.getID() + "/end/";
						messageSender(text, false);
					}
				}
			}
		};
		listen.start();
	}

	// writes to the client console
	private void consoleWriter(String s) {
		chatHistory.append(s);
		chatHistory.setCaretPosition(chatHistory.getDocument().getLength());
	}

}

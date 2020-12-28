import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class LogInWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton buttonLogin;
	private JLabel labelUserName;
	private JLabel labelIpAddress;
	private JLabel labelPort;
	private JPanel contentPane;
	private JTextField textFieldName;
	private JTextField textFieldIpAddress;
	private JTextField textFieldPort;
	private JLabel labelpExample;
	private JLabel labelPortExample;

	private String name;
	private String ipAddress;
	private int port;

	/**
	 * Create the frame.
	 */
	public LogInWindow() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 200, 420, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(60, 179, 113), 12));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);

		buttonLogin = new JButton("Login");
		buttonLogin.setVerticalAlignment(SwingConstants.BOTTOM);
		buttonLogin.setFont(new Font("Calibri", Font.BOLD, 15));
		buttonLogin.setBackground(Color.black);
		buttonLogin.setOpaque(true);
		buttonLogin.setForeground(Color.black);
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startChat();
				System.out.println("Username: " + name + " IP Adress: " + ipAddress + " Port: " + port + "\n");
			}
		});

		buttonLogin.setBounds(162, 247, 115, 33);
		contentPane.add(buttonLogin);

		labelUserName = new JLabel("User Name"); //
		labelUserName.setFont(new Font("Calibri", Font.BOLD, 15));
		labelUserName.setBounds(36, 40, 79, 14);
		contentPane.add(labelUserName);

		labelIpAddress = new JLabel("IP Address"); //
		labelIpAddress.setFont(new Font("Calibri", Font.BOLD, 15));
		labelIpAddress.setBounds(36, 106, 94, 14);
		contentPane.add(labelIpAddress);

		labelPort = new JLabel("Port #"); //
		labelPort.setFont(new Font("Calibri", Font.BOLD, 15));
		labelPort.setBounds(46, 184, 84, 14);
		contentPane.add(labelPort);

		textFieldName = new JTextField();
		textFieldName.setBounds(140, 29, 168, 33);
		contentPane.add(textFieldName);
		textFieldName.setColumns(10);

		textFieldIpAddress = new JTextField();
		textFieldIpAddress.setColumns(10);
		textFieldIpAddress.setBounds(140, 97, 168, 33);
		contentPane.add(textFieldIpAddress);

		textFieldPort = new JTextField();
		textFieldPort.setForeground(Color.BLACK);
		textFieldPort.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					startChat();
					System.out.println("Username: " + name + " IP Adress: " + ipAddress + " Port: " + port);
				}
			}
		});
		textFieldPort.setColumns(10);
		textFieldPort.setBounds(140, 175, 168, 33);
		contentPane.add(textFieldPort);

		// set background color
		getContentPane().setBackground(new Color(0, 250, 154));

		labelpExample = new JLabel("e.g. 192.113.84.239");
		labelpExample.setBounds(172, 131, 105, 14);
		contentPane.add(labelpExample);

		labelPortExample = new JLabel("e.g. 8889");
		labelPortExample.setBounds(198, 208, 58, 14);
		contentPane.add(labelPortExample);

	}

	private void startChat() {
		this.name = textFieldName.getText();
		this.ipAddress = textFieldIpAddress.getText();
		this.port = Integer.parseInt(textFieldPort.getText());
		new ClientWindow(name, ipAddress, port);
		dispose();
	}

	/**
	 * MAIN
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					LogInWindow frame = new LogInWindow();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

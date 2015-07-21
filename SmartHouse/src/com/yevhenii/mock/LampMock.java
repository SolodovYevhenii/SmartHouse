package com.yevhenii.mock;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
public class LampMock {
	private JFrame frame;
	private Label label;
	private boolean st = false;
	private String my_ip;
	private String my_mac;
	private ServerSocket serverSock;
	public static void main(String[] args) {
		LampMock l = new LampMock(args[0], args[1]);
		l.go();
	}
	private LampMock(String i, String m) {
		my_ip = i;
		my_mac = m;
	}
	public void go() {
		frame = new JFrame("Lamp " + my_mac);
		label = new Label();
		Font bigestFont = new Font("serif", Font.BOLD, 28);
		label.setFont(bigestFont);
		MyDrawPanel drawPanel = new MyDrawPanel();
		frame.getContentPane().add(BorderLayout.CENTER, drawPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, label);
		Thread myThread = new Thread(new MyRunnable());
		myThread.start();
		try {
			Socket sock = new Socket("127.0.0.1", 5000);
			PrintWriter	writer = new PrintWriter(sock.getOutputStream());
			String s = new String("add/" + my_mac + "/" + my_ip);
			writer.println(s);
			writer.flush();
			sock.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		frame.setSize(255,210);
		frame.setVisible(true);
		frame.addWindowListener(new WindowEventHandler());
	}
	public void turnOn() {
		st = true;
		frame.repaint();
	}
	public void turnOff() {
		st = false;
		frame.repaint();
	}
	public class MyDrawPanel extends JPanel {
		private static final long serialVersionUID = 2791739641522251422L;
		public void paintComponent(Graphics g) {
			if (st) {
				g.setColor(Color.YELLOW);
				g.fillOval(65, 20, 120, 120);
				label.setText("The Lamp is on");
			} else {
				g.setColor(Color.BLACK);
				g.fillOval(65, 20, 120, 120);
				label.setText("The Lamp is off");
			}
		}
	}
	public class MyRunnable implements Runnable {
		public void run() {
			try {
				InetAddress ia = InetAddress.getByName(my_ip);
				serverSock = new ServerSocket(6000, 50, ia);
				while(true) {
					Socket clientSocket = serverSock.accept();
					InputStreamReader isReader = new InputStreamReader(clientSocket.getInputStream());
					BufferedReader reader = new BufferedReader(isReader);
					String message = null;
					while ((message = reader.readLine()) != null) {
						if (message.equals("on")) {
							turnOn();
						} else if (message.equals("off")) {
							turnOff();
						}
					}
					reader.close();
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	public class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			try {
				serverSock.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			frame.dispose();
		}
	}
}
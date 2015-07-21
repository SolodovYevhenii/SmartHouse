package com.yevhenii.client;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
public class LampClient {
	ArrayList<LampHandler> handlers;
	Socket sock;
	BufferedReader reader;
	PrintWriter writer;
	public static void main(String[] args) {
		new LampClient().go();
	}
	public void go() {
		try {
			sock = new Socket("127.0.0.1", 5000);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new PrintWriter(sock.getOutputStream());
			handlers = new ArrayList<LampHandler>();
			writer.println("get");
			writer.flush();
			String mac = null;
			while (!"#".equals(mac = reader.readLine())) {
				handlers.add(new LampHandler(mac, writer));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		buildGUI();
	}
	public void buildGUI() {
		JFrame frame = new JFrame("Lamp Client");
		GridLayout grid = new GridLayout(handlers.size(),2,5,5);
		grid.setVgap(3);
		JPanel mainPanel = new JPanel(grid);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		for (LampHandler lampHandler:handlers) {
			JLabel label = new JLabel(lampHandler.getMac());
			lampHandler.setLabel(label);
			JButton button = new JButton("Turn On");
			lampHandler.setButton(button);
			button.addActionListener(lampHandler);
			mainPanel.add(label);
			mainPanel.add(button);
		}
		frame.setBounds(100,100,275,80);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}

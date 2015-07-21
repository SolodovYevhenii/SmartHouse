package com.yevhenii.server;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
public class LampServer {
	HashMap<String, String> LampList;
	public static void main (String[] args) {
		new LampServer().go();
	}
	public void go() {
		LampList = new HashMap<String, String>();
		try {
			ServerSocket serverSock = new ServerSocket(5000);
			while(true) {
				Socket clientSocket = serverSock.accept();
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	private synchronized void addLamp(String mac, String ip) {
		LampList.put(mac, ip);
	}
	private synchronized void getLampList(PrintWriter pw) {
		Iterator<String> it = LampList.keySet().iterator();
		while(it.hasNext()) {
			pw.println(it.next());
			pw.flush();
		}
		pw.println("#");
		pw.flush();
	}
	private synchronized void turnOnLamp(String mac) {
		String ip = LampList.get(mac);
		try {
			Socket lampSock = new Socket(ip, 6000);
			PrintWriter writer = new PrintWriter(lampSock.getOutputStream());
			writer.print("on");
			writer.close();
			lampSock.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private synchronized void turnOffLamp(String mac) {
		String ip = LampList.get(mac);
		try {
			Socket lampSock = new Socket(ip, 6000);
			PrintWriter writer = new PrintWriter(lampSock.getOutputStream());
			writer.print("off");
			writer.close();
			lampSock.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket sock;
		PrintWriter pw;
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
				pw = new PrintWriter(clientSocket.getOutputStream());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		public void run() {
			String message = null;
			try {
				while ((message = reader.readLine()) != null) {
					String[] result = message.split("/");
					if (result[0].equals("add")) {
						addLamp(result[1], result[2]);
					} else if (result[0].equals("get")) {
						getLampList(pw);
					} else if (result[0].equals("on")) {
						turnOnLamp(result[1]);
					} else if (result[0].equals("off")) {
						turnOffLamp(result[1]);
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
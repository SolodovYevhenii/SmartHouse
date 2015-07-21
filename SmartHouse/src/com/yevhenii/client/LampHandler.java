package com.yevhenii.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JLabel;
public class LampHandler implements ActionListener {
	private String mac;
	private JLabel label;
	private JButton button;
	private PrintWriter writer;
	public LampHandler(String mymac, PrintWriter pw) {
		mac = mymac;
		writer = pw;
	}
	public String getMac() {
		return mac;
	}
	public void setLabel(JLabel l) {
		label = l;
	}
	public void setButton(JButton b) {
		button = b;
	}
	public void actionPerformed(ActionEvent ev) {
		if (button.getText().equals("Turn On")) {
			button.setText("Turn Off");
			writer.println("on/" + mac);
			writer.flush();
		} else {
			button.setText("Turn On");
			writer.println("off/" + mac);
			writer.flush();
		}
	}
}
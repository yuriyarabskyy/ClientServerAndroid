package com.example.yuriy.simpleclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.TextView;
import java.io.*;
import java.net.*;

public class Client extends Thread {

    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

	private static Handler handler = new Handler(Looper.getMainLooper());

	private TextView dialog;

	private MainActivity act;

	public static Client instance = new Client();


	public Client() { }

	public void setActivity(MainActivity act) { this.act = act; }
	public void setTextView(TextView dialog) { this.dialog = dialog; }

    public void run() {

		setUpNetworking();

		IncomingReader increader = IncomingReader.instance;
		increader.start();

		increader.setReader(reader);
		increader.setDialog(dialog);

		act.setReader(reader);
		act.setSock(sock);
		act.setWriter(writer);

		instance = new Client();
		instance.setActivity(act);
		instance.setTextView(dialog);
    }

     private void setUpNetworking() {
		 try {

			 Context context = act.getApplicationContext();
			 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

			 String ipaddress = prefs.getString(MyPreferenceActivity.IPCONFIG, "192.168.1.107");
			 int tcpPort = Integer.parseInt(prefs.getString(MyPreferenceActivity.TCPCONFIG, "8080"));

			 sock = new Socket(ipaddress, tcpPort);
			 InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			 reader = new BufferedReader(streamReader);
			 writer = new PrintWriter(sock.getOutputStream());
			 handler.post(new Runnable() {
				 @Override
				 public void run() {
					 dialog.append("\nNetworking established");
				 }
			 });
		 } catch (IOException ex) {
			 ex.printStackTrace();
		 }
	 }


		public static class IncomingReader extends Thread {

			private BufferedReader reader;
			private TextView dialog;

			public static IncomingReader instance = new IncomingReader();
			public static IncomingReader runningInstance;

			private IncomingReader() { }

			public void setReader(BufferedReader reader) { this.reader = reader; }
			public void setDialog(TextView dialog) { this.dialog = dialog; }

			public void run() {
				runningInstance = instance;
				instance = new IncomingReader();
				String message;
	    		try {
					while ((message = reader.readLine()) != null) {
							Thread.sleep(500);
							final String msg = message;
							handler.post(new Runnable() {
								@Override
								public void run() {
									dialog.append("\nRead from server: " + msg);
								}
							});
						}
				} catch (InterruptedException ex) {
					return;
				} catch(Exception ex) { ex.printStackTrace(); }
				}
    }
}
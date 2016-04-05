package com.example.yuriy.simpleserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    List<PrintWriter> clientOutputStreams;
	Scanner scanner;

    public class ClientHandler implements Runnable {
	BufferedReader reader;
	Socket sock;

	public ClientHandler(Socket clientSocket) {
	    try {
		sock = clientSocket;
		InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(isReader);
	    } catch(Exception ex) { ex.printStackTrace(); }
	}

	public void run() {
	    String message;
	    try {
		while ((message = reader.readLine()) != null) {
		     System.out.println("read " + message);
		}
	    } catch(Exception ex) { ex.printStackTrace(); }
	}
    }

	public static void main(String[] args) {
	    new Server().go();
	}

	public void go() {
	    clientOutputStreams = new ArrayList<>();
		scanner = new Scanner(System.in);
	    try {
		ServerSocket serverSock = new ServerSocket(8080);
		
		while(true) {
		    Socket clientSocket = serverSock.accept();
		    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
		    clientOutputStreams.add(writer);

		    Thread t = new Thread(new ClientHandler(clientSocket));
		    t.start();
		    new Sender().start();
		    System.out.println("got a connection");
		}
	    } catch(Exception ex) {
		ex.printStackTrace();
	    }
	}

    public void tellEveryone(String message) {
	
	Iterator<PrintWriter> it = clientOutputStreams.iterator();
	while(it.hasNext()) {
	    try {
		PrintWriter writer = it.next();
		writer.println(message);
		writer.flush();
	    } catch (Exception ex) { ex.printStackTrace(); }
	}
	
    }

    private class Sender extends Thread {

		@Override
		public void run() {
			String servermsg;
			while (true) {
					if (scanner.hasNext() && (servermsg = scanner.nextLine()) != null)
						tellEveryone(servermsg);
			}
		}
	}
	    
}

package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class TCPClient extends Thread {
	
	private class SendThread extends Thread {
		Semaphore queue = new Semaphore(0);
		PrintWriter pr = null;
		
		public void run () {
			queue.release();
		}
		
		public void pushMessage (String msg) {
			try {
				queue.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			try {
				if (pr == null) {					
					pr = new PrintWriter(socket.getOutputStream(), true);
				}
				
				pr.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			queue.release();
		}
	}
	
	private class ReceiveThread extends Thread {
		BufferedReader clientIn = null;
		
		public void run () {
			
			if (clientIn == null) {
				try {
					clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			while (socket.isConnected()) {
				String msg = "";
				String input;
				try {
					input = clientIn.readLine();
					
					if (input.startsWith("#STARTOF")) {
						while (!(input = clientIn.readLine()).startsWith("#ENDOF")) {
							msg += input + "\n";
						}						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}		
				
				TCPClient cl = TCPClient.this;
				
				cntrl.handleMessage(cl, msg);
			}
		}
	}
	
	public static enum PERMISSION {
		CHAT, WHISPER
	}
	private static int ID_GEN = 0;
	
	private Socket socket;
	private TCPController cntrl;
	private SendThread sender = null;
	private ReceiveThread receiver = null;
	boolean active = false;
	
	private String name;
	private int id;
	
	private HashMap<PERMISSION, Boolean> permissions = new HashMap<PERMISSION, Boolean>();
	
	public TCPClient (Socket so) {
		socket = so;
		receiver = new ReceiveThread();
		sender = new SendThread();
		id = ID_GEN;
		
		ID_GEN += 1;
		
		// Beim erstmaligen Verbinden darf jeder Client chatten.
		this.promote(PERMISSION.CHAT);
		this.promote(PERMISSION.WHISPER);
	}
	
	public void pushMessage (MessageProtocol message) {
		this.sender.pushMessage(message.toString());
	}
	
	public boolean hasPermission (PERMISSION perm) {
		return permissions.get(perm) == true;
	}
	
	public void promote (PERMISSION perm) {
		this.permissions.put(perm, true);
	}
	
	public void demote (PERMISSION perm) {
		if (this.hasPermission(perm)) {
			this.permissions.put(perm, false);
		}
	}
	
	public Socket getConnection() {
		return this.socket;
	}
	
	public String getUsername() {
		return this.name;
	}
	
	public int getID () {
		return this.id;
	}
	
	public void run () {
		receiver.start();
		sender.start();
	}
}

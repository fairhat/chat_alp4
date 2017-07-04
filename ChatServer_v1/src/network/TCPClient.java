package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class TCPClient extends Thread {
	
	/**
	 * Die Berechtigungen, die existieren
	 */
	public static enum PERMISSION {
		CHAT, WHISPER
	}

	private static int ID_GEN = 0;
	
	private Socket socket;
	private TCPController cntrl;
	private SendThread sender = null;
	private ReceiveThread receiver = null;
	boolean active = false;
	
	private String uname;
	private int id;
	
	/**
	 * Die Berechtigungen eines Clients
	 */
	private HashMap<PERMISSION, Boolean> permissions = new HashMap<PERMISSION, Boolean>();
	
	public TCPClient (Socket so) {
		socket = so;
		receiver = new ReceiveThread();
		sender = new SendThread();
		cntrl = TCPController.get();
		id = ID_GEN;
		
		ID_GEN += 1;
		
		// Beim erstmaligen Verbinden darf jeder Client chatten und flüstern.
		this.promote(PERMISSION.CHAT);
		this.promote(PERMISSION.WHISPER);
	}
	
	/**
	 * Eine Nachricht an einen Client schicken
	 * Ruft den "Sender" Thread auf
	 * @param message
	 */
	public void pushMessage (MessageProtocol message) {
		this.sender.pushMessage(message.toString());
	}
	
	/**
	 * Eine Nachricht an den Client schicken
	 * Wenn sanitized == true, wird der command teil der nachricht entfernt.
	 * 
	 * Beispiel: "/private test abc" => "abc"
	 * 
	 * @param message
	 * @param sanitized
	 */
	public void pushMessage (MessageProtocol message, boolean sanitized) {
		if (sanitized) {
			this.sender.pushMessage(message.toSanitizedString());
		} else {
			this.sender.pushMessage(message.toString());			
		}
	}
	
	/**
	 * Prüft, ob der Nutzer die Berechtigung hat
	 * @param perm
	 * @return
	 */
	public boolean hasPermission (PERMISSION perm) {
		return permissions.get(perm) == true;
	}
	
	/**
	 * Gibt dem Nutzer eine neue Berechtigung
	 * @param perm
	 */
	public void promote (PERMISSION perm) {
		this.permissions.put(perm, true);
	}
	
	/**
	 * Entzieht dem Nutzer eine Berechtigung
	 * @param perm
	 */
	public void demote (PERMISSION perm) {
		if (this.hasPermission(perm)) {
			this.permissions.put(perm, false);
		}
	}
	
	/**
	 * Gibt die Socket Verbindung zurück
	 * @return
	 */
	public Socket getConnection() {
		return this.socket;
	}
	
	public void setUsername(String name) {
		this.uname = name;
	}
	
	public String getUsername() {
		return this.uname;
	}
	
	public int getID () {
		return this.id;
	}
	
	public void run () {
		active = true;
		// Startet einen Receiver Thread, der in einer Schleife auf eingehende Nachrichten wartet.
		receiver.start();
		
		// Startet einen Sender Thread, der ausgehende Nachrichten abarbeitet
		sender.start();
	}
	
	public void shutdown () {
		this.cntrl.clientDisconnected(this);
		active = false;
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect () {
		this.shutdown();
	}
	
	private class SendThread extends Thread {
		Semaphore queue = new Semaphore(0);
		PrintWriter pr = null;
		
		@SuppressWarnings("unused")
		public void shutdown () {
			if (pr != null) {
				pr.close();
			}
		}
		
		@Override
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
		
		@SuppressWarnings("unused")
		public void shutdown () {
			if (clientIn != null) {
				try {
					clientIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void run () {
			
			if (clientIn == null) {
				try {
					clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			while (active) {
				String msg = "";
				String input;
				try {
					input = clientIn.readLine();
					
					if (input == null) {
						TCPClient.this.shutdown();
					}
					
					if (input != null && input.startsWith("#STARTOF") && active) {
						while (!(input = clientIn.readLine()).startsWith("#ENDOF")) {
							msg += input + "\n";
						}
						
						TCPClient cl = TCPClient.this;

						cntrl.handleMessage(cl, msg);
					}
					
				} catch (IOException e) {
					//e.printStackTrace();
				}		
			}
		}
	}
}

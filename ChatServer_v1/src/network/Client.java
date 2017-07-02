package network;

import java.net.Socket;
import java.util.HashMap;

public class Client {
	
	private String name;
	public static enum PERMISSION {
		KICK, MUTE, CHAT
	};
	private HashMap<PERMISSION, Boolean> permissions = new HashMap<PERMISSION, Boolean>();
	private Socket connection;
	private int id;
	
	private static int ID_GEN = 0;
	
	
	/**
	 * Erstellt einen Client
	 * @param name
	 * @param connection
	 */
	public Client (String name, Socket connection) {
		this.name = name;
		this.connection = connection;
		this.id = ID_GEN;
		
		ID_GEN += 1;
		
		// Beim Verbinden darf jeder Client chatten.
		this.promote(PERMISSION.CHAT);
	}
	
	/**
	 * Wrapper für Message Parsing.
	 * 
	 * @param connMsg
	 * @return
	 */
	public static ClientMessage parseMessage (String connMsg) {
		return new ClientMessage(connMsg);
	}
	
	/**
	 * Prüft, ob ein Client die angegebene Berechtigung hat.
	 * 
	 * @param perm
	 * @return
	 */
	public boolean hasPermission (PERMISSION perm) {
		return permissions.get(perm) == true;
	}
	
	/**
	 * Verleiht dem Client eine Berechtigung (wenn noch nicht geschehen)
	 * 
	 * @param perm
	 */
	public void promote (PERMISSION perm) {
		this.permissions.put(perm, true);
	}
	
	/**
	 * Nimmt dem Client eine Berechtigung (wenn sie existiert)
	 * 
	 * @param perm
	 */
	public void demote (PERMISSION perm) {
		if (this.hasPermission(perm)) {
			this.permissions.put(perm, false);
		}
	}
	
	/**
	 * Socket Verbindung abrufen.
	 * 
	 * @return
	 */
	public Socket getConnection () {
		return this.connection;
	}
	
	/**
	 * Username des Clients.
	 * 
	 * @return
	 */
	public String getName () {
		return this.name;
	}
	
	public int getId () {
		return this.id;
	}
}

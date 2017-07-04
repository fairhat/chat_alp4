package network;

import java.util.HashMap;
import java.util.Set;

/**
 * Die Klasse verwaltet die verbundenen Client Threads
 * Sie werden intern in einer HashMap <ID, Client> abgespeichert
 * 
 * SINGLETON
 *
 */
public class TCPClientManager {
	
	private static TCPClientManager instance = null;
	private HashMap<Integer, TCPClient> list;
	
	private TCPClientManager () {
		list = new HashMap<Integer, TCPClient>();
	}
	
	/**
	 * Instanz abrufen
	 * @return
	 */
	public static TCPClientManager get() {
		if (instance == null) {
			instance = new TCPClientManager();
		}
		
		return instance;
	}
	
	/**
	 * Gibt an, ob ein Client bereits registriert wurde (nach Id)
	 * @param Id
	 * @return
	 */
	public boolean hasClient (int Id) {
		return list.get(Id) != null;
	}
	
	/**
	 * Gibt an, ob ein Client bereits registriert wurde (nach Client Instanz)
	 * @param client
	 * @return
	 */
	public boolean hasClient (TCPClient client) {
		Set<Integer> keys = list.keySet();
		
		for (int key : keys) {
			TCPClient c = list.get(key);
			
			if (c == client) return true;
		}
		
		return false;
	}
	
	/**
	 * Sucht einen Client nach seinem Namen
	 * @param name
	 * @return
	 */
	public TCPClient getClientByName (String name) {
		Set<Integer> keys = list.keySet();
		
		for (int key : keys) {
			TCPClient c = list.get(key);
			
			if (c.getUsername().equals(name)) {
				return c;
			}
		}
		
		return null;
	}
	
	/**
	 * Sucht einen Client nach seiner ID
	 * @param Id
	 * @return
	 */
	public TCPClient getClient (int Id) {
		return list.get(Id);
	}
	
	
	/**
	 * Sucht einen Client nach seiner client Instanz
	 * @param client
	 * @return
	 */
	public String getClientName (TCPClient client) {
		Set<Integer> keys = list.keySet();
		
		for (int key : keys) {
			TCPClient c = list.get(key);
			
			if (c == client) return client.getUsername();
		}
		
		return null;
	}
	
	/**
	 * Gibt alle Namen der Clients zurück
	 * @return
	 */
	public String[] getAllNames() {
		return list.keySet().toArray(new String[0]);
	}
	
	/**
	 * Gibt alle Clients als Array zurück
	 * @return
	 */
	public TCPClient[] getAllClients() {
		TCPClient[] arr = new TCPClient[list.size()];
		
		Set<Integer> keys = list.keySet();
		
		int i = 0;
		for (int key : keys) {
			TCPClient c = list.get(key);
			arr[i] = c;
			
			i += 1;
		}
		return arr;
	}
	
	/**
	 * Fügt einen Client hinzu
	 * @param client
	 */
	public void addClient (TCPClient client) {
		if (!hasClient(client.getID())) {
			list.put(client.getID(), client);
		}
	}
	
	/**
	 * Aktualisiert den Namen eines Clients
	 * Hinweis: funktioniert nur, wenn der client auch registriert wurde
	 * @param name
	 * @param client
	 */
	public void updateClient (String name, TCPClient client) {
		if (hasClient(client.getID())) {
			client.setUsername(name);
		}
	}
	
	/**
	 * Löscht einen Client nach ID
	 * @param id
	 * @return
	 */
	public TCPClient deleteClient (int id) {
		if (hasClient(id)) {
			TCPClient client = list.get(id);
			
			
			list.remove(id);
			
			return client;
		}
		
		return null;
	}
	
	/**
	 * Löscht einen Client nach Instanz
	 * @param client
	 */
	public void deleteClient (TCPClient client) {
		if (hasClient(client.getID())) {
			list.remove(client.getID());
		}
	}
	
	/**
	 * Cleanup Methode
	 */
	public void shutdown () {
		for (TCPClient cl : this.getAllClients()) {
			cl.shutdown();
			
			list.remove(cl.getID());
		}
	}
}

package network;

import java.util.HashMap;
import java.util.Set;

public class TCPClientManager {
	
	private static TCPClientManager instance = null;
	private HashMap<Integer, TCPClient> list;
	
	private TCPClientManager () {
		list = new HashMap<Integer, TCPClient>();
	}
	
	public static TCPClientManager get() {
		if (instance == null) {
			instance = new TCPClientManager();
		}
		
		return instance;
	}
	
	public boolean hasClient (int Id) {
		return list.get(Id) != null;
	}
	
	public boolean hasClient (TCPClient client) {
		Set<Integer> keys = list.keySet();
		
		for (int key : keys) {
			TCPClient c = list.get(key);
			
			if (c == client) return true;
		}
		
		return false;
	}
	
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
	
	public TCPClient getClient (int Id) {
		return list.get(Id);
	}
	
	public String getClientName (TCPClient client) {
		Set<Integer> keys = list.keySet();
		
		for (int key : keys) {
			TCPClient c = list.get(key);
			
			if (c == client) return client.getUsername();
		}
		
		return null;
	}
	
	public String[] getAllNames() {
		return list.keySet().toArray(new String[0]);
	}
	
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
	
	public void addClient (TCPClient client) {
		if (!hasClient(client.getID())) {
			list.put(client.getID(), client);
		}
	}
	
	public void updateClient (String name, TCPClient client) {
		if (hasClient(client.getID())) {
			client.setUsername(name);
		}
	}
	
	public TCPClient deleteClient (int id) {
		if (hasClient(id)) {
			TCPClient client = list.get(id);
			
			
			list.remove(id);
			
			return client;
		}
		
		return null;
	}
	
	public void deleteClient (TCPClient client) {
		if (hasClient(client.getID())) {
			list.remove(client.getID());
		}
	}
	
	public void shutdown () {
		System.out.println("shutting: " + this.getAllClients().length);
		for (TCPClient cl : this.getAllClients()) {
			System.out.println("shutt");
			cl.shutdown();
		}
	}
}

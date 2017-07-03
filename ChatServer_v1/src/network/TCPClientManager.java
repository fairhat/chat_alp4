package network;

import java.util.HashMap;

public class TCPClientManager {
	
	private static TCPClientManager instance = null;
	private HashMap<String, TCPClient> list;
	
	private TCPClientManager () {
		list = new HashMap<String, TCPClient>();
	}
	
	public static TCPClientManager get() {
		if (instance == null) {
			instance = new TCPClientManager();
		}
		
		return instance;
	}
	
	public boolean hasClient (String name) {
		return list.get(name) != null;
	}
	
	public TCPClient getClient (String name) {
		return list.get(name);
	}
	
	public TCPClient[] getAllClients() {
		return list.values().toArray(new TCPClient[0]);
	}
	
	public void addClient (TCPClient client) {
		if (!hasClient(client.getName())) {
			list.put(client.getName(), client);
		}
	}
	
	public void deleteClient (TCPClient client) {
		if (hasClient(client.getName())) {
			list.remove(client.getName());
		}
	}
}

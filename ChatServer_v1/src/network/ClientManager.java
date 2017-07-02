package network;

import java.util.HashMap;

public class ClientManager {
	
	private HashMap<String, Client> list;
	
	public ClientManager() {
		this.list = new HashMap<String, Client>();
	}
	
	public boolean hasClient (String name) {
		return list.get(name) != null;
	}
	
	public Client getClient (String name) {
		return list.get(name);
	}
	
	public void registerClient (Client client) {
		if (!hasClient(client.getName())) {
			list.put(client.getName(), client);			
		}
	}
	
	public void deleteClient (Client client) {
		if (hasClient(client.getName())) {
			list.remove(client.getName());
		}
	}
	
	
}

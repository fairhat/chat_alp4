package network;

import java.util.concurrent.Semaphore;

import fx.ServerGUI;

public class TCPController {
	
	private ServerGUI gui;
	private Semaphore queue = new Semaphore(0);
	private TCPClientManager manager = TCPClientManager.get();

	private static TCPController instance = null;
	
	private TCPController (ServerGUI gui) {
		this.gui = gui;
		//queue.release();
	}
	
	public static TCPController init (ServerGUI gui) {
		if (instance == null) {
			instance = new TCPController(gui);
		}
		
		return instance;
	}
	
	public static TCPController get () {
		if (instance == null) {
			throw new RuntimeException();
		}
		
		return instance;
	}
	
	public void signal (String message) {
		this.gui.pushConsoleMessage(message);
	}
	
	public void signalError (String message) {
		this.gui.pushConsoleMessage("Error:" + message);
	}
	
	public MessageProtocol parseMessage (String message) {
		return MessageProtocol.parse(message);
	}
	
	public void handleMessage(TCPClient client, String message) {
		if (message.length() == 0) { return; }
		
		MessageProtocol msg = parseMessage(message);
		
		if (msg.intent == MessageProtocol.COMMAND.LOGIN) {
			login(client, msg);
		}
		
		if (msg.intent == MessageProtocol.COMMAND.CHAT) {
			chat(msg);
		}
		
		if (msg.intent == MessageProtocol.COMMAND.WHISPER) {
			whisper(client, msg);
		}
	}
	
	public void whisper (TCPClient client, MessageProtocol msg) {
		TCPClient receiver = manager.getClientByName(msg.to);
		
		receiver.pushMessage(msg, true);
		client.pushMessage(msg, true);
	}
	
	public void chat (MessageProtocol msg) {
		TCPClient[] clients = manager.getAllClients();
		
		for (TCPClient client : clients) {
			client.pushMessage(msg);
		}
	}
	
	public void login(TCPClient client, MessageProtocol msg) {
		client.setUsername(msg.clientName);
		if (!manager.hasClient(client)) {
			gui.addClient(client.getID(), client.getUsername());
			manager.addClient(client);
		}
	}
	
	public void logout(TCPClient client) {
		if (manager.hasClient(client.getID())) {
			gui.removeClient(client.getID());
			manager.deleteClient(client.getID());
		}
	}
	
	public void update(TCPClient client, MessageProtocol msg) {
		gui.removeClient(client.getID());
		String oldUsername = client.getUsername();
		client.setUsername(msg.clientName);
		manager.updateClient(oldUsername, client);
		gui.addClient(client.getID(), client.getUsername());
	}
}

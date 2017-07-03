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
		queue.release();
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
		
		TCPClient[] clients = manager.getAllClients();
		
		for (TCPClient c : clients) {
			c.pushMessage(parseMessage(message));
		}
	}
}

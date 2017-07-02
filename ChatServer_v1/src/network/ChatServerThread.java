package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fx.ServerGUI;

public class ChatServerThread extends Thread {
	
	private ServerSocket socket;
	private int port;
	private ServerGUI gui;
	private boolean running;
	private ExecutorService serverPool;
	private ClientManager manager;
	
	public ChatServerThread (int port, ServerGUI gui) {
		this.port 	 	= port;
		this.gui 	 	= gui;
		this.running 	= false;
		this.serverPool = Executors.newCachedThreadPool();
		this.manager 	= new ClientManager();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run () {
				shutdown();
			}
		});
	}
	
	public void run () {
		this.gui.pushConsoleMessage("Server wird gestartet.");
		this.running = true;
		try {
			this.socket = new ServerSocket(this.port);
			this.gui.pushConsoleMessage("Server gestartet: localhost:" + this.port);
		} catch (IOException e) {
			this.gui.pushConsoleMessage("Server konnte nicht gestartet werden.");
			this.running = false;
			e.printStackTrace();
		}
		
		while (this.running) {
			Socket client;
			try {
				client = this.socket.accept();
				this.gui.pushConsoleMessage("Client verbunden: " + client.getRemoteSocketAddress());
				
				ServerConnection handler = new ServerConnection(client, manager, gui);
				this.serverPool.execute(handler);
			} catch (IOException e) {
				this.gui.pushConsoleMessage("Ein Client wurde abgelehnt.");
				e.printStackTrace();
			}
		}
	}
	
	public void shutdown () {
		System.out.println("Shutting down");
		this.running = false;
		this.serverPool.shutdownNow();
		try {
			this.socket.close();
			this.gui.pushConsoleMessage("Server wurde gestoppt.");
		} catch (IOException e) {
			this.gui.pushConsoleMessage("Server konnte nicht gestoppt werden.");
			e.printStackTrace();
		}
	}
}

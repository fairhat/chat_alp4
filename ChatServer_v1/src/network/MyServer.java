package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import fx.ServerGUI;

public class MyServer extends AbstractChatServer {
	
	ServerSocket socket;
	int port;
	HashMap<String, Socket> clients;

	public MyServer(ServerGUI gui) {
		super(gui);
	}

	@Override
	public void receiveConsoleCommand(String command, String msg) {
		// TODO Auto-generated method stub
		
	}
	
	void handleClient (Socket client) {
		
	}
	
	private void registerClient(Socket client) {
		
	}

	@Override
	public void start(String port) {
		this.port = Integer.parseInt(port);
		try {
			this.socket 		= new ServerSocket(this.port);
			this.gui.pushConsoleMessage("Server gestartet: localhost:" + port);
			
			new Thread("clienthandler") {
				public void run () {
					Socket client 	= socket.accept();
					registerClient(client);
					handleClient(client);
				}
			}.start();
		} catch (IOException e) {
			
			this.gui.pushConsoleMessage("Server konnte nicht gestartet werden.");
			e.printStackTrace();
		}
	}
		

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}

package network;

import fx.ServerGUI;

public class MyServer extends AbstractChatServer {
	
	TCPServer server;
	TCPController controller;
	boolean running = false;

	public MyServer(ServerGUI gui) {
		super(gui);
	}

	@Override
	public void receiveConsoleCommand(String command, String msg) {
		
	}

	@Override
	public void start (String port) {
		running = true;
		int prt = Integer.parseInt(port);
		
		this.controller = TCPController.init(this.gui);
		
		this.server = new TCPServer(prt);
		this.server.start();
	}
	
	@Override
	public void stop () {
		if (running) this.server.shutdown();
		running = false;
	}

	@Override
	public void terminate () {
		if(running) this.server.shutdown();
		running = false;
	}

}

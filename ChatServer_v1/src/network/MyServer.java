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
		
		if (command.startsWith("/m") || command.startsWith("/mute")) {
			int delim = msg.indexOf(" ");
			
			String userName = msg.substring(0, delim);
			
			int time = Integer.parseInt(msg.substring(delim + 1));
			
			controller.mute(userName, time);
		}
		
		if (command.startsWith("/e") || command.startsWith("/exclude")) {
			String userName = msg;
			
			controller.kick(userName);
		}
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

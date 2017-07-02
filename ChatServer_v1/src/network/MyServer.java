package network;

import java.io.BufferedWriter;
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
	ChatServerThread server;

	public MyServer(ServerGUI gui) {
		super(gui);
	}

	@Override
	public void receiveConsoleCommand(String command, String msg) {
		
	}

	@Override
	public void start(String port) {
		this.port = Integer.parseInt(port);
		this.server = new ChatServerThread(this.port, gui);
		this.server.start();
	}
		

	@Override
	public void stop() {
		this.server.shutdown();
	}

	@Override
	public void terminate() {
		this.server.shutdown();
	}

}

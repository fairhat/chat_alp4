package network;

import java.net.Socket;
import java.util.Scanner;
import java.io.*;

import fx.ClientGUI;

public class MyClient extends AbstractChatClient {
	
	Socket 	server 			= null;
	Scanner fromServer 		= null;
	Writer 	toServer 		= null;
	boolean running 		= false;

	public MyClient(ClientGUI gui) {
		super(gui);
	}

	@Override
	public void sendChatMessage(String msg) {
		System.out.println(msg);
	}
	
	private void waitForServer() {
		
	}

	@Override
	public void connect(String address, String port) {
		try {
			server 		= new Socket(address, Integer.parseInt(port));
			running 	= true;
			
			server.setKeepAlive(true);
			
			fromServer 	= new Scanner(server.getInputStream());
			toServer 	= new PrintWriter(server.getOutputStream(), true);
			
			new Thread("client") {
				public void run () {
					String servermessage = fromServer.next();
					
					gui.pushChatMessage("Server: " + servermessage);
				}
			}.start();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}

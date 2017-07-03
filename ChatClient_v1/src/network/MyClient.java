package network;

import java.net.Socket;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;
import java.io.*;

import fx.ClientGUI;

public class MyClient extends AbstractChatClient {
	
	Socket 	server 				= null;
	//Scanner fromServer 			= null;
	//PrintWriter toServer 		= null;
	boolean running 			= false;

	public MyClient(ClientGUI gui) {
		super(gui);
	}

	@Override
	public void sendChatMessage(String msg) {
		PrintWriter toServer;
		try {
			toServer = new PrintWriter(server.getOutputStream(), true);
			if (running) {
				String time = Instant.now().toString();
				String toOut = "#STARTOF\nname=" + uName + "\ntime=" + time + "\nmsg=" + msg + "\n#ENDOF";
				System.out.println("raus:\n" + toOut);
				toServer.println(toOut);
				//toServer.flush();		
				toServer.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String address, String port) {
		try {
			server 		= new Socket(address, Integer.parseInt(port));
			running 	= true;
			
			server.setKeepAlive(true);
			
			//fromServer 	= new Scanner(server.getInputStream());
			//toServer 	= new PrintWriter(server.getOutputStream(), true);
			
//			new Thread("client") {
//				public void run () {
//					while (true) {
//						//String servermessage = fromServer.next();
//						
//						//gui.pushChatMessage("Server: " + servermessage);
//					}
//						
//				}
//			}.start();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		running = false;
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}

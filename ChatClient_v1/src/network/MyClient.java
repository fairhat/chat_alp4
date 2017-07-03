package network;

import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.io.*;

import fx.ClientGUI;

public class MyClient extends AbstractChatClient {
	
	DateTimeFormatter formatter =
		    DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
		                     .withLocale( Locale.GERMANY )
		                     .withZone( ZoneId.systemDefault() );
	
	private class ReceiveThread extends Thread {
		
		@Override
		public void run() {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (reader != null) {
				while (!server.isClosed()) {
					String msg = "";
					String input;
					try {
						input = reader.readLine();
						
						if (input.startsWith("#STARTOF")) {
							while (!(input = reader.readLine()).startsWith("#ENDOF")) {
								msg += input + "\n";
							}						
						}
						
						MessageProtocol mp = MessageProtocol.parse(msg);
						String chatMsg = "[" + formatter.format(mp.timestamp) + "] " + mp.clientName + ": " + mp.message;
						gui.pushChatMessage(chatMsg);
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
			}
		}
	}
	
	Socket 	server 				= null;
	ReceiveThread fromServer 	= null;
	PrintWriter toServer 		= null;
	boolean running 			= false;

	public MyClient(ClientGUI gui) {
		super(gui);
	}

	@Override
	public void sendChatMessage(String msg) {
		try {
			if (running) {
				if (toServer == null) { toServer = new PrintWriter(server.getOutputStream(), true); }
				String time = Instant.now().toString();
				String toOut = "#STARTOF\nname=" + uName + "\ntime=" + time + "\nmsg=" + msg + "\n#ENDOF";

				toServer.println(toOut);
	
				toServer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String address, String port) {
		if (server == null && !running) {
			try {
				server 		= new Socket(address, Integer.parseInt(port));
				running 	= true;
				
				gui.pushChatMessage("Mit dem Server verbunden.");
				
				server.setKeepAlive(true);
				new ReceiveThread().start();
				
				try {
					if (toServer == null) { toServer = new PrintWriter(server.getOutputStream(), true); }
					if (running) {
						String time = Instant.now().toString();
						String toOut = "#STARTOF\nname=" + uName + "\ntime=" + time + "\nmsg=/login\n#ENDOF";

						toServer.println(toOut);
			
						toServer.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void disconnect() {
		if (server != null && !server.isClosed()) {			
			sendChatMessage("/logout");
			running = false;
			gui.pushChatMessage("Server Verbindung getrennt.");
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void terminate() {
		if (running) {
			disconnect();
		}
	}

}

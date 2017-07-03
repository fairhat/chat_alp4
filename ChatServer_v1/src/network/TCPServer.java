package network;

import java.util.concurrent.Executors;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer extends Thread {
	
	private ServerSocket socket;
	private int port;
	private boolean running;
	private ExecutorService pool;
	private TCPClientManager mngr;
	private TCPController cntrl;
	
	public TCPServer (int port) {
		this.port = port;
		this.running = false;
		this.pool = Executors.newCachedThreadPool();
		this.mngr = TCPClientManager.get();
		this.cntrl = TCPController.get();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run () {
				shutdown();
			}
		});
	}
	
	public void run () {
		
		this.running = true;
		
		try {
			this.socket = new ServerSocket(this.port);
			this.cntrl.signal("Server gestartet: localhost:" + this.port);
		} catch (IOException e) {
			this.cntrl.signalError("Server konnte nicht gestartet werden.");
			this.running = false;
			e.printStackTrace();
		}
		
		while (this.running) {
			Socket client;
			
			try {
				client = this.socket.accept();
				this.cntrl.signal("Client verbunden: " + client.getRemoteSocketAddress());
				
				TCPClient handler = new TCPClient(client);
				this.pool.execute(handler);
			} catch (IOException e) {
				this.cntrl.signal("Ein Client wurde abgemeldet.");
				e.printStackTrace();
			}
		}
	}
	
	public void shutdown () {
		this.cntrl.signal("Server wird beendet.");
		this.running = false;
		
		this.pool.shutdownNow();
		
		try {
			this.socket.close();
			this.cntrl.signal("Server beendet.");
		} catch (IOException e) {
			this.cntrl.signalError("Server konnte nicht beendet werden.");
			e.printStackTrace();
		}
	}
}

package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import protobuf.TimeServerProtos.TimeServerMessage;
import protobuf.TSMBuilder;

public class ServerUDP {
	
	boolean running = true;

	public static void main(String[] args) throws IOException {
		ServerTCP server = new ServerTCP();
		server.start(1338);
	}

	// This data should be transmitted to each client.
	private int requestID = 0;
	private String getTime() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	}
	private String zone = TimeZone.getDefault().getDisplayName();
	private String name = "Server#1 (Germany, Berlin)";

	public void start(int serverPort) throws IOException {
		
		DatagramSocket socket = new DatagramSocket(serverPort);

		while (running) {
			
			// Listen for incoming requests
			DatagramPacket datagramIn = new DatagramPacket(new byte[0], 0);
			socket.receive(datagramIn);
			
			// Create the message
			TimeServerMessage tsm = TSMBuilder.buildTSM(++requestID, getTime(), name, zone);

			// Create the Datagram
			DatagramPacket datagramOut = new DatagramPacket(tsm.toByteArray(), tsm.toByteArray().length, datagramIn.getSocketAddress());	
			
			// Send Datagram
			socket.send(datagramOut);

		}

		socket.close();
	}

}

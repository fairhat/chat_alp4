package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import protobuf.TimeServerProtos.TimeServerMessage;

public class ClientTCP {


	public static void main(String[] args) throws IOException {
		ClientTCP client = new ClientTCP();
		client.connect("localhost", 1338);
	}

	private int requestID;
	private String time;
	private String zone;
	private String name;

	public void connect(String serverAddress, int serverPort) throws IOException {

		// Establish connection to Server
		Socket socket = new Socket(serverAddress, serverPort);
		InputStream in = socket.getInputStream();
		// ------------------------------

		TimeServerMessage tsm = TimeServerMessage.parseDelimitedFrom(in);

		requestID = tsm.getRequests();
		name = tsm.getName();
		time = tsm.getTime();

		if (tsm.hasZone())
			zone = tsm.getZone();

		// Print received data
		System.out.println(requestID);
		System.out.println(name);
		System.out.println(time);
		System.out.println(zone);

		// Close stream and socket
		in.close();
		socket.close();
	}

}

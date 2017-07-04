package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

import protobuf.TimeServerProtos.TimeServerMessage;

public class ClientUDP {

	public static void main(String[] args) throws IOException {
		ClientTCP client = new ClientTCP();
		client.connect("localhost", 1338);
	}

	private int requestID;
	private String time;
	private String zone;
	private String name;

	public void connect(String serverIp, int serverPort) throws IOException {

		
		InetSocketAddress address = new InetSocketAddress(serverIp, serverPort);
		
		// Create new socket. The port will be set automatically 
		DatagramSocket socket = new DatagramSocket();

		// Call server by sending an empty package
		DatagramPacket datagramOut = new DatagramPacket(new byte[0], 0, address);
		socket.send(datagramOut);

		// Receive data from server
		DatagramPacket datagramIn = new DatagramPacket(new byte[1024], 1024);

		socket.receive(datagramIn);

		// make sure to get only the yummy part of the data
		int offset = datagramIn.getOffset();
		int length = datagramIn.getLength();
		byte[] payload = Arrays.copyOfRange(datagramIn.getData(), offset, (length + offset));

		// parse the TimeServerMessage out of the bytes (this would fail with not-yummy parts leftover)
		TimeServerMessage tsm = TimeServerMessage.parseFrom(payload);

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

		// Close socket
		socket.close();
	}

}

package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import protobuf.TimeServerProtos.TimeServerMessage;
import protobuf.TSMBuilder;

public class ServerTCP {
	
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
		ServerSocket serverSocket = new ServerSocket(serverPort);
		Socket clientSocket;

		while (running) {
			try {
				// Listen for incoming requests
				clientSocket = serverSocket.accept();
				OutputStream out = clientSocket.getOutputStream();
				// ----------------------------

				TimeServerMessage tsm = TSMBuilder.buildTSM(++requestID, getTime(), name, zone);
				tsm.writeDelimitedTo(out);

				// Close stream and socket
				out.close();
				clientSocket.close();
				// ----------------------------
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		serverSocket.close();
	}

}

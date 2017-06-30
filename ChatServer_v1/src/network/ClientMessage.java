package network;

import java.util.Date;

public class ClientMessage {
	
	String clientName;
	Date timestamp;
	String message;
	
	public ClientMessage (String msg) {
		String[] lines = msg.split("\n");
		
		for (String line : lines) {
			String[] line_ = line.split("=", 2);
			if (line_[0] == "name") {
				
			}
		}
	}
}

package network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ClientMessage {
	
	enum COMMAND {
		WHISPER, CHAT, LOGIN
	};
	
	String clientName;
	Instant timestamp;
	String message;
	COMMAND intent;
	
	static COMMAND getCommand (String message) {
		
		if (message.startsWith("/")) {
			
			if (message.startsWith("/p")) {
				return COMMAND.WHISPER;
			}
			
			if (message.startsWith("/l")) {
				return COMMAND.LOGIN;
			}
		}
		
		return COMMAND.CHAT;
	}
	
	public ClientMessage (String msg) {
		String[] lines = msg.split("\n");
		
		for (String line : lines) {
			String[] li = line.split("=", 2);
			//System.out.println("li => " + li[0] + " <= " + li[1]);
			if (li[0].equals("name")) {
				clientName = li[1];
			}
			
			if (li[0].equals("time")) {
				timestamp = Instant.parse(li[1]);
			}
			
			if (li[0].equals("msg")) {
				message = li[1];
			}
		}
	}
	
	public static String convertMessageFromClient (String clientName, Instant time, String message) {
		String timeStr = time.toString();
		
		return 		"#STARTOF"
				+ 	"\nname=" + clientName
				+	" \ntime=" + timeStr
				+	"\nmsg="	+ message
				+	"\n#ENDOF"
		;
	}
	
	public String toString () {
		return "{\n"
				+ "\rname: " + clientName
				+ "\n\rtime: " + timestamp
				+ "\n\rmsg:  " + message + "\n}";
	}
}

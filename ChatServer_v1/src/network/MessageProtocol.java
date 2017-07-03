package network;

import java.time.Instant;

public class MessageProtocol {
	enum COMMAND {
		WHISPER, CHAT, LOGIN
	}
	
	String clientName;
	Instant timestamp;
	String message;
	COMMAND intent;
	
	static COMMAND getCommand (String msg) {
		
		if (msg.startsWith("/")) {
			
			if (msg.startsWith("/p") || msg.startsWith("/private")) {
				return COMMAND.WHISPER;
			}
			
			if (msg.startsWith("/l") || msg.startsWith("/login")) {
				return COMMAND.LOGIN;
			}
		}
		
		return COMMAND.CHAT;
	}
	
	public MessageProtocol (String msg) {
		String[] lines = msg.split("\n");
		
		for (String line : lines) {
			String []li = line.split("=", 2);
			
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
	
	public static MessageProtocol parse (String msg) {
		return new MessageProtocol(msg);
	}
	
	public static String convertToString (MessageProtocol msg) {
		String time = msg.timestamp.toString();
		
		return		"#STARTOF\n"
				+	"name=" + msg.clientName + "\n"
				+	"time=" + time + "\n"
				+	"msg=" 	+ msg.message + "\n"
				+	"#ENDOF";
	}
	
	public String toString () {
		return MessageProtocol.convertToString(this);
	}
}

package network;

import java.time.Instant;

public class MessageProtocol {
	enum COMMAND {
		WHISPER, CHAT, LOGIN, SWITCHNAME
	}
	
	String clientName;
	Instant timestamp;
	String message;
	COMMAND intent;
	String to;
	String sanitizedMessage = "";
	
	static COMMAND getCommand (String msg) {
		
		if (msg.startsWith("/")) {
			
			if (msg.startsWith("/p") || msg.startsWith("/private")) {
				return COMMAND.WHISPER;
			}
			
			if (msg.startsWith("/l") || msg.startsWith("/login")) {
				return COMMAND.LOGIN;
			}
			
			if (msg.startsWith("/r") || msg.startsWith("/rename")) {
				return COMMAND.SWITCHNAME;
			}
		}
		
		return COMMAND.CHAT;
	}
	
	public MessageProtocol (String name, String time, String message) {
		this.clientName = name;
		this.timestamp = Instant.parse(time);
		this.message = message;
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
		
		intent = getCommand(message);
		
		if (intent == COMMAND.WHISPER) {
			String cpy = message;
			
			int endOfCommand = message.indexOf(" ");
			
			String withoutCmd = cpy.substring(endOfCommand + 1);
			int nextDelimiter = withoutCmd.indexOf(" ");
			String userName = withoutCmd.substring(0, nextDelimiter);
			
			String withoutUsername = withoutCmd.substring(nextDelimiter);
			
			sanitizedMessage = "*PRIVATNACHRICHT* " + withoutUsername;
			to = userName;
		}
	}
	
	public static MessageProtocol parse (String msg) {
		return new MessageProtocol(msg);
	}
	
	public static String convertToString (MessageProtocol msg, boolean sanitized) {
		
		String message = msg.message;
		String time = msg.timestamp.toString();
		
		if (sanitized) {
			message = msg.sanitizedMessage;
		}
		
		return		"#STARTOF\n"
				+	"name=" + msg.clientName + "\n"
				+	"time=" + time + "\n"
				+	"msg=" 	+ message + "\n"
				+	"#ENDOF";
	}
	
	public String toSanitizedString () {
		return MessageProtocol.convertToString(this, true);
	}
	
	public String toString () {
		return MessageProtocol.convertToString(this, false);
	}
}

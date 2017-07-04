package network;

import java.time.Instant;

import fx.ServerGUI;
import javafx.scene.paint.Color;

public class TCPController {
	
	private ServerGUI gui;
	private TCPClientManager manager = TCPClientManager.get();

	private static TCPController instance = null;
	
	private TCPController (ServerGUI gui) {
		this.gui = gui;
	}
	
	/**
	 * Den Controller initialisieren
	 * @param gui
	 * @return
	 */
	public static TCPController init (ServerGUI gui) {
		instance = new TCPController(gui);
		
		return instance;
	}
	
	/**
	 * Controller Instanz abrufen (Singleton)
	 * @return
	 */
	public static TCPController get () {
		if (instance == null) {
			throw new RuntimeException();
		}
		
		return instance;
	}
	
	/**
	 * Server starten (färbt das icon grün)
	 */
	public void startServer () {
		this.gui.setSymbolColor(Color.GREEN);
	}
	
	/**
	 * Server stoppen (färbt das icon rot)
	 */
	public void stopServer () {
		this.gui.setSymbolColor(Color.RED);
	}
	
	/**
	 * Der GUI Konsole eine Nachricht übergeben
	 * @param message
	 */
	public void signal (String message) {
		this.gui.pushConsoleMessage(message);
	}
	
	/**
	 * Der GUI Konsole eine Fehlermeldung übergeben
	 * @param message
	 */
	public void signalError (String message) {
		this.gui.pushConsoleMessage("Error:" + message);
	}
	
	/**
	 * Liest eine Nachricht in dem MessageProtocol Format
	 * @param message
	 * @return
	 */
	public MessageProtocol parseMessage (String message) {
		return MessageProtocol.parse(message);
	}
	
	/**
	 * Eine Clientnachricht verarbeiten
	 * @param client
	 * @param message
	 */
	public void handleMessage(TCPClient client, String message) {
		if (message.length() == 0) { return; }
		
		MessageProtocol msg = parseMessage(message);
		
		if (msg.intent == MessageProtocol.COMMAND.SWITCHNAME) {
			update(client, msg);
		}
		
		if (msg.intent == MessageProtocol.COMMAND.LOGIN) {
			login(client, msg);
		}
		
		if (msg.intent == MessageProtocol.COMMAND.CHAT) {
			chat(client, msg);
		}
		
		if (msg.intent == MessageProtocol.COMMAND.WHISPER) {
			whisper(client, msg);
		}
	}
	
	/**
	 * FLÜSTERN (2.c)
	 * @param client
	 * @param msg
	 */
	public void whisper (TCPClient client, MessageProtocol msg) {
		TCPClient receiver = manager.getClientByName(msg.to);
		
		receiver.pushMessage(msg, true);
		client.pushMessage(msg, true);
	}
	
	/**
	 * Eine Clientnachricht an alle Clients verschicken
	 * @param cl
	 * @param msg
	 */
	public void chat (TCPClient cl, MessageProtocol msg) {
		if (!cl.hasPermission(TCPClient.PERMISSION.CHAT)) {
			noPermissions(cl);
			
			return;
		}
		
		broadcast(msg);
	}
	
	/**
	 * Eine Nachricht an alle Clients verschicken
	 * @param msg
	 */
	public void broadcast(MessageProtocol msg) {
		TCPClient[] clients = manager.getAllClients();
		
		for (TCPClient client : clients) {
			client.pushMessage(msg);
		}
	}
	
	/**
	 * Eine Nachricht an einen einzelnen Client schicken
	 * @param client
	 * @param msg
	 */
	public void sendTo (TCPClient client, MessageProtocol msg) {
		client.pushMessage(msg);
	}
	
	
	/**
	 * Dem Nutzer anzeigen, dass ihm die Berechtigungen fehlen.
	 * @param client
	 */
	public void noPermissions (TCPClient client) {
		sendTo(client, new MessageProtocol("server", Instant.now().toString(), "Sie haben keine Berechtigung dazu."));
	}
	
	/**
	 * MUTE (2.c)
	 * Einen Benutzer für X Sekunden muten.
	 * @param userName
	 * @param timeSeconds
	 */
	public void mute (String userName, int timeSeconds) {
		TCPClient client = manager.getClientByName(userName);
		
		if (client != null) {
			client.demote(TCPClient.PERMISSION.CHAT);
			new java.util.Timer().schedule( 
			        new java.util.TimerTask() {
			            @Override
			            public void run() {
			                client.promote(TCPClient.PERMISSION.CHAT);
			            }
			        }, 
			        timeSeconds * 1000 
			);
		} else {
			gui.pushConsoleMessage("Nutzer " + userName + " nicht gefunden. Sicher, dass der Name richtig ist?");
		}
	}
	
	/**
	 * EXCLUDE (2.c)
	 * Einen Nutzer aus dem Chatroom kicken
	 * @param userName
	 */
	public void kick (String userName) {
		TCPClient client = manager.getClientByName(userName);
		
		if (client != null) {
			client.shutdown();
		} else {
			gui.pushConsoleMessage("Nutzer " + userName + " nicht gefunden. Sicher, dass der Name richtig ist?");
		}
	}
	
	/**
	 * Sich als Client anmelden
	 * @param client
	 * @param msg
	 */
	public void login(TCPClient client, MessageProtocol msg) {
		client.setUsername(msg.clientName);
		if (!manager.hasClient(client)) {
			gui.addClient(client.getID(), client.getUsername());
			manager.addClient(client);
		}
	}
	
	/**
	 * Sich als Client abmelden
	 * @param client
	 */
	public void logout (TCPClient client) {
		if (manager.hasClient(client.getID())) {
			gui.removeClient(client.getID());
			manager.deleteClient(client.getID());
		}
	}
	
	/**
	 * NAME ÄNDERN (2.c)
	 * 
	 * Den Namen eines Users ändern
	 * @param client
	 * @param msg
	 */
	public void update (TCPClient client, MessageProtocol msg) {
		String oldName = client.getUsername();
		gui.removeClient(client.getID());
		gui.addClient(client.getID(), msg.clientName);
		manager.updateClient(msg.clientName, client);
		
		broadcast(new MessageProtocol("server", Instant.now().toString(), oldName + " heißt nun " + msg.clientName));
	}
	
	
	/**
	 * Wird aufgerufen, wenn der Client die Verbindung trennt.
	 * @param client
	 */
	public void clientDisconnected (TCPClient client) {
		gui.removeClient(client.getID());
		manager.deleteClient(client.getID());
	}
}

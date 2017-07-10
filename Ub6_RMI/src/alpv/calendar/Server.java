package alpv.calendar;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server extends UnicastRemoteObject implements CalendarServer {
	private static final long serialVersionUID = -3700841376600131132L;
	
	private final CalendarService service = CalendarService.getInstance();
	private final Registry registry;
	private int port;
	
	private final Event dummyEvent = new Event("Dummy", new String[] {"dummy"}, Date.from(Instant.MIN));
	
	private final String serverName = "CALENDAR_SERVER";
	
	
	protected Server(int port) throws RemoteException {
		super();
		this.port = port;
		
		registry = LocateRegistry.createRegistry(port);
		registry.rebind(serverName, this);
	}
	
	@Override
	public synchronized long addEvent(Event e) throws RemoteException {
		UUID guid = UUID.randomUUID();
		long id = guid.timestamp();
		
		e.setId(id);
		
		service.addEvent(e);
		
		return id;
	}

	@Override
	public boolean removeEvent(long id) throws RemoteException {
		return service.removeEvent(id);
	}

	@Override
	public boolean updateEvent(long id, Event e) throws RemoteException {
		return service.updateEvent(id, e);
	}

	@Override
	public List<Event> listEvents(String user) throws RemoteException {
		return service.getUserEvents(user);
	}

	@Override
	public Event getNextEvent(String user) throws RemoteException {		
		Event ev = service.getNextUserEvent(user);
		
		service.schedule(user, ev);
		
		return ev;
	}

	@Override
	public void RegisterCallback(EventCallback ec, String user) throws RemoteException {
		service.addUserCallback(user, ec);
	}

	@Override
	public void UnregisterCallback(EventCallback ec) throws RemoteException {
		service.removeCallback(ec);
	}

}

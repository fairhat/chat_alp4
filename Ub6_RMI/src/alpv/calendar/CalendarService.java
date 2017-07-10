package alpv.calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class CalendarService {
	
	private Semaphore mutex = new Semaphore(1);
	private static CalendarService instance = null;
	private HashMap<Long, Event> eventIds = new HashMap<Long, Event>();
	private HashMap<String, ArrayList<Event>> eventUsers = new HashMap<String, ArrayList<Event>>();
	private ArrayList<Notification> notifiers = new ArrayList<Notification>();
	
	private CalendarService () {}

	public static synchronized CalendarService getInstance() {
		if (instance == null) {
			instance = new CalendarService();
		}
		
		return instance;
	}
	
	public boolean addEvent (Event e) {
		boolean added = true;
		try {
			mutex.acquire();
			eventIds.put(e.getId(), e);
			
			String[] users = e.getUser();
			
			for (String user : users) {
				ArrayList<Event> events = eventUsers.get(user);
				
				if (events == null) {
					events = new ArrayList<Event>();
				}
				
				events.add(e);
				
				eventUsers.put(user, events);
			}
		} catch (InterruptedException e1) {
			added = false;
			e1.printStackTrace();
		} finally {
			mutex.release();
		}
		
		return added;
	}
	
	public boolean removeEvent (long id) {
		boolean removed = true;
		
		try {
			mutex.acquire();
			eventIds.remove(id);
		} catch (InterruptedException e) {
			removed = false;
			e.printStackTrace();
		} finally {
			mutex.release();
		}
		
		return removed;
	}
	
	public Event getEvent (long id) {
		return eventIds.get(id);
	}
	
	public Event getNextUserEvent (String user) {
		ArrayList<Event> evs = this.getUserEvents(user);
		
		return evs.get(0);
	}
	
	public ArrayList<Event> getUserEvents (String user) {
		ArrayList<Event> evs = eventUsers.get(user);
		
		if (evs == null) {
			return new ArrayList<Event>();
		}
		
		return evs;
	}
	
	public void schedule (String user, Event ev) {
		Notification n = new Notification();
	}
	
	public boolean updateEvent (long id, Event e) {
		Event existing = eventIds.get(id);
		
		if (existing == null) return false;
		
		existing.setName(e.getName());
		existing.setUser(e.getUser());
		existing.setBegin(e.getBegin());
		
		return true;
	}
	
	public void addUserCallback (EventCallback cb, String user) {
		
	}
	
	public void removeCallback (EventCallback cb) {
		
	}
}

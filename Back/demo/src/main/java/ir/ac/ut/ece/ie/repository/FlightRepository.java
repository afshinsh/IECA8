package ir.ac.ut.ece.ie.repository;

import java.util.HashMap;

public class FlightRepository {
	private static FlightRepository instance;
	
	HashMap<String, Integer> flights;
	private FlightRepository() {
		flights = new HashMap<>();
		flights.put("Beijing", 165);
		flights.put("Toronto", 70);
		flights.put("Barcelona", 66);
		flights.put("Paris", 27);
	}
	
	public static FlightRepository getInstance() {
		if (instance == null)
			instance = new FlightRepository();
		return instance;
	}
	
	public int getAvailableSeats(String name) {
		return flights.get(name);
	}
	
	public void updateAvailableSeats(String name, int value) {
		flights.put(name, flights.get(name) - value);
	}
	
}

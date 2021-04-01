package comp303.assignment5;

import java.util.ArrayList;

public abstract class AbstractWatchable implements Watchable{
	//Abstract some common fields here
	protected String aTitle;
	
	//To implement the observer pattern, record the Watchlists which added this Movie
	private ArrayList<WatchList> observers = new ArrayList<WatchList>(); 
	

	public String getTitle() {
		return aTitle;
	}
	
	@Override
	public void watch() {
		//When we watch the movie, we need to notify all of observers
		for(WatchList o : this.observers) {
			o.notifyToUpdate(this);
		}
	}
	
	protected void acceptList(WatchList w) {
		// TODO Auto-generated method stub
		assert w != null;
		this.observers.add(w);
		
	}

	protected void withdrawtList(WatchList w) {
		// TODO Auto-generated method stub
		assert w != null;
		this.observers.remove(w);
	}
}

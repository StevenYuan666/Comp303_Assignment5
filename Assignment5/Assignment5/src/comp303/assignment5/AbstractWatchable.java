package comp303.assignment5;

import java.util.ArrayList;

public abstract class AbstractWatchable implements Watchable{
	//To implement the observer pattern, record the Watchlists which added this Movie
		private ArrayList<WatchList> observers = new ArrayList<WatchList>(); 

		@Override
		public void watch() {
			//When we watch the movie, we need to notify all of observers
			for(WatchList o : this.observers) {
				o.notifyToUpdate(this);
			}
			
			// Just a stub.
			// We don't expect you to implement a full media player!
			System.out.println("Now playing " + aTitle);
		}
		
	@Override
	public void acceptList(WatchList w) {
		// TODO Auto-generated method stub
		assert w != null;
		this.observers.add(w);
		
	}

	@Override
	public void withdrawtList(WatchList w) {
		// TODO Auto-generated method stub
		assert w != null;
		this.observers.remove(w);
	}
}

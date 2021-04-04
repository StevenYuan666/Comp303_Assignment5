package comp303.assignment5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWatchable implements Watchable{
	/*
	 * Title and aTags can be protected here, since the subclass should have access to these fields
	 * And in real world, the Driver will be outside the package, so it's ok
	 */
	
	//Abstract some common fields here
	protected String aTitle;
	/*
	 * Since we write the Episode as inner class of TVShow, so we don't have Language and Studio
	 * However, each Episode should have Language and Studio, so I abstract them out
	 */
	private Language aLanguage;
	private String aStudio;
	//Tags in Movie and Episode is same as the Info in TVShow
	protected Map<String, String> aTags = new HashMap<>();

	//To implement the observer pattern, record the Watchlists which added this Movie
	private ArrayList<Observer> observers = new ArrayList<Observer>(); 
	
	//The abstract constructor so that all subclass can instantiate the commom field
	public AbstractWatchable(String pTitle, Language pLanguage, String pStudio) {
		assert pTitle != null && pLanguage != null && pStudio != null;
		this.aTitle = pTitle;
		this.aLanguage = pLanguage;
		this.aStudio = pStudio;
	}
	
	//Common methods about the info
	public String setInfo(String pKey, String pValue) {
		assert pKey != null && !pKey.isBlank();
		if (pValue == null) {
			return aTags.remove(pKey);
		}
		else {
			return aTags.put(pKey, pValue);
		}
	}
	
	public boolean hasInfo(String pKey) {
		assert pKey != null && !pKey.isBlank();
		return aTags.containsKey(pKey);
	}
	
	public String getInfo(String pKey) {
		assert hasInfo(pKey);
		return aTags.get(pKey);
	}
	
	//Common methods about Language and Studio
	public Language getLanguage() {
		return aLanguage;
	}
	
	public String getStudio() {
		return aStudio;
	}
	
	//Common method about Title
	public String getTitle() {
		return aTitle;
	}
	
	/*
	 * Watch and notify all observers to update the most recent watched
	 */
	@Override
	public void watch() {
		//When we watch the movie, we need to notify all of observers
		for(Observer o : this.observers) {
			o.notifyToUpdate(this);
		}
	}
	
	//Common methods for adding or removing observers
	protected void acceptList(Observer w) {
		// TODO Auto-generated method stub
		assert w != null;
		this.observers.add(w);
	}

	protected void withdrawtList(Observer w) {
		// TODO Auto-generated method stub
		assert w != null;
		this.observers.remove(w);
	}
}

package comp303.assignment5;

/**
 * Represents a video object that can be watched
 */
interface Watchable {
	
	/**
	 * When add a watchable object to the watchlist, add such list to the private field
	 */
	public void acceptList(WatchList w);
	
	/**
	 * When remove a watchable object of the watchlist, remove such list in the private field
	 */
	public void withdrawtList(WatchList w);
	
	/**
	 * Plays the video to the user
	 * @pre isValid()
	 */
	public void watch();

	/**
	 * Indicates whether the Watchable element is ready to be played.
	 * 
	 * @return true if the file path points to an existing location that can be read and is not a directory
	 */
	public boolean isValid();
	
}
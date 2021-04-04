package comp303.assignment5;

/*
 * Don't change the interface Watchable. Instead of, create an abstract class AbtractWatchable
 * For future enhancement, we may change the Watchable interface as Playable interface, and have
 * several abstract class like AbstractWatchable, AbstractListenable etc. 
 */


/**
 * Represents a video object that can be watched
 */
interface Watchable {
	
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
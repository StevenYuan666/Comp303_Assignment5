package comp303.assignment5;

/*
 * Abstract an interface for the Observer, so that we can maintain our work easily in the future
 * For example, we may add ListenList and create several Listenable objects
 */
public interface Observer {
	public AbstractWatchable lastWatched();
	public void notifyToUpdate(AbstractWatchable w);
}

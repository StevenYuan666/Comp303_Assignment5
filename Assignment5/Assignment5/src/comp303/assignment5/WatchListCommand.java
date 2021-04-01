package comp303.assignment5;

import java.util.Optional;

public interface WatchListCommand {
	
	Optional<AbstractWatchable> execute();
	
	Optional<AbstractWatchable> undo();
	
	Optional<AbstractWatchable> redo();
}

package comp303.assignment5;

import java.util.Optional;

//To implement the command pattern, create an interface for the Command
public interface WatchListCommand {
	
	//May have return value for executing the command
	Optional<AbstractWatchable> execute();
	
	void undo();
	
	//May have return value for redoing the command
	Optional<AbstractWatchable> redo();
}

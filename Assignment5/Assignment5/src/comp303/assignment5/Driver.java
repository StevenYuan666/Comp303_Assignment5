package comp303.assignment5;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Set up for the tests
		File f1 = new File("comp303/Avengers1.MP4");
		Movie m1 = new Movie(f1, "A1", Language.ENGLISH, "Marvel");
		File f2 = new File("comp303/Avengers2.MP4");
		Movie m2 = new Movie(f2, "A2", Language.ENGLISH, "Marvel");
		File f3 = new File("comp303/Avengers3.MP4");
		Movie m3 = new Movie(f3, "A3", Language.ENGLISH, "Marvel");
		Map<String, String> mm = new HashMap<String, String>();
		TVShow t1 = new TVShow("Singer", Language.ENGLISH, "Hunan", mm);
		File f4 = new File("Singers1");
		File f5 = new File("Singers2");
		File f6 = new File("Singers3");
		t1.createAndAddEpisode(f4, "Ep1");
		t1.createAndAddEpisode(f5, "Ep2");
		t1.createAndAddEpisode(f6, "Ep3");
		
		//Tests for Question1
		System.out.println("Start The Tests For Question1");
		WatchList w = new WatchList("W");
		w.addWatchableCommand(m1);
		w.addWatchableCommand(m2);
		w.addWatchableCommand(m3);
		w.addWatchableCommand(t1);
		m1.watch();
		m2.watch();
		System.out.println(w.lastWatched().getTitle());
		//Cannot watch each episode here since the files are not valid
		t1.watch();
		System.out.println(w.lastWatched().getTitle());
		t1.getEpisode(2).watch();
		System.out.println(w.lastWatched().getTitle());
		
		//Tests for Question3
		System.out.println();
		System.out.println("Start The Tests For Question3");
		WatchList marvel = new WatchList("Marvel");
		System.out.println("Test addWatchable: ");
		marvel.getList();
		marvel.addWatchableCommand(m1);
		marvel.getList();
		marvel.addWatchableCommand(m2);
		marvel.getList();
		marvel.addWatchableCommand(m3);
		marvel.getList();
		System.out.println("Test setName: ");
		System.out.println("Initial Name:");
		System.out.println(marvel.getName());
		System.out.println("Start setting new name: ");
		marvel.setName("M1");
		System.out.println(marvel.getName());
		marvel.setName("M2");
		System.out.println(marvel.getName());
		System.out.println("Start redo: ");
		marvel.redo();
		System.out.println(marvel.getName());
		marvel.redo();
		System.out.println(marvel.getName());
		System.out.println("Start undo: ");
		marvel.undo();
		marvel.getList();
		System.out.println(marvel.getName());
		marvel.undo();
		marvel.getList();
		System.out.println(marvel.getName());
		marvel.undo();
		marvel.getList();
		System.out.println(marvel.getName());
		marvel.undo();
		marvel.getList();
		System.out.println(marvel.getName());
		//One more time when stack is empty
		marvel.undo();
		marvel.getList();
		System.out.println(marvel.getName());
		marvel.undo();
		marvel.getList();
		System.out.println(marvel.getName());
		System.out.println("Test removeWatchable: ");
		marvel.addWatchableCommand(m1);
		marvel.addWatchableCommand(m2);
		marvel.addWatchableCommand(m3);
		System.out.println("Initial List: ");
		marvel.getList();
		System.out.println("Start remove: ");
		marvel.removeWatchableCommand(0);
		marvel.getList();
		System.out.println("Start redo: ");
		marvel.redo();
		marvel.getList();
		System.out.println("Start undo: ");
		marvel.undo();
		marvel.getList();
		System.out.println("Start redo again: ");
		marvel.redo();
		marvel.getList();
		//The list will not changed here, since all undo actions have been redone already
		marvel.redo();
		marvel.getList();
		System.out.println("Test next: ");
		WatchList marvel2 = new WatchList("Marvel2");
		marvel2.addWatchableCommand(m1);
		marvel2.addWatchableCommand(m2);
		marvel2.addWatchableCommand(m3);
		System.out.println("Initial List ");
		marvel2.getList();
		System.out.println("Start call next: ");
		System.out.println(marvel2.next().getTitle());
		System.out.println(marvel2.next().getTitle());
		System.out.println("Start call redo: ");
		System.out.println(marvel2.redo().get().getTitle());
		System.out.println(marvel2.redo().get().getTitle());
		System.out.println("Start call undo: 3 times");
		marvel2.undo();
		marvel2.undo();
		marvel2.undo();
		System.out.println("Then call next again: "); //Should be A2 here
		System.out.println(marvel2.next().getTitle());
		System.out.println("Test reset: ");
		marvel2.reset();
		//If reset not work correctly, A3 will be here instead
		System.out.println(marvel2.next().getTitle()); // Should be A1 here
		System.out.println("Start undo: ");
		marvel2.undo();
		marvel2.undo();
		//Should be A3
		System.out.println(marvel2.next().getTitle());
		System.out.println("Start redo: ");
		marvel2.next(); // A1
		marvel2.next(); // A2
		marvel2.reset();
		marvel2.redo();
		marvel2.redo();
		//Should be A1 here
		System.out.println(marvel2.next().getTitle());
		marvel2.undo(); // cancel the next command
		marvel2.undo(); // cancel the reset command, since 
		//Should be A3 here
		System.out.println(marvel2.next().getTitle());
	}

}

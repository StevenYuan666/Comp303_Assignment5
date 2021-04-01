package comp303.assignment5;

import java.io.File;

public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f1 = new File("Avengers1");
		Movie m1 = new Movie(f1, "A1", Language.ENGLISH, "Marvel");
		File f2 = new File("Avengers2");
		Movie m2 = new Movie(f2, "A2", Language.ENGLISH, "Marvel");
		File f3 = new File("Avengers3");
		Movie m3 = new Movie(f3, "A3", Language.ENGLISH, "Marvel");
		WatchList marvel = new WatchList("Marvel");
		marvel.addWatchableCommand(m1);
		marvel.addWatchableCommand(m2);
		marvel.addWatchableCommand(m3);
		
	}

}

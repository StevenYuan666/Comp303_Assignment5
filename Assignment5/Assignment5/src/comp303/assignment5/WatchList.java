package comp303.assignment5;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * Represents a sequence of watchables to watch in FIFO order.
 */
public class WatchList implements Bingeable<AbstractWatchable>, Observer {
	
	private final List<AbstractWatchable> aList = new LinkedList<>();
	private String aName;
	private int aNext;
	//A field stored the AbstractWatchable object that most recently watched
	private AbstractWatchable mostRecent = null;
	//Help to check if the command is called just after the undo command
	private boolean afterUndo = false;
	//Help to achieve the function of redo and undo
	private Stack<WatchListCommand> doneByOrder = new Stack<WatchListCommand>();
	private Stack<WatchListCommand> doneByUndo = new Stack<WatchListCommand>();
	
	/**
	 * Creates a new empty watchlist.
	 * 
	 * @param pName
	 *            the name of the list
	 * @pre pName!=null;
	 */
	public WatchList(String pName) {
		assert pName != null;
		aName = pName;
		aNext = 0;
	}
	
	public String getName() {
		return aName;
	}
	
	/**
	 * Changes the name of this watchlist.
	 * 
	 * @param pName
	 *            the new name
	 * @pre pName!=null;
	 */
	public void setName(String pName) {
		WatchListCommand setName = new WatchListCommand() {
			String previousName = aName;
			String currentName = pName;

			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				assert currentName != null;
				aName = currentName;
				//push this command to Command Stack and clear the undo Stack
				doneByOrder.push(this);
				doneByUndo.clear();
				afterUndo = false;
				return Optional.empty();
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				aName = previousName;
			}

			@Override
			public Optional<AbstractWatchable> redo() {
				// TODO Auto-generated method stub
				assert currentName != null;
				aName = currentName;
				//No need to push the command to Command stack here since the state will not change
				return Optional.empty();
			}
		};
		setName.execute();
	}
	
	/**
	 * Adds a watchable at the end of this watchlist.
	 * 
	 * @param pWatchable
	 *            the watchable to add
	 * @pre pWatchable!=null;
	 */
	private void addWatchable(AbstractWatchable pWatchable) {
		assert pWatchable != null;
		aList.add(pWatchable);
		//Add the current watchlist to the watchtable obejct so that we can notify it
		pWatchable.acceptList(this);
	}
	
	public void addWatchableCommand(AbstractWatchable pWatchable) {
		WatchListCommand add = new WatchListCommand() {
			//When undo the command, the index of watchable object need to be removed = cur size
			Stack<Integer> toRemove = new Stack<Integer>();
			AbstractWatchable toAdd = pWatchable;
			
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				toRemove.push(aList.size());
				addWatchable(toAdd);
				//push this command to Command Stack and clear the undo Stack
				doneByOrder.push(this);
				doneByUndo.clear();
				afterUndo = false;
				//doneByUndo.push(this);
				return Optional.empty();
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				removeWatchable(toRemove.pop());
			}

			@Override
			public Optional<AbstractWatchable> redo() {
				// TODO Auto-generated method stub
				toRemove.push(aList.size());
				addWatchable(toAdd);
				//Need to push the command here since the state will be changed by calling redo
				doneByOrder.push(this);
				return Optional.empty();
			}
		};
		add.execute();
	}
	
	/**
	 * Retrieves and removes the Watchable at the specified index.
	 * 
	 * @param pIndex
	 *            the position of the Watchable to remove
	 * @pre pIndex < getTotalCount() && pIndex >= 0
	 */
	private AbstractWatchable removeWatchable(int pIndex) {
		assert pIndex < aList.size() && pIndex >= 0;
		if (aNext > pIndex) {
			aNext--;
		}
		//Remove the current watchlist of the watchable object so that we don't notify it anymore
		AbstractWatchable toRemove = aList.remove(pIndex);
		toRemove.withdrawtList(this);
		return toRemove;
	}
	
	/*
	 * A helper function of the undo function of removeWatchableCommand, so that we can restore
	 * the Watchable object we popped correctly
	 */
	
	private void removeUndoHelper(int justRemove, AbstractWatchable toRestore) {
		assert toRestore != null;
		aList.add(justRemove, toRestore);
		//Add the current watchlist to the watchtable obejct so that we can notify it
		toRestore.acceptList(this);
	}
	
	public Watchable removeWatchableCommand(int pIndex) {
		WatchListCommand removeWatchable = new WatchListCommand() {
			//Use Stack to remember the objects we removed
			Stack<AbstractWatchable> toAdd = new Stack<AbstractWatchable>();
			Stack<Integer> recover = new Stack<Integer>();
			int toRemove = pIndex;
			
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				toAdd.push(aList.get(toRemove));
				recover.push(aNext);
				//push this command to Command Stack and clear the undo Stack
				doneByOrder.push(this);
				doneByUndo.clear();
				afterUndo = false;
				//doneByUndo.push(this);
				return Optional.of(removeWatchable(toRemove));
			}

			@Override
			public void undo() {
				//aNext might be changed in remove execution, need to recover it here
				aNext = recover.pop();
				AbstractWatchable toRestore = toAdd.pop();
				removeUndoHelper(toRemove, toRestore);
			}

			@Override
			public Optional<AbstractWatchable> redo() {
				// TODO Auto-generated method stub
				toAdd.push(aList.get(toRemove));
				recover.push(aNext);
				//Need to push command here since the state will be changed by calling redo
				doneByOrder.push(this);
				return Optional.of(removeWatchable(toRemove));
			}
		};
		Optional<AbstractWatchable> result = removeWatchable.execute();
		return result.get();
	}
	
	@Override
	public AbstractWatchable next() {
		WatchListCommand getNext = new WatchListCommand() {
			Stack<Integer> recover = new Stack<Integer>();
			
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				assert getRemainingCount() > 0;
				recover.push(aNext);
				AbstractWatchable next = aList.get(aNext);
				aNext++;
				if (aNext >= aList.size()) {
					aNext = 0;
				}
				//push this command to Command Stack and clear the undo Stack
				doneByOrder.push(this);
				doneByUndo.clear();
				afterUndo = false;
				return Optional.of(next);
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				//Just need to simply recover the value of aNext here, other status not changed
				aNext = recover.pop();
			}

			@Override
			public Optional<AbstractWatchable> redo() {
				// TODO Auto-generated method stub
				assert getRemainingCount() > 0;
				recover.push(aNext);
				AbstractWatchable next = aList.get(aNext);
				aNext++;
				if (aNext >= aList.size()) {
					aNext = 0;
				}
				//Need to push this command since the state will be changed by calling redo
				doneByOrder.push(this);
				return Optional.of(next);
			}
		};
		Optional<AbstractWatchable> result = getNext.execute();
		return result.get();
	}
	
	@Override
	public void reset() {
		WatchListCommand reset = new WatchListCommand() {
			 int recover = aNext;
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				aNext = 0;
				//push this command to Command Stack and clear the undo Stack
				doneByOrder.push(this);
				doneByUndo.clear();
				afterUndo = false;
				//doneByUndo.push(this);
				return Optional.empty();
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				aNext = recover;
			}

			@Override
			public Optional<AbstractWatchable> redo() {
				// TODO Auto-generated method stub
				aNext = 0;
				//No need to push the command here, since the state will not be changed
				return Optional.empty();
			}
		};
		reset.execute();
	}
	
	public void undo() {
		//Restore the last state directly
		if(!this.doneByOrder.isEmpty()) {
			WatchListCommand toUndo = this.doneByOrder.pop();
			toUndo.undo();
			this.doneByUndo.push(toUndo);
			this.afterUndo = true;
		}
	}
	
	public Optional<AbstractWatchable> redo(){
		//Redo the undone command when we've called undo
		if(!this.doneByUndo.isEmpty() && this.afterUndo) {
			WatchListCommand toRedo = this.doneByUndo.pop();
			return toRedo.redo();
		}
		//Redo the last command we called when we've not called undo
		if(this.doneByUndo.isEmpty() && !this.doneByOrder.isEmpty() && !this.afterUndo) {
			WatchListCommand toRedo = this.doneByOrder.peek();
			return toRedo.redo();
		}
		return Optional.empty();
	}
	
	/**
	 * @return the total number of valid watchable elements
	 */
	public int getValidCount() {
		int count = 0;
		for (Watchable item : aList) {
			if (item.isValid()) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public int getTotalCount() {
		return aList.size();
	}
	
	@Override
	public int getRemainingCount() {
		return aList.size() - aNext;
	}
	
	@Override
	public Iterator<AbstractWatchable> iterator() {
		return Collections.unmodifiableList(aList).iterator();
	}
	
	/*
	 * return the most recently watched object of the current watchlist
	 */
	@Override
	public AbstractWatchable lastWatched() {
		if(this.mostRecent == null) {
			throw new AssertionError("You have not watched anything yet");
		}
		else {
			return this.mostRecent;
		}
	}
	
	/*
	 * Implementation of the Observer pattern so that the observers can be notified
	 */
	@Override
	public void notifyToUpdate(AbstractWatchable w) {
		assert w != null;
		this.mostRecent = w;
	}
	
	/*
	 * Used to test and show result in Driver
	 */
	public void getList(){
		String[] all = new String[aList.size()];
		for(int i = 0; i < all.length; i ++) {
			all[i] = this.aList.get(i).getTitle();
		}
		System.out.println(Arrays.deepToString(all));
	}
}

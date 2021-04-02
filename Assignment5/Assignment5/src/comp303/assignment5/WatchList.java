package comp303.assignment5;

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
	
	private AbstractWatchable mostRecent = null;
	
	private Stack<WatchListCommand> doneByUser = new Stack<WatchListCommand>();
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
				return Optional.empty();
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				aName = previousName;
			}
		};
		setName.execute();
		this.doneByUser.push(setName);
		this.doneByUndo.clear();
		this.doneByUndo.push(setName);
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
		WatchListCommand addWatchable = new WatchListCommand() {
			//When undo the command, the index of watchable object need to be removed = cur size
			int toRemove = aList.size();
			AbstractWatchable toAdd = pWatchable;
			
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				addWatchable(toAdd);
				return Optional.empty();
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				removeWatchable(toRemove);
			}
		};
		addWatchable.execute();
		this.doneByUser.push(addWatchable);
		this.doneByUndo.clear();
		this.doneByUndo.push(addWatchable);
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
	
	public Watchable removeWatchableCommand(int pIndex) {
		WatchListCommand removeWatchable = new WatchListCommand() {
			AbstractWatchable toAdd = aList.get(pIndex);
			int recover = aNext;
			int toRemove = pIndex;
			
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				return Optional.of(removeWatchable(toRemove));
			}

			@Override
			public void undo() {
				//aNext might be changed in remove execution, need to recover it here
				aNext = recover;
				addWatchable(toAdd);
			}
		};
		this.doneByUser.push(removeWatchable);
		this.doneByUndo.clear();
		this.doneByUndo.push(removeWatchable);
		return removeWatchable.execute().get();
	}
	
	@Override
	public AbstractWatchable next() {
		WatchListCommand getNext = new WatchListCommand() {
			int recover = aNext;
			
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				assert getRemainingCount() > 0;
				AbstractWatchable next = aList.get(aNext);
				aNext++;
				if (aNext >= aList.size()) {
					aNext = 0;
				}
				return Optional.of(next);
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				//Just need to simply recover the value of aNext here, other status not changed
				aNext = recover;
			}
		};
		this.doneByUser.push(getNext);
		this.doneByUndo.clear();
		this.doneByUndo.push(getNext);
		return getNext.execute().get();
	}
	
	@Override
	public void reset() {
		WatchListCommand reset = new WatchListCommand() {
			 int recover = aNext;
			@Override
			public Optional<AbstractWatchable> execute() {
				// TODO Auto-generated method stub
				 aNext = 0;
				 return Optional.empty();
			}

			@Override
			public void undo() {
				// TODO Auto-generated method stub
				aNext = recover;
			}
		};
		reset.execute();
		this.doneByUser.push(reset);
		this.doneByUndo.clear();
		this.doneByUndo.push(reset);
	}
	
	public void undo() {
		if(!this.doneByUser.isEmpty()) {
			WatchListCommand toUndo = this.doneByUser.pop();
			toUndo.undo();
			this.doneByUndo.push(toUndo);
		}
	}
	
	public Optional<AbstractWatchable> redo(){
		if(!this.doneByUndo.isEmpty()) {
			WatchListCommand toRedo = this.doneByUndo.pop();
			this.doneByUser.push(toRedo);
			if(this.doneByUndo.isEmpty()) {
				this.doneByUndo.push(toRedo);
			}
			return toRedo.execute();
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
	
	public Watchable lastWatched() {
		assert this.mostRecent != null;
		return this.mostRecent;
	}
	
	@Override
	public void notifyToUpdate(AbstractWatchable w) {
		assert w != null;
		this.mostRecent = w;
	}
}

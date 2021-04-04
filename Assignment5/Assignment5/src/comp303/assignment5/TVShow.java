package comp303.assignment5;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import comp303.assignment5.TVShow.Episode;

/**
 * Represents a single TV show, with at least a title, language, and publishing studio. Each TVShow aggregates episodes.
 */
public class TVShow  extends AbstractWatchable implements Bingeable<Episode> {
	//Remove all common fields
	private Optional<Episode> aPrototype = Optional.empty();
	private List<Episode> aEpisodes = new ArrayList<>();
	private int aNextToWatch;
	
	/**
	 * Creates a TVShow with required metadata about the show.
	 *
	 * @param pTitle
	 *            official title of the TVShow
	 * @param pLanguage
	 *            language of the TVShow, in full text (e.g., "English")
	 * @param pStudio
	 *            studio which originally published the movie
	 * @param pCast
	 *            standard cast of the episodes in TVShow in <CharacterName, ActorName> pairs
	 * @pre pTitle!=null && pLanguage!=null && pStudio!=null
	 */
	public TVShow(String pTitle, Language pLanguage, String pStudio, Map<String, String> pCast) {
		//Instantiate the common fields first
		super(pTitle, pLanguage, pStudio);
		aNextToWatch = 0;
	}
	
	/*
	 * Watch the TVShow and notify all observers to update the most recent watched
	 */
	@Override
	public void watch() {
		for (Episode episode : aEpisodes) {
			if (episode.isValid()) {
				episode.watch();
			}
		}
		//Need call the super method here, since the last watched should be the whole TVShow
		super.watch();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return true if the TV show contains at least one valid episode
	 */
	@Override
	public boolean isValid() {
		for (Episode episode : aEpisodes) {
			if (episode.isValid()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates a new Episode for this TV show, and adds it the end of the episode list.
	 * 
	 * @param pPath
	 *            the location of the video file of the episode
	 * @param pTitle
	 *            the title of the episode
	 * @return the newly created episode
	 */
	public Episode createAndAddEpisode(File pPath, String pTitle) {
		Episode episode = new Episode(aEpisodes.size() + 1, pTitle, pPath);
		aEpisodes.add(episode);
		return episode;
	}
	
	/**
	 * Selects an Episode by index to use as a prototype for the creation of new episodes. The prototype will not be
	 * updated if the original episode is modified following this change.
	 * 
	 * @param pEpisodeNumber
	 *            1-based index of the episode to use as prototype
	 */
	public void setEpisodePrototype(int pEpisodeNumber) {
		assert pEpisodeNumber <= aEpisodes.size();
		aPrototype = Optional.of(getEpisode(pEpisodeNumber).clone());
	}
	
	/**
	 * Creates a new Episode for this TV show based on the current prototype, and adds it at the end of the episode
	 * list. If no prototype was selected, the first episode is set as the prototype.
	 * 
	 * @param pPath
	 *            the location of the video file of the episode
	 * @param pTitle
	 *            the title of the episode
	 * @return the newly created episode
	 * @pre getTotalCount() > 0
	 */
	public Episode createAndAddEpisodeFromPrototype(File pPath, String pTitle) {
		assert getTotalCount() > 0;
		if (aPrototype.isEmpty()) {
			setEpisodePrototype(1);
		}
		Episode episode = aPrototype.get().clone();
		episode.aEpisodeNumber = aEpisodes.size() + 1;
		episode.aPath = pPath;
		episode.aTitle = pTitle;
		aEpisodes.add(episode);
		return episode;
	}
	
	/**
	 * Returns the Episode of a given number. Episode numbers are 1-based: the first episode is 1, not 0.
	 *
	 * @param pNumber
	 *            the number whose Episode is to be returned
	 * @return the Episode of the given number
	 * @pre there is an episode for the given number
	 */
	public Episode getEpisode(int pNumber) {
		assert aEpisodes.size() >= pNumber;
		return aEpisodes.get(pNumber - 1);
	}
	
	@Override
	public int getTotalCount() {
		return aEpisodes.size();
	}
	
	@Override
	public int getRemainingCount() {
		return aEpisodes.size() - aNextToWatch;
	}
	
	@Override
	public Episode next() {
		assert getRemainingCount() > 0;
		Episode nextEpisode = aEpisodes.get(aNextToWatch);
		aNextToWatch++;
		if (aNextToWatch >= aEpisodes.size()) {
			aNextToWatch = 0;
		}
		return nextEpisode;
	}
	
	@Override
	public void reset() {
		aNextToWatch = 0;
	}
	
	@Override
	public Iterator<Episode> iterator() {
		return Collections.unmodifiableList(aEpisodes).iterator();
	}
	
	/*
	 * Since we assume that if a watch list add a TVshow, then all episodes of the TVShow
	 * will be added to the watch list as well. As a consequence, we need to let all episodes
	 * add the observer as well
	 */
	@Override
	protected void acceptList(Observer w) {
		super.acceptList(w);
		for(int i = 0; i < this.getTotalCount(); i ++) {
			Episode e = this.getEpisode(i + 1);
			e.acceptList(w);
		}
	}

	/*
	 * Since we assume that if a watch list remove a TVshow, then all episodes of the TVShow
	 * will be removed off the watch list as well. As a consequence, we need to let all episodes
	 * remove the observer as well
	 */
	@Override
	protected void withdrawtList(Observer w) {
		super.withdrawtList(w);
		for(int i = 0; i < this.getTotalCount(); i ++) {
			Episode e = this.getEpisode(i + 1);
			e.withdrawtList(w);
		}
	}
	
	/**
	 * Represents a single episode, with at least a title, and an episode number. Each episode is identified by its path
	 * on the file system.
	 */
	public class Episode extends AbstractWatchable implements Sequenceable<Episode>, Cloneable {
		
		//Remove all common fields
		private File aPath;
		private int aEpisodeNumber;
		private Map<String, String> aCast = new HashMap<>();
		
		/**
		 * Creates an episode from the file path. This method should not be called by a client. Use
		 * TVShow.createAndAddEpisode instead.
		 *
		 * @param pTVShow
		 *            TVShow that this episode is a part of
		 * @param pCast
		 *            cast of the Episode in <CharacterName, ActorName> pairs
		 * @param pEpisodeNumber
		 *            the episode number that identifies the episode
		 */
		private Episode(int pEpisodeNumber, String pTitle, File pPath) {
			//Instantiate the common fields first
			super(pTitle, TVShow.this.getLanguage(), TVShow.this.getStudio());
			aPath = pPath;
			aEpisodeNumber = pEpisodeNumber;
		}
		
		/*
		 * Watch the Episode and notify all observers to update the most recent watched
		 */
		@Override
		public void watch() {
			//Need call the super method here
			super.watch();
			System.out.println("Now watching " + TVShow.this.getTitle() + " [" + aEpisodeNumber + "]: " + aTitle);
		}
		
		@Override
		public boolean isValid() {
			return aPath.isFile() && aPath.canRead();
		}
		
		public TVShow getTVShow() {
			return TVShow.this;
		}
		
		/**
		 * @return the episode number of the episode
		 */
		public int getEpisodeNumber() {
			return aEpisodeNumber;
		}
		
		public String setCast(String pCharacter, String pActor) {
			if (pActor == null) {
				return aCast.remove(pCharacter);
			}
			else {
				return aCast.put(pCharacter, pActor);
			}
		}
		
		public String getCast(String pCharacter) {
			return aCast.get(pCharacter);
		}
		
		public Set<String> getAllCharacters() {
			return Collections.unmodifiableSet(aCast.keySet());
		}
		
		
		@Override
		public boolean hasPrevious() {
			return aEpisodeNumber > 1;
		}
		
		@Override
		public boolean hasNext() {
			return aEpisodeNumber < getTotalCount();
		}
		
		@Override
		public Episode getPrevious() {
			return getEpisode(aEpisodeNumber - 1);
		}
		
		@Override
		public Episode getNext() {
			return getEpisode(aEpisodeNumber + 1);
		}
		
		@Override
		public Episode clone() {
			try {
				Episode clone = (Episode) super.clone();
				clone.aCast = new HashMap<>(this.aCast);
				clone.aTags = new HashMap<>(this.aTags);
				return clone;
			}
			catch (CloneNotSupportedException e) {
				assert false;
				return null;
			}
		}
	}

}
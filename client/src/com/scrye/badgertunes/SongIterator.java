package com.scrye.badgertunes;

public interface SongIterator {
	public boolean hasNext();
	public void stepForward();
	public Node getCurrentSong();
	public boolean hasPrevious();
	public void stepBackward();
}

package com.scrye.badgertunes;

public class NodeSource implements SongIterator {
	
	private Node starting_node;
	
	public NodeSource(Node node) {
		starting_node = node;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stepForward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Node getCurrentSong() {
		// TODO Auto-generated method stub
		return starting_node;
	}

	@Override
	public boolean hasPrevious() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stepBackward() {
		// TODO Auto-generated method stub

	}

}

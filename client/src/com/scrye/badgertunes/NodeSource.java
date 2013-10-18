package com.scrye.badgertunes;

public class NodeSource implements SongIterator {
	
	private Node starting_node;
	private Node current_node;
	
	public NodeSource(Node node) {
		if(node.children == null) {
			starting_node = node.parent;
			current_node = node;
		} else {
		    starting_node = node;
		    while(node.children != null && node.children.size() > 0) {
		    	node = node.children.get(0);
		    }
		    current_node = node;
		}
	}

	@Override
	public boolean hasNext() {
		return (getNext() != null);
	}
	
	static private int indexOf(Node node) {
		if(node.parent == null) {
			return 0;
		}
		return node.parent.children.indexOf(node);
	}
	
	static private boolean isLast(Node node) {
		if(node.parent == null) {
			return true;
		}
		return indexOf(node) == node.parent.children.size() - 1;
	}
	
	static private boolean isFirst(Node node) {
		if(node.parent == null) {
			return true;
		}
		return indexOf(node) == 0;
	}
	
	private Node getNext() {
		Node node = current_node;
		while(isLast(node) && node != starting_node) {
			node = node.parent;
		}
		if(isLast(node)) {
			return null;
		}
		node = node.parent.children.get(indexOf(node) + 1);
		while(node.children != null) {
			node = node.children.get(0);
		}
		return node;
	}

	private Node getPrevious() {
		Node node = current_node;
		while(isFirst(node) && node != starting_node) {
			node = node.parent;
		}
		if(isFirst(node)) {
			return null;
		}
		node = node.parent.children.get(indexOf(node) - 1);
		while(node.children != null) {
			node = node.children.get(node.children.size() - 1);
		}
		return node;
	}
	
	@Override
	public void stepForward() {
		current_node = getNext();
	}

	@Override
	public Node getCurrentSong() {
		return current_node;
	}

	@Override
	public boolean hasPrevious() {
		return (getPrevious() != null);
	}

	@Override
	public void stepBackward() {
		current_node = getPrevious();
	}

}

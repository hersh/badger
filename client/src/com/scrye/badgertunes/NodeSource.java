package com.scrye.badgertunes;

public class NodeSource implements SongIterator {
	
	private Node starting_node;
	private Node current_node;
	
	public NodeSource(Node node) {
		if(node.getChildren() == null) {
			starting_node = node.getParent();
			current_node = node;
		} else {
		    starting_node = node;
		    while(node.getChildren() != null && node.getChildren().size() > 0) {
		    	node = node.getChildren().get(0);
		    }
		    current_node = node;
		}
	}

	@Override
	public boolean hasNext() {
		return (getNext() != null);
	}
	
	static private int indexOf(Node node) {
		if(node.getParent() == null) {
			return 0;
		}
		return node.getParent().getChildren().indexOf(node);
	}
	
	static private boolean isLast(Node node) {
		if(node.getParent() == null) {
			return true;
		}
		return indexOf(node) == node.getParent().getChildren().size() - 1;
	}
	
	static private boolean isFirst(Node node) {
		if(node.getParent() == null) {
			return true;
		}
		return indexOf(node) == 0;
	}
	
	private Node getNext() {
		Node node = current_node;
		while(isLast(node) && node != starting_node) {
			node = node.getParent();
		}
		if(isLast(node)) {
			return null;
		}
		node = node.getParent().getChildren().get(indexOf(node) + 1);
		while(node.getChildren() != null) {
			node = node.getChildren().get(0);
		}
		return node;
	}

	private Node getPrevious() {
		Node node = current_node;
		while(isFirst(node) && node != starting_node) {
			node = node.getParent();
		}
		if(isFirst(node)) {
			return null;
		}
		node = node.getParent().getChildren().get(indexOf(node) - 1);
		while(node.getChildren() != null) {
			node = node.getChildren().get(node.getChildren().size() - 1);
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

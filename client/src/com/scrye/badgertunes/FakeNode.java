package com.scrye.badgertunes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class FakeNode implements Node {

	private Node origin; ///< The node this FakeNode refers to.
	private Node parent;
	private ArrayList<Node> children;
	
	public FakeNode(Node origin_node) {
		origin = origin_node;
	}
	
	public void setParent(Node parent_node) {
		parent = parent_node;
	}
	
	public void addChild(FakeNode new_child) {
        if(children == null) {
        	children = new ArrayList<Node>();
        }
		children.add(new_child);
		new_child.parent = this;
	}
	
	@Override
	public String getName() {
		return origin.getName();
	}

	@Override
	public String getFilename() {
		return origin.getFilename();
	}

	@Override
	public ArrayList<Node> getChildren() {
		return children;
	}

	@Override
	public Node getParent() {
		return parent;
	}

	@Override
	public void writeTags(HashMap<String, Boolean> tag_values) {
		origin.writeTags(tag_values);
	}

	@Override
	public HashMap<String, Boolean> readTags() {
		return origin.readTags();
	}

	@Override
	public void fillTagSet(Set<String> tag_set) {
		origin.fillTagSet(tag_set);
	}
}

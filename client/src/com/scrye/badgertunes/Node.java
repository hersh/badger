package com.scrye.badgertunes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface Node {

	public abstract String toString();

	public abstract String getName();

	public abstract String getFilename();

	public abstract ArrayList<Node> getChildren();

	public abstract Node getParent();

	public abstract void writeTags(HashMap<String, Boolean> tag_values);

	/** @brief Read the tags from a Node object and return them as a simple HashMap. */
	public abstract HashMap<String, Boolean> readTags();

	/** @brief Find all the positive tags this node and all its children and fill @a tag_set with them. */
	public abstract void fillTagSet(Set<String> tag_set);
}
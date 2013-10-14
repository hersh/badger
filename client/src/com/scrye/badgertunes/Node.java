package com.scrye.badgertunes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Node {
    public String name;
    public String filename;
    public ArrayList<Node> children;
    public Node parent;
    
    public String toString() {
    	return name;
    }

    static public String nameFromFilename(String filename) {
    	String[] path_elements = filename.split("/");
    	return path_elements[path_elements.length - 1];
    }
    
    static public Node readJson(JSONObject json) {
        Node node = new Node();
        JSONArray json_children;
        try {
            node.filename = json.getString("Name");
        } catch( JSONException ex ) {
        	return null;
        }
        node.name = nameFromFilename(node.filename);
        try {
            json_children = json.getJSONArray("Children");
        } catch( JSONException ex ) {
            json_children = null;
        }
        if( json_children != null && json_children.length() > 0 ) {
            node.children = new ArrayList<Node>(json_children.length());
            for( int i = 0; i < json_children.length(); i++ ) {
                try {
                    JSONObject json_child = json_children.getJSONObject( i );
                    Node child = readJson( json_child );
                    if( child != null ) {
                        node.children.add( child );
                        child.parent = node;
                    }
                } catch( JSONException ex ) {}
            }
            sortNodes(node.children);
        }
        return node;
    }
    
    static public Node readLocal(File file) {
        Node node = new Node();
        node.filename = file.getPath();
        node.name = file.getName();
        if(file.isDirectory()) {
        	File[] files = file.listFiles();
        	if(files != null && files.length > 0) {
        		node.children = new ArrayList<Node>(files.length);
        		for(int i = 0; i < files.length; i++) {
        			Node child = readLocal(files[i]);
        			if(child != null) {
        				node.children.add(child);
        				child.parent = node;
        			}
        		}
        		sortNodes(node.children);
        	}
        }
        return node;
    }
    
    static public void sortNodes(ArrayList<Node> nodes) {
    	Collections.sort(nodes, new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				return a.name.compareToIgnoreCase(b.name);
			}
    	});
    }
}

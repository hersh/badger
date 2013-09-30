package com.scrye.badgertunes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

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
    
    static public Node read(JSONObject json) {
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
                    Node child = read( json_child );
                    if( child != null ) {
                        node.children.add( child );
                        child.parent = node;
                    }
                } catch( JSONException ex ) {}
            }
        }
        return node;
    }
}

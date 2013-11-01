package com.scrye.badgertunes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Node {
    public String name;
    public String filename;
    public ArrayList<Node> children;
    public Node parent;
    public HashMap<String,Boolean> tags = new HashMap<String,Boolean>();
    
    public String toString() {
    	return name;
    }
    
    public void writeTags(HashMap<String,Boolean> tag_values) {
    	// Clear all tags and re-write them based on tag_values and parents' tags.
    	tags.clear();
    	
    	// Loop over all tags in the incoming map
    	for(Map.Entry<String, Boolean> entry : tag_values.entrySet()) {
    	    String tag = entry.getKey();
    	    boolean value = entry.getValue().booleanValue();
    	    
    	    // For each tag, go up the Node tree to see what the parents think of it.
    	    Node node = this;
    	    while(node != null) {
    	    	Boolean tag_val = node.tags.get(tag);
    	    	if(tag_val != null) {
    	    		// Only store this tag locally if the parent's version is different.
    	    		if(tag_val.booleanValue() != value) {
    	    			tags.put(tag, value);
    	    		}
        	    	// The first parent we find with an opinion is the only one we care about.
    	    		break;
    	    	}
    	    	node = node.parent;
    	    }
    	    // If we got to the root without seeing this tag and it's positive, add it in.
    	    if(node == null && value == true) {
    	    	tags.put(tag, value);
    	    }
    	}
    	
    	writeTagsToFile();
    }
    
    public static String readFile( String filePath ) throws IOException {
        Reader reader = new FileReader( filePath );
        StringBuilder sb = new StringBuilder(); 
        char buffer[] = new char[16384];  // read 16k blocks
        int len; // how much content was read? 
        while( ( len = reader.read( buffer ) ) > 0 ){
            sb.append( buffer, 0, len ); 
        }
        reader.close();
        return sb.toString();
    }
    
    // Tag file format: (json)
    // this_dir: {tag1: true, tag2: false},
    // filename.mp3: {tag2: true, tag3: true},
    // other_file.mp3: {tag1: false}
    private void writeTagsToFile() {
    	String tagfile;
    	if(children == null) {
    		tagfile = parent.filename;
    	} else {
    		tagfile = filename;
    	}
    	tagfile += "/tags";
    	JSONObject json = null;
    	try {
			String tagfile_contents = readFile(tagfile);
			json = new JSONObject(tagfile_contents);
		} catch (IOException e) {
		} catch (JSONException e) {
		}
    	if(json == null) {
    		json = new JSONObject();
    	}
    	String key;
    	if(children == null) {
    		key = name;
    	} else {
    		key = "this_dir";
    	}
    	JSONObject tags_json = new JSONObject();
    	for(Map.Entry<String, Boolean> entry : tags.entrySet()) {
    	    String tag = entry.getKey();
    	    boolean value = entry.getValue().booleanValue();
    	    try {
				tags_json.put(tag, value);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	try {
			json.put(key, tags_json);
			String json_string_out = json.toString(2);
			writeFile(json_string_out, tagfile);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void writeFile(String data, String file_path) {
        try {
        	FileWriter writer = new FileWriter(file_path);
            writer.write(data);
            writer.close();
        }
        catch (IOException e) {
			e.printStackTrace();
        } 
    }
    
    public HashMap<String,Boolean> readTags() {
    	HashMap<String, Boolean> result = new HashMap<String, Boolean>();
	    Node node = this;
	    while(node != null) {
	       	for(Map.Entry<String, Boolean> entry : node.tags.entrySet()) {
	    	    String tag = entry.getKey();
	    	    boolean value = entry.getValue().booleanValue();
	    	    if(result.get(tag) == null) {
	    	    	result.put(tag, value);
	    	    }
	       	}
	    	node = node.parent;
	    }    	
    	return result;
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

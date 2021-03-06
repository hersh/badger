package com.scrye.badgertunes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RealNode implements Node {
    private String name;
    private String filename;
    private ArrayList<Node> children;
    private RealNode parent;
    private HashMap<String,Boolean> tags = new HashMap<String,Boolean>();
    private int scroll_y;
    
    @Override
	public String toString() {
    	return getName();
    }
    
    public String getName() {
		return name;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public ArrayList<Node> getChildren() {		
		return children;
	}

	@Override
	public Node getParent() {
		return parent;
	}

	/** @brief Return a tree of FakeNodes which have the given tag.  If tag is "None", return this node. */
	public Node filter(String tag) {
    	if(tag == "None") {
    		return this;
    	} else {
    		return filterInternal(tag, false);
    	}
    }

	private FakeNode filterInternal(String tag, boolean tag_base_value) {
		Boolean value = tags.get(tag);
		boolean tag_val = (value != null && value.booleanValue() == true || value == null && tag_base_value == true);

		if(children == null) {
			if(tag_val == true) {
				return new FakeNode(this);
			} else {
				return null;
			}
		} else {
			FakeNode new_node = new FakeNode(this);
			for(Node child: children) {
				RealNode real_child = (RealNode) child;
				FakeNode new_child = real_child.filterInternal(tag, tag_val);
				if(new_child != null) {
					new_node.addChild(new_child);
				}
			}
			if(new_node.getChildren() != null) {
				return new_node;
			} else {
				return null;
			}
		}
	}
	
	private boolean hasTag(String tag) {
    	RealNode node = this;
	    while(node != null) {
	    	Boolean value = node.tags.get(tag);
	    	if(value != null) {
	    		return value.booleanValue();
	    	}
	    	node = node.parent;
	    }
	    return false;
    }
    
    /** @brief Recursively scan this tag and all its children, adding positive tags to tag_set. */
    public void fillTagSet(Set<String> tag_set) {
    	// Harvest all the positive tag keys in this node.
    	for(Map.Entry<String, Boolean> entry : tags.entrySet()) {
    	    String tag = entry.getKey();
    	    if(entry.getValue().booleanValue() == true) {
    	    	tag_set.add(tag);
    	    }
    	}
    	// recursively call this on all children
    	if(getChildren() != null) {	
    		for(Node child : getChildren()) {
    			child.fillTagSet(tag_set);
    		}
    	}
    }
    
    /* (non-Javadoc)
	 * @see com.scrye.badgertunes.NodeInterface#writeTags(java.util.HashMap)
	 */
    @Override
	public void writeTags(HashMap<String,Boolean> tag_values) {
    	// Clear all tags and re-write them based on tag_values and parents' tags.
    	tags.clear();
    	
    	// Loop over all tags in the incoming map
    	for(Map.Entry<String, Boolean> entry : tag_values.entrySet()) {
    	    String tag = entry.getKey();
    	    boolean value = entry.getValue().booleanValue();
    	    
    	    // For each tag, go up the Node tree to see what the parents think of it.
    	    RealNode node = this;
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
    	if(getChildren() == null) {
    		tagfile = getParent().getFilename();
    	} else {
    		tagfile = getFilename();
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
    	if(getChildren() == null) {
    		key = getName();
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
    
    @Override
	public HashMap<String,Boolean> readTags() {
    	HashMap<String, Boolean> result = new HashMap<String, Boolean>();
	    RealNode node = this;
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
    
    static public RealNode readJson(JSONObject json) {
        RealNode node = new RealNode();
        JSONArray json_children;
        try {
            node.filename = json.getString("Name");
        } catch( JSONException ex ) {
        	return null;
        }
        node.name = nameFromFilename(node.getFilename());
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
                    RealNode child = readJson( json_child );
                    if( child != null ) {
                        node.getChildren().add( child );
                        child.parent = node;
                    }
                } catch( JSONException ex ) {}
            }
            node.sortChildren();
        }
        return node;
    }
    
    private void setTagsFromJSON(JSONObject tags_json) {
		Iterator<?> keys = tags_json.keys();
		while(keys.hasNext()) {
			String tag = (String) keys.next();
			boolean value;
			try {
				value = tags_json.getBoolean(tag);
				tags.put(tag, value);
			} catch (JSONException e) {
			}
		}
    }
    
    static public RealNode readLocal(File file) {
        RealNode node = new RealNode();
        node.filename = file.getPath();
        node.name = file.getName();
        if(node.getName().equals("tags")) {
        	return null;
        }
        if(file.isDirectory()) {
        	JSONObject tags_json = null;
        	try {
        		String tagfile = node.getFilename() + "/tags";
        		String tagfile_contents = readFile(tagfile);
    			tags_json = new JSONObject(tagfile_contents);
    			JSONObject this_dir_tags_json = tags_json.getJSONObject("this_dir");
    			node.setTagsFromJSON(this_dir_tags_json);
    		} catch (IOException e) {
    		} catch (JSONException e) {
    		}
        	
        	File[] files = file.listFiles();
        	if(files != null && files.length > 0) {
        		node.children = new ArrayList<Node>(files.length);
        		for(int i = 0; i < files.length; i++) {
        			RealNode child = readLocal(files[i]);
        			if(child != null) {
        				node.getChildren().add(child);
        				child.parent = node;
        				// Tags for song files are stored in the tags file in the containing directory.
        				// We've already read that file into tags_json, so look up any tags for this song in it.
        				if(child.getChildren() == null && tags_json != null) {
        					try {
        						JSONObject child_tags_json = tags_json.getJSONObject(child.getName());
        						child.setTagsFromJSON(child_tags_json);
        					} catch (JSONException e) {
        					}
        				}
        			}
        		}
        		node.sortChildren();
        	}
        }
        return node;
    }
    
    public void sortChildren() {
    	Collections.sort(children, new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				return a.getName().compareToIgnoreCase(b.getName());
			}
    	});
    }

	@Override
	public int getScrollY() {
		return scroll_y;
	}
	
	@Override
	public void setScrollY(int scroll_y) {
		this.scroll_y = scroll_y;
	}
}

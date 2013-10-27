package com.scrye.badgertunes;

import java.util.ArrayList;
import java.util.Set;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ApplyTagDialog extends DialogFragment {
	private Node node;
	private PlayerActivity pa;
	private Set<String> all_tags;
	private View view;
	
	public static ApplyTagDialog create(Node node) {
		ApplyTagDialog new_fragment = new ApplyTagDialog();
		Bundle args = new Bundle();
		ArrayList<String> node_path = new ArrayList<String>();
		while(node != null) {
			node_path.add(0, node.name);
			node = node.parent;
		}
		args.putStringArrayList("node", node_path);
		new_fragment.setArguments(args);
		return new_fragment;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        pa = (PlayerActivity) getActivity();
        ArrayList<String> node_path = getArguments().getStringArrayList("node");
        Node node = pa.getLocalRoot();
        for(int i = 1; i < node_path.size(); i++) {
    		boolean found = false;
        	if(node.children != null) {
        		for(int child_index = 0; child_index < node.children.size(); child_index++) {
        			if(node.children.get(child_index).name == node_path.get(i)) {
        				node = node.children.get(child_index);
        				found = true;
        				break;
        			}
        		}
        	}
        	if(!found) {
       			pa.showError("Failed to find child " + node_path.get(i) + " in node " + node.filename);
       			dismiss();
       			return;
       		}
       	}
        this.node = node;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(node == null) {
    		dismiss();
    		return null;
    	}
    	view = inflater.inflate(R.layout.apply_tag_dialog, container, false);
    	
        TextView name_view = (TextView) view.findViewById(R.id.name);
        name_view.setText(node.filename);
        
        TextView no_tags_text_view = (TextView) view.findViewById(R.id.no_tags_text);
        ListView tag_list_view = (ListView) view.findViewById(R.id.tag_list);
        tag_list_view.setEmptyView(no_tags_text_view);
        
        all_tags = pa.getAllTags();
        showTags();
        
        EditText text_editor = (EditText) view.findViewById(R.id.edit_new_tag);
        text_editor.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addTag(v.getText().toString());
                    v.setText("");
                    handled = true;
                }
                return handled;
            }
        });
        
        return view;
    }

    private void showTags() {
        ArrayList<String> tags = new ArrayList<String>();
        tags.addAll(all_tags);
        ListView tag_list_view = (ListView) view.findViewById(R.id.tag_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(pa, android.R.layout.simple_list_item_1, tags);
        tag_list_view.setAdapter(adapter);
    }

    public void addTag(String new_tag) {
    	if(new_tag != null && new_tag.length() > 0) {
    		all_tags.add(new_tag);
    		showTags();
    	}
    }
}

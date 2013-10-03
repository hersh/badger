package com.scrye.badgertunes;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class NodeAdapter extends ArrayAdapter<Node> {
	
	private PlayerActivity player;

	public NodeAdapter(PlayerActivity _player, ArrayList<Node> objects) {
		super(_player, R.id.title, objects);
		player = _player;
	}

	@Override
	public View getView(int pos, View convert_view, ViewGroup parent) { 
		NodeView view = (NodeView) convert_view;
		if(view == null) {
			view = new NodeView(player);
		}
		return view;
	}
}

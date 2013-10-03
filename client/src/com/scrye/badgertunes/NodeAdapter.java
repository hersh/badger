package com.scrye.badgertunes;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NodeAdapter extends ArrayAdapter<Node> {

	private PlayerActivity player;
	
	public NodeAdapter(PlayerActivity _player,
				       int textViewResourceId,
			           List<Node> objects) {
		super(_player, textViewResourceId, objects);
		player = _player;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater) player.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        view = inflater.inflate(R.layout.song_item, null);
	    }
		ClickableNodeText title_view = (ClickableNodeText) view.findViewById(R.id.title);
		Node node = getItem(position);
	    title_view.setNode(node);
		return view;
	}
}

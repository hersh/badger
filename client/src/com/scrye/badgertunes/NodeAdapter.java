package com.scrye.badgertunes;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

public class NodeAdapter extends ArrayAdapter<Node> {

	private PlayerActivity player;
	private boolean use_local;
	
	public NodeAdapter(PlayerActivity _player,
				       int textViewResourceId,
			           List<Node> objects,
			           boolean _use_local) {
		super(_player, textViewResourceId, objects);
		player = _player;
		use_local = _use_local;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if(view == null) {
			int layout_id = use_local ? R.layout.local_song_item : R.layout.remote_song_item;
			LayoutInflater inflater = (LayoutInflater) player.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        view = inflater.inflate(layout_id, null);
	    }
		ItemButton title_view = (ItemButton) view.findViewById(R.id.title);
		final Node node = getItem(position);
	    title_view.setNode(node);
	    
	    if(!use_local) {
	    	ImageButton download_button = (ImageButton) view.findViewById(R.id.download_button);
	    	download_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					player.downloadNode(node);
				}
	    	});
	    }
	    // ImageButton play_button = (ImageButton) view.findViewById(R.id.play_button);
	    
		return view;
	}
}

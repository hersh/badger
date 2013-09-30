package com.scrye.badgertunes;

import android.view.View;
import android.widget.Button;

public class DirectoryButton extends Button implements View.OnClickListener{
	public Node node;
	private PlayerActivity player;
	
	public DirectoryButton(PlayerActivity _player, Node _node) {
		super(_player);
		player = _player;
		node = _node;
		setOnClickListener(this);
		setText(node.name);
	}

	@Override
	public void onClick(View v) {
		player.setCurrentDirectory(node);
	}
}

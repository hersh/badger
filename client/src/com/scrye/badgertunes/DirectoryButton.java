package com.scrye.badgertunes;

import android.view.View;
import android.widget.Button;

public class DirectoryButton extends Button implements View.OnClickListener{
	public Node node;
	private PlayerActivity player;
	
	public DirectoryButton(PlayerActivity _player, Node node2) {
		super(_player);
		player = _player;
		node = node2;
		setOnClickListener(this);
		setText(node.getName());
	}

	@Override
	public void onClick(View v) {
		player.setCurrentDirectory(node);
	}
}

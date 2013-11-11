package com.scrye.badgertunes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class ItemButton extends Button implements Button.OnClickListener {

	private PlayerActivity player;
	private Node node;
	
	public ItemButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ItemButton(Context context) {
		super(context);
		init(context);
	}

	public ItemButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		player = (PlayerActivity) context;
		this.setOnClickListener(this);
	}
	
	public void setNode(Node node2) {
		node = node2;
		setText(node.getName());
	}

	@Override
	public void onClick(View clicked_view) {
		player.onListedNodeClicked(node);
	}
}

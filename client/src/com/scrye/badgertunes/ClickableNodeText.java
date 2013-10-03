package com.scrye.badgertunes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class ClickableNodeText extends Button implements Button.OnClickListener {

	private PlayerActivity player;
	private Node node;
	
	public ClickableNodeText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ClickableNodeText(Context context) {
		super(context);
		init(context);
	}

	public ClickableNodeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		player = (PlayerActivity) context;
		this.setOnClickListener(this);
	}
	
	public void setNode(Node _node) {
		node = _node;
		setText(node.name);
	}

	@Override
	public void onClick(View clicked_view) {
		player.onListedNodeClicked(node);
	}
}

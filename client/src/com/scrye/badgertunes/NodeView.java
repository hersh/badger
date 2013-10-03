package com.scrye.badgertunes;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NodeView extends LinearLayout {
	private TextView text_view;
	
	public NodeView(Context context) {
		super(context);
		setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                       android.R.attr.listPreferredItemHeight));
		
		text_view = new TextView(context);
		text_view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					                                            ViewGroup.LayoutParams.MATCH_PARENT));
		text_view.setId(R.id.title);
		this.addView(text_view);
	}
}

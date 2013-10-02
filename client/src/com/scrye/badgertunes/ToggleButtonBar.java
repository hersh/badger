package com.scrye.badgertunes;
import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.view.View;
import android.view.View.OnClickListener;

public class ToggleButtonBar extends LinearLayout implements OnClickListener {
	
	private ArrayList<TBBButton> buttons = new ArrayList<TBBButton>();
	private Context context;
	private PlayerActivity listener;

	public ToggleButtonBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ToggleButtonBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ToggleButtonBar(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context _context) {
		context = _context;
	}
	
	public void addButton(String tag, String label, boolean active) {
		TBBButton new_tbbb = new TBBButton();
		new_tbbb.button = new ToggleButton(context);
		new_tbbb.button.setText(label);
		new_tbbb.button.setTextOff(label);
		new_tbbb.button.setTextOn(label);
		new_tbbb.button.setChecked(active);
		new_tbbb.button.setOnClickListener(this);
		new_tbbb.tag = tag;
		buttons.add(new_tbbb);
		addView(new_tbbb.button);
	}
	
	public void setListener(PlayerActivity _listener) {
		listener = _listener;
	}
	
	private class TBBButton {
		public ToggleButton button;
		public String tag;
	}

	@Override
	public void onClick(View v) {
		for(int i = 0; i < buttons.size(); i++) {
			TBBButton tbbb = buttons.get(i);
			if(tbbb.button == v) {
				if(tbbb.button.isChecked()) {
					// user newly checked this button.
					if(listener != null) {
						listener.onToggleButtonChanged(tbbb.tag);
					}
				} else {
					tbbb.button.setChecked(true);
				}
			} else {
				tbbb.button.setChecked(false);
			}
		}
	}

}

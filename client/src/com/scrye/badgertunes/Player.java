package com.scrye.badgertunes;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

// Class for managing playing of music.
// 2 usages:
//  - existing song, paused: just call play().
//  - want to play a different song: call setSource(new_song), then call play().
public class Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
	private SongIterator source;
	private MediaPlayer media_player;
	private PlayerActivity pa;
	private boolean prepared = false;
	private boolean song_set_up = false;
	private ImageButton play_pause_button;
	private ImageButton next_button;
	private ImageButton previous_button;
	private boolean play_button_looks_like_play;

	public Player(PlayerActivity _pa) {
		pa = _pa;
		media_player = new MediaPlayer();
		media_player.setOnPreparedListener(this);
		media_player.setOnCompletionListener(this);
		media_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		media_player.setVolume(1,  1);
	}
	
	public void kill() {
		media_player.release();
		media_player = null;
	}
	
	private void setPlayButtonState(boolean state) {
		play_button_looks_like_play = state;
		if(play_button_looks_like_play) {
			play_pause_button.setImageResource(R.drawable.ic_action_play);
		} else {
			play_pause_button.setImageResource(R.drawable.ic_action_pause);
		}
	}
	
	public void connectButtons() {
		play_pause_button = (ImageButton) pa.findViewById(R.id.play_pause_button);
		play_pause_button.setEnabled(false);
		setPlayButtonState(true);
		play_pause_button.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(play_button_looks_like_play) {
					play();
				} else {
					pause();
				}
			}			
		});
		
		next_button = (ImageButton) pa.findViewById(R.id.next_song_button);
		next_button.setEnabled(false);
		next_button.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				next();
			}
		});

		previous_button = (ImageButton) pa.findViewById(R.id.previous_song_button);
		previous_button.setEnabled(false);
		previous_button.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				previous();
			}
		});
	}

	public void setSource(SongIterator _source) {
		media_player.stop();
		source = _source;
		setupCurrentSong();
		play_pause_button.setEnabled(true);
		next_button.setEnabled(source.hasNext());
		previous_button.setEnabled(source.hasPrevious());
	}

	public SongIterator getSource() {
		return source;
	}
	
	private void setupCurrentSong() {
		pa.showCurrentPlayingSong(source.getCurrentSong());

		if (!song_set_up) {
			try {
				media_player.reset();
				String filename = source.getCurrentSong().filename;
				media_player.setDataSource(filename);
				Log.w("Player", "setDataSource(" + filename + ")");
				song_set_up = true;
				prepared = false;
			} catch (IllegalArgumentException e) {
				pa.showError(e.getMessage());
				e.printStackTrace();
			} catch (SecurityException e) {
				pa.showError(e.getMessage());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				pa.showError(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				pa.showError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void play() {
		if (media_player.isPlaying()) {
			return;
		}
		setPlayButtonState(false);
		setupCurrentSong();
		if (prepared) {
			Log.w("Player", "start()");
			media_player.start();
		} else {
			Log.w("Player", "prepareAsync()");
			media_player.prepareAsync();
		}
	}

	@Override
	public void  onPrepared(MediaPlayer mp) {
		prepared = true;
		Log.w("Player", "start() 2");
		media_player.start();
	}
	
	public void pause() {
		if (media_player.isPlaying()) {
			media_player.pause();
			setPlayButtonState(true);
		}
	}
	
	private void stop() {
		if(media_player.isPlaying()) {
			media_player.stop();
		}
		song_set_up = false;
	}

	public void next() {
		if (source.hasNext()) {
			source.stepForward();
			stop();
			play();
		} else {
			setPlayButtonState(true);
		}
		previous_button.setEnabled(source.hasPrevious());
		next_button.setEnabled(source.hasNext());
	}

	public void previous() {
		if (source.hasPrevious()) {
			source.stepBackward();
			stop();
			play();
		} else {
			setPlayButtonState(true);
		}
		previous_button.setEnabled(source.hasPrevious());
		next_button.setEnabled(source.hasNext());
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		next();
	}
}

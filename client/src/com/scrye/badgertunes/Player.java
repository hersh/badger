package com.scrye.badgertunes;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

// Class for managing playing of music.
// 2 usages:
//  - existing song, paused: just call play().
//  - want to play a different song: call setSource(new_song), then call play().
public class Player implements MediaPlayer.OnPreparedListener {
	private SongIterator source;
	private MediaPlayer media_player;
	private PlayerActivity pa;
	private boolean prepared = false;
	private boolean song_set_up = false;

	public Player(PlayerActivity _pa) {
		pa = _pa;
		media_player = new MediaPlayer();
		media_player.setOnPreparedListener(this);
		media_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		media_player.setVolume(1,  1);
	}

	public void setSource(SongIterator _source) {
		if (media_player.isPlaying()) {
			media_player.stop();
		}
		source = _source;
		setupCurrentSong();
	}

	private void setupCurrentSong() {
		if (!song_set_up) {
			try {
				media_player.reset();
				String filename = source.getCurrentSong().filename;
				media_player.setDataSource(filename);
				Log.w("Player", "setDataSource(" + filename + ")");
				song_set_up = true;
				prepared = false;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void play() {
		if (media_player.isPlaying()) {
			return;
		}
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
	public void onPrepared(MediaPlayer mp) {
		prepared = true;
		Log.w("Player", "start() 2");
		media_player.start();
	}
	
	public void pause() {
		if (media_player.isPlaying()) {
			media_player.pause();
		}
	}

	public void next() {
		if (source.hasNext()) {
			source.stepForward();
			play();
		}
	}

	public void previous() {
		if (source.hasPrevious()) {
			source.stepBackward();
			play();
		}
	}
}

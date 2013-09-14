package com.scrye.badgertunes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class PlayerActivity extends Activity {

    private Handler handler = new Handler();
    private ArrayList<SongInfo> songs = new ArrayList<SongInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
        											 LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        
        ListView song_list_view = (ListView) findViewById(R.id.song_list);
        song_list_view.setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
            	Log.w("PlayerActivity", "loading song list");
            	loadSongList();
            	// When that is done, trigger the display of the list in the GUI thread.
                handler.post(new Runnable() {
                    public void run() {
                        showSongList();
                    }
                });
            }
        }).start();
    }

    private StringBuilder stringBuilderFromReader(InputStreamReader input_stream) throws IOException {
    	BufferedReader r = new BufferedReader(input_stream);
    	StringBuilder total = new StringBuilder();
    	String line;
    	while ((line = r.readLine()) != null) {
    		total.append(line);
    	}
    	return total;
    }

    // Must be run in non-GUI thread
    private void loadSongList() {
    	try{
    	    // Create a new HTTP Client
    	    DefaultHttpClient defaultClient = new DefaultHttpClient();
    	    // Setup the get request
    	    HttpGet httpGetRequest = new HttpGet("http://10.1.10.16:8080/list");

    	    // Execute the request in the client
    	    HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
    	    // Grab the response
    	    StringBuilder sb = stringBuilderFromReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
    	    
    	    // Instantiate a JSON array from the request response
    	    JSONArray json_song_array = new JSONArray(sb.toString());
    	    
    	    loadSongsFromJSON(json_song_array);

    	} catch(Exception e){
    	    // TODO In your production code handle any errors and catch the individual exceptions
    		Log.e("PlayerActivity", e.toString());
    	}
    }
    
    private void loadSongsFromJSON(JSONArray array) {
        songs.clear();
    	for(int i = 0; i < array.length(); i++){
            JSONObject json_song;
			try {
				json_song = array.getJSONObject(i);
			    SongInfo song = new SongInfo();
                song.title = json_song.getString("Title");
                song.album = json_song.getString("Album");
                song.artist = json_song.getString("Artist");
                song.filename = json_song.getString("Filename");
                songs.add(song);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    // Must be run in GUI thread
    private void showSongList() {
    	ArrayAdapter<SongInfo> adapter =
    		new ArrayAdapter<SongInfo>(this, R.layout.song_item, R.id.title, songs);
        ListView song_list_view = (ListView) findViewById(R.id.song_list);
    	song_list_view.setAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }
    
}

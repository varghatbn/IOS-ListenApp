package com.playonlineaudiomp3_android_examples.com;

import java.io.IOException;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

	Button buttonStop,buttonStart ;
	
	String AudioURL = "http://projectfeva.esy.es/FileUploads/uploads/1.mp3";

	MediaPlayer mediaplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        buttonStart = (Button)findViewById(R.id.button1);
        buttonStop = (Button)findViewById(R.id.button2);
        
        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        buttonStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        
		        try {
		        	
					mediaplayer.setDataSource(AudioURL);
					mediaplayer.prepare();
					
					
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
				
				mediaplayer.start();
				
				
			}
		});
        
        buttonStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				mediaplayer.stop();
				
				
			}
		});
    }

}

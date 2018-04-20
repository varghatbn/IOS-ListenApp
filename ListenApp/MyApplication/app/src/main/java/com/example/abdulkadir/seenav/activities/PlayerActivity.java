package com.example.abdulkadir.seenav.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.example.abdulkadir.seenav.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlayerActivity extends AppCompatActivity {




    private MediaPlayer mediaPlayer;
    private int playbackPosition=0;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Button button1 = (Button) findViewById(R.id.button8);
        Button button2 = (Button) findViewById(R.id.button10);

        Button button3 = (Button) findViewById(R.id.button11);
        Button button4 = (Button) findViewById(R.id.button12);

        Button button5 = (Button) findViewById(R.id.button13);
        Button button6 = (Button) findViewById(R.id.button14);

        Button button7 = (Button) findViewById(R.id.button15);
        Button button8 = (Button) findViewById(R.id.button16);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }

        enable_button();
    }





    public void doClick(View view) {
        switch(view.getId()) {
            case R.id.button8:
                try {
                    playAudio("http://projectfeva.esy.es/FileUploads/uploads/1.mp3");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button10:
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                    playbackPosition = 0;
                }
                break;
            case R.id.button11:
                try {
                    playAudio("http://projectfeva.esy.es/FileUploads/uploads/2.mp3");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button12:
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                    playbackPosition = 0;
                }
                break;
            case R.id.button13:
                try {
                    playAudio("http://projectfeva.esy.es/FileUploads/uploads/3.mp3");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button14:
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                    playbackPosition = 0;
                }
                break;
            case R.id.button15:
                try {
                    playAudio("http://projectfeva.esy.es/FileUploads/uploads/5.3pg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button16:
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                    playbackPosition = 0;
                }
                break;
        }
    }

    private void playAudio(String url) throws Exception
    {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer() {
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void enable_button() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(PlayerActivity.this)
                        .withRequestCode(10)
                        .start();

            }
        });
    }

    ProgressDialog progress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK){

            progress = new ProgressDialog(PlayerActivity.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {



                    File f  = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    String content_type  = getMimeType(f.getPath());




                    String file_path = f.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    //PostMethod method = new PostMethod("http://projectfeva.esy.es/FileUploads/save_file.php");
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),MODE_PRIVATE);
                    String userId = sharedPref.getString("userId",null);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                            .addFormDataPart("id",userId)
                            .build();

                    String Username = file_path.substring(file_path.lastIndexOf("/")+1);


                    Request request = new Request.Builder()
                            .url("http://projectfeva.esy.es/FileUploads/save_file.php")
                            .post(request_body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        if(!response.isSuccessful()){
                            throw new IOException("Error : "+response);
                        }

                        progress.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });

            t.start();




        }
    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

}

package com.example.abdulkadir.seenav.musicPlayer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import 	java.net.URI;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.provider.SyncStateContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;

import android.os.Environment;
import android.widget.ToggleButton;

import com.example.abdulkadir.seenav.musicPlayer.DeviceListActivity;
import com.example.abdulkadir.seenav.activities.MainActivity;
import com.example.abdulkadir.seenav.R;
import com.example.abdulkadir.seenav.bluetooth.BluetoothService;
import com.example.abdulkadir.seenav.bluetooth.Constants;
import com.example.abdulkadir.seenav.musicPlayer.musicService;
import com.example.abdulkadir.seenav.musicPlayer.MusicUpdateTask;
import com.example.abdulkadir.seenav.musicPlayer.MusicItem;
import com.example.abdulkadir.seenav.musicPlayer.MusicAdapter;

import static android.R.id.message;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/**
 *
 * @author kk
 *
 */

public class MainPage extends AppCompatActivity {

    private musicService.MusicServiceIBinder mMusicService;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //绑定成功后，取得MusicSercice提供的接口
            mMusicService = (musicService.MusicServiceIBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private MusicUpdateTask mMusicUpdateTask;
    private List<MusicItem> mMusicList;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ListView mMusicListView;
    private ArrayAdapter<String> mConversationArrayAdapter;

    private BluetoothService bS;
    MediaPlayer mMusicPlayer = new MediaPlayer();
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    ToggleButton playpause;
    Button btn1;
    Button Stop;
    Button Play;
    Button Send;
    Button Connect;


    boolean server = false;

    int CurrentPosition = -1;
    String  mConnectedDeviceName;


    //Any numberΩΩΩΩ
    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPlayList() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //设置对话框的图标
        builder.setIcon(R.mipmap.ic_launcher);
        //设计对话框的显示标题
        builder.setTitle("Play List");

        //获取播放列表，把播放列表中歌曲的名字取出组成新的列表
        List<MusicItem> playList = mMusicService.getPlayList();
        ArrayList<String> data = new ArrayList<String>();
        for(MusicItem music : playList) {
            data.add(music.name);
        }
        if(data.size() > 0) {
            //播放列表有曲目，显示音乐的名称
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
            builder.setAdapter(adapter, null);
        }
        else {
            //播放列表没有曲目，显示没有音乐
            //builder.setMessage(getString("Song empty"));
        }

        //设置该对话框是可以自动取消的，例如当用户在空白处随便点击一下，对话框就会关闭消失
        builder.setCancelable(true);

        //创建并显示对话框
        builder.create().show();
    }



    //public Context context = getApplicationContext();
        @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            getPermissionToReadUserContacts();
            //ActivityCompat.OnRequestPermissionsResultCallback(1001,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_GRANTED );

            this.setContentView(R.layout.activity_music_list);
            init();
            mMusicList = new ArrayList<MusicItem>();
            mMusicListView = (ListView) findViewById(R.id.music);
            mMusicUpdateTask = new MusicUpdateTask(this,mMusicListView,mMusicList);
            mMusicUpdateTask.execute();
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();







            Stop = (Button)findViewById(R.id.stop);
            Play = (Button)findViewById(R.id.play_btn2);
            //playpause = (ToggleButton)findViewById(R.id.playPause);

            MusicAdapter adapter = new MusicAdapter(mMusicList, R.layout.musicitem,MainPage.this);



            mMusicListView.setAdapter(adapter);

            Intent i = new Intent(this, musicService.class);
            //启动MusicService




            startService(i);


            if (bS!= null) {
                if (bS.getState() == BluetoothService.STATE_NONE) {
                    bS.start();
                }
            }
           /* btn1.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MainPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();//停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
                }

            });*/
            Stop.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if( mMusicPlayer.isPlaying()) {
                        mMusicPlayer.stop();
                        mMusicPlayer.release();
                    }
                }

            });
          /*  Send.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View view){

                    sendMessage("Hello");

                }



            });*/

          /*  Connect.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View view){

                    Intent intent = new Intent(MainPage.this, DeviceListActivity.class);
                    //int resultText = 1;
                    //intent.putExtra("fromMain", resultText);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);

                }



            });*/

          /*  playpause.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(playpause.isChecked()) {
                        if (!mMusicPlayer.isPlaying())
                            mMusicPlayer.start();
                    }else{
                        if (mMusicPlayer.isPlaying())
                            mMusicPlayer.pause();
                    }

            }
            });*/

            Play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CurrentPosition != -1) {
                        MusicItem item = mMusicList.get(CurrentPosition);
                        try {
                            mMusicPlayer.reset();
                            mMusicPlayer.setDataSource(MainPage.this, item.songUri);


                            mMusicPlayer.prepare();
                            mMusicPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //实现绑定操作

            bindService(i, mServiceConnection, BIND_AUTO_CREATE);


            mMusicListView.setOnItemClickListener(mOnMusicItemClickListener);

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                //System.out.println("fuck");
                //TODO: just enable the button to start capture here
                // Otherwise, setup the chat session
            } else if (bS == null) {
                setup();
            }



        }

    private void init()
    {
        setUpToolbar();
    }

    private void setUpToolbar()
    {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setup() {
        mConversationArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.setup_chat_message);
        bS = new BluetoothService(getApplicationContext(), mHandler);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);

                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setup();
                } else {
                    Toast.makeText(getApplicationContext(),"not enable leaving",
                            Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        System.out.println("address is" + address);
        bS.connect(device, secure);
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bS.getState() != bS.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT).show();
            //System.out.println(bS.getState());
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            //System.out.println(bS.getState());
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bS.write(send);

            // Reset out string buffer to zero and clear the edit text field

        }
    }



    public File getFile(){

        return null;

    };

    public void sendFile(File file){

    }

    //定义监听器
    private AdapterView.OnItemClickListener mOnMusicItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CurrentPosition = position;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mMusicUpdateTask != null && mMusicUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMusicUpdateTask.cancel(true);
        }
        mMusicUpdateTask = null;

        for(MusicItem item : mMusicList) {
            if( item.thumb != null ) {
                item.thumb.recycle();
                item.thumb = null;
            }
        }

        mMusicList.clear();
        unbindService(mServiceConnection);
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what)

            {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:

                            // mConversationArrayAdapter.clear();
                            if (!server) {
                                // Capture Frag exists, so this is the server!
                                System.out.println("This is the server");
                            }
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            System.out.println("Connecting");
                            break;
                        case BluetoothService.STATE_LISTEN:
                            System.out.println("It is listening.");
                        case BluetoothService.STATE_NONE:
                            System.out.println("We have no connection");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    // Message read is sent to both client and server even though client broadcasts
                    if (server) {
                        InputStream MusicFile = (InputStream) msg.obj;


                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != getApplicationContext()) {
                        Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();


                        break;

                    }
            }
        }


    };









}
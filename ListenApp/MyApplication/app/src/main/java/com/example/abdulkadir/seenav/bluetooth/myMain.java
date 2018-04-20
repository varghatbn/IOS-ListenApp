package com.example.abdulkadir.seenav.bluetooth;
/**
 * Created by yinuoyang on 11/14/16.
 */

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;


import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abdulkadir.seenav.musicPlayer.DeviceListActivity;
import com.example.abdulkadir.seenav.activities.MainActivity;
import com.example.abdulkadir.seenav.R;
import com.example.abdulkadir.seenav.musicPlayer.micDList;

import static android.R.attr.data;

public class myMain extends AppCompatActivity implements OnClickListener {

    private ProgressBar playSeekBar;

    private Button buttonPlay;

    private Button buttonStopPlay;

    private MediaPlayer player;

    private Button btn2;
    private Button btn3;
    public AudioRecord recorder;

    private int sampleRate = 8000;      //How much will be ideal?
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    public int minBufSize;

    private AudioTrack speaker;


    private Button receiveServer;

    boolean broadcast = false;
    private Button dList;
    boolean server = false;
    public BluetoothService bS;


    private BluetoothAdapter mBluetoothAdapter = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    String  mConnectedDeviceName;


    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    //Audio Configuration.


    private boolean status = true;





    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymain);
        init();
        initializeUIElements();
        initializeMediaPlayer();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setup();

        if ( bS  != null) {
            if ( bS .getState() == BluetoothService.STATE_NONE) {
                bS.start();
                System.out.println("started?");
            }
        }


        btn3 = (Button)findViewById(R.id.toggleButton6);


        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ensureDiscoverable();
                //停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
            }

        });



        dList=(Button)findViewById(R.id.c2);


        dList.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(myMain.this, micDList.class);
                //int resultText = 1;
                //intent.putExtra("fromMain", resultText);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);

            }



        });

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

    private final OnClickListener receiveListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if(!broadcast && bS.getState()== 1){
                startReceiving();
            }else{
                System.out.println("status is" + bS.getState());
            }

        }

    };

    private void ensureDiscoverable() {
        //TODO: Make discoverable -> replace fragment_bt_capture_base with the fragment_screen_capture
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private final OnClickListener playListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            broadcast= true;
            if(broadcast && bS.getState()== 3){
                server = true;
                startStreaming();
            }else{
                System.out.println("status is" + bS.getState());
            }

        }

    };


    private void setup() {
        mConversationArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.setup_chat_message);
        bS = new BluetoothService(getApplicationContext(), mHandler);
    }

    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        System.out.println("address is" + address);
        bS.connect(device, secure);
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

    private void initializeUIElements() {

        playSeekBar = (ProgressBar) findViewById(R.id.progressBar1);
        playSeekBar.setMax(100);
        playSeekBar.setVisibility(View.INVISIBLE);

        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(playListener);

        buttonStopPlay = (Button) findViewById(R.id.buttonStopPlay);
        buttonStopPlay.setEnabled(false);
        buttonStopPlay.setOnClickListener(this);


    }

    public void onClick(View v) {
        if (v == buttonPlay) {
            startPlaying();
        } else if (v == buttonStopPlay) {
            stopPlaying();
        }
    }

    private void startPlaying() {
        buttonStopPlay.setEnabled(true);
        buttonPlay.setEnabled(false);

        playSeekBar.setVisibility(View.VISIBLE);

        player.prepareAsync();

        player.setOnPreparedListener(new OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });

    }

    private void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            initializeMediaPlayer();
        }

        buttonPlay.setEnabled(true);
        buttonStopPlay.setEnabled(false);
        playSeekBar.setVisibility(View.INVISIBLE);
    }

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource("http://server2.crearradio.com:8371");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playSeekBar.setSecondaryProgress(percent);
                Log.i("Buffering", "" + percent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            player.stop();
        }
    }



    public void startStreaming() {


        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {


                    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);


                    byte[] buffer = new byte[minBufSize];


                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);

                    recorder.startRecording();

                     System.out.print(bS.getState());

                    while(status == true) {


                        //reading data from MIC into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        //putting buffer in the packet
                        //packet = new DatagramPacket (buffer,buffer.length,destination,port);
                        //System.out.println(buffer[0]);
                        bS.write(buffer);

                        //socket.send(packet);


                    }

            }

        });
        streamThread.start();
    }

    public void startReceiving() {
        Thread receiveThread = new Thread (new Runnable() {

            @Override
            public void run() {

                    //DatagramSocket socket = new DatagramSocket(50005);
                    //Log.d("VR", "Socket Created");



                    //BluetoothSocket in = bS.mConnectedThread.getSocket();



                    //minimum buffer size. need to be careful. might cause problems. try setting manually if any problems faced




                    System.out.print(bS.getState());

                    while(status == true) {
                            //DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
                            //socket.receive(packet);
                            //try{in.read(buffer);}catch(IOException e){e.printStackTrace();}
                            //sending data to the Audiotrack obj i.e. speaker


                    }


            }

        });
        receiveThread.start();
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
                            if (server) {
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
                    System.out.println("write");
                    break;
                case Constants.DATA_RECEIVED:
                    System.out.println("received");
                    break;
                case Constants.MESSAGE_READ:
                    // Message read is sent to both client and server even though client broadcasts
                    byte[] readBuf = (byte[]) msg.obj;
                    System.out.println();
                    //System.out.println("string is" + msg.obj.toString());
                    speaker.write(readBuf, 0, minBufSize);
                    System.out.println(readBuf[0]);
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
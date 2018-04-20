package com.example.abdulkadir.seenav.activities;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.abdulkadir.seenav.R;
import com.example.abdulkadir.seenav.bluetooth.BluetoothService;
import com.example.abdulkadir.seenav.bluetooth.Constants;
import com.example.abdulkadir.seenav.bluetooth.myMain;
import com.example.abdulkadir.seenav.musicPlayer.MainPage;
import com.example.abdulkadir.seenav.activities.FeedActivity;
import com.example.abdulkadir.seenav.login.LoginActivity;
import com.example.abdulkadir.seenav.fragments.MainFragment;
import com.example.abdulkadir.seenav.utils.UtilsDevice;
import com.example.abdulkadir.seenav.utils.UtilsMiscellaneous;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService bluetoothService = null;
    private String mConnectedDeviceName = null;

    private final static String sKEY_SAVED_POSITION = "savedPosition";
    private final static int sPOSITION_HOME = 0;
    private final static int sPOSITION_EXPLORE = 1;
    private static final int sDELAY_MILLIS = 250;

    private Toolbar mToolbar;
    private Context mContext;
    private FrameLayout mAccountRow;
    private DrawerLayout mDrawerLayout;
    private int mCurrentPosition = sPOSITION_HOME;
    private ScrimInsetsFrameLayout mScrimInsetsFrameLayout;
    private FrameLayout mHomeRow, mBluetoothRow, mLibraryRow, mMicRow, mPlayRow, mLogoutRow;
    private FloatingActionButton mfab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if ( bluetoothService  != null) {
            if ( bluetoothService .getState() == BluetoothService.STATE_NONE) {
                bluetoothService .start();
            }
        }


    }

    private void init(@NonNull final Bundle savedInstanceState)
    {
        bindResources();
        setUpToolbar();
        setUpDrawer();
        restoreState(savedInstanceState);
    }


    private void bindResources()
    {
        mHomeRow = (FrameLayout) findViewById(R.id.navigation_drawer_items_list_linearLayout_home);
        mAccountRow = (FrameLayout) findViewById(R.id.navigation_drawer_header);
        mBluetoothRow = (FrameLayout) findViewById(R.id.navigation_drawer_items_list_linearLayout_bluetooth);
        mMicRow = (FrameLayout) findViewById(R.id.navigation_drawer_items_list_linearLayout_record);
        mLibraryRow = (FrameLayout) findViewById(R.id.navigation_drawer_items_list_linearLayout_library);
        mPlayRow =  (FrameLayout) findViewById(R.id.navigation_drawer_items_list_linearLayout_play);
        mLogoutRow = (FrameLayout) findViewById(R.id.navigation_drawer_items_list_linearLayout_logout);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
       // mfab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_DrawerLayout);

    }

    private void setUpToolbar()
    {
        setSupportActionBar(mToolbar);
    }

    /*private void setUpIcons()
    {
        setColorStateList(mHomeIcon);
    }*/

    private void setColorStateList(@NonNull final ImageView icon)
    {
        final Drawable homeDrawable = DrawableCompat.wrap(icon.getDrawable());
        DrawableCompat.setTintList
                (
                        homeDrawable.mutate(),
                        ContextCompat.getColorStateList(this, R.color.nav_drawer_icon)
                );

        icon.setImageDrawable(homeDrawable);
    }

    private void setUpDrawer()
    {
        setUpDrawerAffordance();
       // setUpDrawerMaxWidth();
        setUpDrawerClickListeners();
    }

    private void setUpDrawerAffordance()
    {
        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        mDrawerLayout,
                        mToolbar,
                        R.string.navigation_drawer_opened,
                        R.string.navigation_drawer_closed
                )
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                // Disables the burger/arrow animation by default
                super.onDrawerSlide(drawerView, 0);
            }
        };

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void setFloatButton()
    {
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }



    private void setUpDrawerClickListeners()
    {
        mAccountRow.setOnClickListener(this);
        mHomeRow.setOnClickListener(this);
        mBluetoothRow.setOnClickListener(this);
        mLibraryRow.setOnClickListener(this);
        mMicRow.setOnClickListener(this);
        mPlayRow.setOnClickListener(this);
        mLogoutRow.setOnClickListener(this);

    }

    private void setUpDrawerMaxWidth()
    {
        final int probableMinDrawerWidth = UtilsDevice.getScreenWidthInPx(this) -
                UtilsMiscellaneous.getThemeAttributeDimensionSize(this, android.R.attr.actionBarSize);

        final int maxDrawerWidth = getResources()
                .getDimensionPixelSize(R.dimen.navigation_drawer_max_width);

        mScrimInsetsFrameLayout.getLayoutParams().width =
                Math.min(probableMinDrawerWidth, maxDrawerWidth);
    }

    @Override
    public void onClick(View view)
    {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        if (view == mAccountRow)
        {
            startActivityWithDelay(AccountActivity.class);
        }
        else
        {
            if (!view.isSelected())
            {
                if (view == mHomeRow)
                {
                    selectHomeFragment();
                }
                else if (view == mBluetoothRow)
                {
                     startActivityWithDelay(myMain.class);
                }
                else if (view == mLibraryRow)
                {
                    startActivityWithDelay(MainPage.class);
                }
                else if (view == mMicRow)
                {
                    startActivityWithDelay(RecordActivity.class);
                }
                else if (view == mPlayRow)
                {
                    startActivityWithDelay(RetrieveActivity.class);
                }
                else if (view == mLogoutRow)
                {
                    startActivityWithDelay(LoginActivity.class);
                }
            }
        }
    }

    private void restoreState(final @Nullable Bundle savedInstanceState)
    {
        // This allow us to know if the activity was recreated
        // after orientation change and restore the Toolbar title
        if (savedInstanceState != null)
        {
            switch (savedInstanceState.getInt(sKEY_SAVED_POSITION, sPOSITION_HOME))
            {
                case sPOSITION_HOME:
                    selectHomeFragment();
                    break;

                default:
                    //selectExploreFragment();
                    break;

            }
        }
        else
        {
            selectHomeFragment();
        }
    }

    private void selectHomeFragment()
    {
        mCurrentPosition = sPOSITION_HOME;
        deselectRows();
        mHomeRow.setSelected(true);
        setToolbarTitle(R.string.toolbar_title_home);
        MainFragment fragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_activity_content_frame, fragment).commit();
    }

    /**
     * We start the transaction with delay to avoid junk while closing the drawer
     */
    private void replaceFragmentWithDelay(@NonNull final Bundle bundle)
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_content_frame, new MainFragment())
                        .commit();
            }
        }, sDELAY_MILLIS);
    }

    private void setToolbarTitle(@StringRes final int string)
    {
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(string);
        }
    }

    /**
     * We start this activities with delay to avoid junk while closing the drawer
     */
    private void startActivityWithDelay(@NonNull final Class activity)
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(mContext, activity));
            }
        }, sDELAY_MILLIS);
    }

    private void deselectRows()
    {
        mHomeRow.setSelected(false);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState)
    {
        outState.putInt(sKEY_SAVED_POSITION, mCurrentPosition);
        super.onSaveInstanceState(outState);
    }

    private void setup() {
        //mConversationArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.setup_chat_message);
        bluetoothService = new BluetoothService(getApplicationContext(), mHandler);
    }

    private void ensureDiscoverable() {
        //TODO: Make discoverable -> replace fragment_bt_capture_base with the fragment_screen_capture
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what)

            {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            System.out.println("This is the server");
                            // mConversationArrayAdapter.clear();
                            //if (!server) {
                            // Capture Frag exists, so this is the server!
                            //System.out.println("This is the server");
                            //}
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
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}

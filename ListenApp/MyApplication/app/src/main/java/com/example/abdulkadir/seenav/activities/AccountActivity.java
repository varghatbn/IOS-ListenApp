package com.example.abdulkadir.seenav.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.abdulkadir.seenav.R;

/**
 * Created by Abdulkadir on 2016-11-30.
 */

public class AccountActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);
        init();
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
}

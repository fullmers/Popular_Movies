package com.amiculous.popularmoviesi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sarah on 08/02/2018.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsFragment fragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.settings_fragment_container, fragment)
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

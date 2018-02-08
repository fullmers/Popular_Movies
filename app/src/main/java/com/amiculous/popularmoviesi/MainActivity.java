package com.amiculous.popularmoviesi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amiculous.popularmoviesi.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkUtils.buildUrl(this);
//        String sortOrder = getSortOrderPreference();
 //       Log.d("sortOrder", sortOrder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  /*  private String getSortOrderPreference() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String keyForSortOrder = getString(R.string.pref_sort_by_key);
        String defaultSortOrder = getString(R.string.pref_sort_by_popularity);
        return sp.getString(keyForSortOrder, defaultSortOrder);
    }*/
}

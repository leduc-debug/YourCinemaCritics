package com.lduwcs.yourcinemacritics.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lduwcs.yourcinemacritics.R;
import com.lduwcs.yourcinemacritics.fragments.FavoriteFragment;
import com.lduwcs.yourcinemacritics.fragments.HomeFragment;
import com.lduwcs.yourcinemacritics.fragments.ProfileFragment;
import com.lduwcs.yourcinemacritics.fragments.SearchFragment;
import com.lduwcs.yourcinemacritics.uiComponents.BotNavBar;
import com.lduwcs.yourcinemacritics.utils.Genres;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG1";
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    public static SharedPreferences.Editor editor;
    public static Boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        updateTheme();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            initBotNavBar();
        }
        Genres.setData();
    }

    //thuan_loki
    private void initBotNavBar() {
        BotNavBar botNavBar = findViewById(R.id.botNavBar);
        final Fragment[] fragment = new Fragment[1];
        fragment[0] = new HomeFragment();
        loadFragment(fragment[0]);
        botNavBar.setHomeOnClick(v -> {
            fragment[0] = new HomeFragment();
            loadFragment(fragment[0]);
        });
        botNavBar.setSearchOnClick(v -> {
            Log.d("alo", "initBotNavBar: bruh");
            fragment[0] = new SearchFragment();
            loadFragment(fragment[0]);
        });
        botNavBar.setFavOnClick(v -> {
            fragment[0] = new FavoriteFragment();
            loadFragment(fragment[0]);
        });
        botNavBar.setProfileOnClick(v -> {
            fragment[0] = new ProfileFragment();
            loadFragment(fragment[0]);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @SuppressLint("CommitPrefEdits")
    private void updateTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPrefs", 0);
        editor = sharedPreferences.edit();
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private boolean isConnectWifi(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
}
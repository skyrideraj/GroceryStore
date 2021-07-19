package com.maheshwari.stores.grocerystore.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.maheshwari.stores.grocerystore.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import sdk.pendo.io.Pendo;

public class GroceryApplication extends Application {
    private static final String TAG = GroceryApplication.class.getSimpleName();
    public FirebaseAnalytics firebaseAnalyticsInstance;
    public FirebaseRemoteConfig firebaseRemoteConfigInstance;
    // Create the instance
    private static GroceryApplication instance;
    public static GroceryApplication getInstance()
    {
        if (instance== null) {
            synchronized(GroceryApplication.class) {
                if (instance == null)
                    instance = new GroceryApplication();
            }
        }
        // Return the instance
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFirebaseServiceInstances();
        createNotificationChannel();
        initPendoSDK();
    }

    @SuppressLint("MissingPermission")
    private void initPendoSDK() {
        Pendo.PendoInitParams pendoParams = new Pendo.PendoInitParams();
        pendoParams.setVisitorId("Akash Sharma");
        pendoParams.setAccountId("Pendo Test");

        //send Visitor Level Data
        Map<String, Object> userData = new HashMap<>();
        userData.put("age", "29");
        userData.put("country", "India");
        pendoParams.setVisitorData(userData);

        //send Account Level Data
        Map<String, Object> accountData = new HashMap<>();
        accountData.put("Tier", "1");
        accountData.put("Size", "Enterprise");
        pendoParams.setAccountData(accountData);

        String pendoAppKey = "eyJhbGciOiJSUzI1NiIsImtpZCI6IiIsInR5cCI6IkpXVCJ9.eyJkYXRhY2VudGVyIjoidXMiLCJrZXkiOiI0M2UyYWU1MTExOWI2YTY4NzM0Y2M2NTczZGNkOTBmMzdjMThkYWZlZTdjNjI5NjE0OWExZmZjYWQwZWQ5MjE5NTZlNjY2MDEwMzA0NTFlMzdmMmRmNDdkMTlkOTEzMDM2ZDg5NGQwYmU0OGIxOTA2MmYzYmMyMjI3M2QyZGZkOWMyN2I4ODU3ZTY3MTliZjI4MjU0YWNkYjZhOWQ4ZmQ5LmVhMWEwNTVlNjljNjVhMDhlMjYyNzdlNTg1YjZkYThlLjQ3YWI0ZTRlZGE1Yzc3ZjhjYWY0ZDdhYWEzY2Y5OGRlZWJmMjlhOGFmYTBkNjczZDhhODBjYWQzMmU4ZTZhNjcifQ.l54bwl7Fz29BqxxDlK1e0r7EkqNb1KoKarGCdtEsBeNYaalAfhdXXUhOH15-GEzpycXfkdyExNLZyWDsatVukwF5QApt0pnKcuplUFFHNicN4vTbNnnrxPU4E36_pHGWR6C8HEiu7F8JDOw-TPbwwBHTcbHijicmJR4F98RzFZk";

        Pendo.initSDK(
                this,
                pendoAppKey,
                pendoParams);

    }

    public void initFirebaseServiceInstances()
    {
        firebaseAnalyticsInstance = FirebaseAnalytics.getInstance(getApplicationContext());
        firebaseRemoteConfigInstance = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(Utils.FIREBASE_REMOTE_MINIMUM_FETCH_INTERVAL_SECONDS)
                .setFetchTimeoutInSeconds(Utils.FIREBASE_REMOTE_FETCH_TIMEOUT_SECONDS)
                .build();
        firebaseRemoteConfigInstance.setConfigSettingsAsync(configSettings);
        firebaseRemoteConfigInstance.setDefaultsAsync(R.xml.remote_config_default);
    }

    public void intiRemoteConfigFetchAndActivate(){
        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean updated = task.getResult();
                    Log.d(TAG, "Config params updated: " + updated);

                } else {
                    Log.d(TAG, "Config params fetch failed: " + task.getException());
                }
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            NotificationChannel notificationChannel = new NotificationChannel(Utils.CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationChannel.setDescription(name.toString());
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}

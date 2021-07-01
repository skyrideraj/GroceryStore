package com.maheshwari.stores.grocerystore.util;

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

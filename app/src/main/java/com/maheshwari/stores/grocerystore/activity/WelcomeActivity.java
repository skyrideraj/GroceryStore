package com.maheshwari.stores.grocerystore.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.adapter.ViewPagerAdapter;
import com.maheshwari.stores.grocerystore.api.clients.RestClient;
import com.maheshwari.stores.grocerystore.fragment.PaymentFragment;
import com.maheshwari.stores.grocerystore.model.User;
import com.maheshwari.stores.grocerystore.model.UserResult;
import com.maheshwari.stores.grocerystore.util.GroceryApplication;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    Timer timer;
    int page_position = 0;
    LocalStorage localStorage;
    private int dotscount;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        localStorage = new LocalStorage(getApplicationContext());

        if (localStorage.isUserLoggedIn()) {
            if(localStorage.getFirebaseToken()!=null){
                User user = new Gson().fromJson(localStorage.getUserLogin(), User.class);
                updateFCMTokenOnServer(user.getToken(),user.getUser_id(),localStorage.getFirebaseToken());
            }
            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            if(getIntent().getData()!=null){
                mainActivityIntent.putExtra("deeplink",getIntent().getDataString());
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(mainActivityIntent);
            finish();
        }



        GroceryApplication.getInstance().intiRemoteConfigFetchAndActivate();
        setContentView(R.layout.activity_welcome);

        timer = new Timer();
        viewPager = findViewById(R.id.viewPager);

        sliderDotspanel = findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scheduleSlider();
    }

    public void scheduleSlider() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                viewPager.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 2000, 5000);
    }

    @Override
    protected void onStop() {
        timer.cancel();
        super.onStop();
    }

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    public void onLetsClicked(View view) {
        startActivity(new Intent(getApplicationContext(), LoginRegisterActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void updateFCMTokenOnServer(final String token,String user_id, String fcm_token) {

        Call<UserResult> call = RestClient.getRestService(WelcomeActivity.this).updateUserFCM(token,user_id,fcm_token);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    UserResult userResult = response.body();
                    if (userResult.getCode() == 200 || userResult.getCode() == 202) {

                        Log.i(TAG, "Success - User FCM token updated on server - "+userResult.getMessage());
                    } else {
                        Log.e(TAG, "Error - User FCM token update faield -"+userResult.getCode()+" - "+userResult.getMessage());
                    }

                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                Log.e(TAG, "Error FCM update "+t.getMessage());
            }
        });

    }
}

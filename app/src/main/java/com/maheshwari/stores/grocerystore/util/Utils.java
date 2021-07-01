package com.maheshwari.stores.grocerystore.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Spanned;
import android.util.Log;

import androidx.core.text.HtmlCompat;

import com.maheshwari.stores.grocerystore.api.clients.RestClient;


public class Utils {

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Fragments Tags
    public static final String Login_Fragment = "LoginFragment";
    public static final String SignUp_Fragment = "SignUpFragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";
    public static final String CategoryImage = RestClient.BASE_URL + "includes/images/category/";
    public static final String ProductImage = RestClient.BASE_URL + "includes/images/product/";

    //Constants
    public static final int FIREBASE_REMOTE_MINIMUM_FETCH_INTERVAL_SECONDS = 0;
    public static final int FIREBASE_REMOTE_FETCH_TIMEOUT_SECONDS = 60;
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_PAID  = "PAID";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    public static final String CHANNEL_ID = "com.maheshwari.stores.grocerystore.nchannel";

    private static final String TAG = Utils.class.getSimpleName();

    public static Spanned getStrikeThroughTextForProductAdapters(String price, String discounted_price, int discPercentage){
        String htmlText;
        if(discPercentage>1){
            htmlText = "<strike>"+price+"</strike> "+discounted_price;
        }else {
            htmlText = price;
        }
        return HtmlCompat.fromHtml(htmlText,HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    public static Spanned getStrikeThroughTextForCartAdapter(String price, String discounted_price){
        String htmlText;
        if(!price.equalsIgnoreCase(discounted_price)){
            htmlText = "<strike>"+price+"</strike> "+discounted_price;
        }else {
            htmlText = price;
        }
        return HtmlCompat.fromHtml(htmlText,HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    public static boolean isNetworkAvailable(Context context) {
        if(context == null)  return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i(TAG, "Cellular Network is available!");
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i(TAG, "WIFI Network is available!");
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        Log.i(TAG, "Ethernet Network is available!");
                        return true;
                    }
                }
            }

            else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i(TAG, "Network is available!");
                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Network not available " + e.getMessage());
                }
            }
        }
        Log.e(TAG,"Network not available");
        return false;
    }

}

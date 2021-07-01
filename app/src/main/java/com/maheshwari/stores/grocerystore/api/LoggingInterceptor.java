package com.maheshwari.stores.grocerystore.api;

import android.content.Context;
import android.util.Log;

import com.maheshwari.stores.grocerystore.util.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by : Akash Sharma
 */

public class LoggingInterceptor implements Interceptor {
    private Context context;

    public LoggingInterceptor(Context context) {
        this.context = context;
    }

    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        if (!Utils.isNetworkAvailable(context)) {
            throw new NoConnectivityException();
        }
        Request request = chain.request();
        long t1 = System.nanoTime();
        String requestLog = String.format("Sending request %s url",
                request.url(), chain.connection(), request.headers());
        //YLog.d(String.format("Sending request %s on %s%n%s",
        // request.url(), chain.connection(), request.headers()));
        if (request.method().compareToIgnoreCase("post") == 0) {
            requestLog = "\n" + requestLog
            //TODO Uncomment below line if want to log parameters
            //+ "\n" + bodyToString(request)
            ;
        }
        Log.d("TAG", "request" + "\n" + requestLog);

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        String responseLog = String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());

        String bodyString = response.body().string();

        Log.d("TAG", "response" + "\n" + responseLog + "\n" + bodyString);

        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), bodyString))
                .build();
    }

    private class NoConnectivityException extends IOException {
        @Override
        public String getMessage() {
            return "No internet connectivity exception!";
        }
    }
}

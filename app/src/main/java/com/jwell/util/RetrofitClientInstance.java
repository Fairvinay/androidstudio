package com.jwell.util;

import com.squareup.okhttp.Interceptor;
//import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
/*
https://dev.to/tusharsadhwani/connecting-android-apps-to-localhost-simplified-57lm

The Correct, easy way
use adb reverse.

Yup, that's it.

Connect your android device to your pc via USB, ensure you have adb setup, and run this in your terminal:
adb reverse tcp:8000 tcp:8000

Now, your mobile can access localhost:8000, just like your PC. (you can replace 8000 with whichever
 port you want to forward)



 */
public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.1.4:8080";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient();
           /* client.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Dlog.d("RetrofitInstance", "req body "+ chain.request().body().toString());

                    Response response=chain.proceed(chain.request());
                    return response;
                }
            });*/
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

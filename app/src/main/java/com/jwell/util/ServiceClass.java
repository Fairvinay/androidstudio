package com.jwell.util;

//import com.squareup.okhttp.Interceptor;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Response;

import java.io.IOException;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

public class ServiceClass {
    static ServiceInterface serviceInterface;
    //    public static final String baseUrl= HttpConstants.BASE_URL_GEONAME;
    public static final String baseUrl= HttpConstants.baseUrl;

    public static ServiceInterface connection(){
        if(serviceInterface==null){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient();
           /* client.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Response response=chain.proceed(chain.request());
                    return response;
                }
            });*/
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build();
            serviceInterface=retrofit.create(ServiceInterface.class);
        }
        return serviceInterface;
    }



}

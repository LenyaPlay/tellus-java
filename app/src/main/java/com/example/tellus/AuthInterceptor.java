package com.example.tellus;


import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class AuthInterceptor implements Interceptor {
    String token = null;

    public AuthInterceptor(String token){
        this.token = token;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request originalRequest = chain.request();

        if(token == null){
            Log.w("AuthInterceptor.java","Токен отсутствует");
            return chain.proceed(originalRequest);
        }

        Request authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(authorizedRequest);
    }
}

















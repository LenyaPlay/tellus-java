package com.example.tellus;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {

    private static OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();
    private static final String HOST = "http://192.168.56.204";

    private HttpHelper(){
        // Hide public constructor
    }

    public static void setHttpCliebt(OkHttpClient client){
        HttpHelper.CLIENT = client;
    }
    public static OkHttpClient getHttpCliebt(){
        return HttpHelper.CLIENT;
    }

    public static void post(String resource, Object object, Callback callback){
        RequestBody body = RequestBody.create(GSON.toJson(object),MediaType.parse("application/json"));
        request(resource, body, callback);
    }

    public static void post(String resource, byte[] data, Callback callback){
        RequestBody body = RequestBody.create(data, MediaType.parse("application/octet-stream"));
        request(resource, body, callback);
    }

    private static void request(String resource, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(HOST + resource)
                .post(body)
                .build();
        CLIENT.newCall(request).enqueue(callback);
    }

    public static void get(String resource, Callback callback) {
        Request request = new Request.Builder()
                .url(HOST + resource)
                .get()
                .build();
        CLIENT.newCall(request).enqueue(callback);
    }

    public static Response  executePost(String resource, byte[] bytes) throws IOException {
        RequestBody body = RequestBody.create(bytes,MediaType.parse("application/octet-stream"));

        Request request = new Request.Builder()
                .url(HOST + resource)
                .post(body)
                .build();
        return CLIENT.newCall(request).execute();
    }
    public static Response  executePost(String resource, Object object) throws IOException {
        RequestBody body = RequestBody.create(GSON.toJson(object),MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(HOST + resource)
                .post(body)
                .build();
        return CLIENT.newCall(request).execute();
    }
    public static Response executeGet(String resource) throws IOException {
        Request request = new Request.Builder()
                .url(HOST + resource)
                .get()
                .build();
        return CLIENT.newCall(request).execute();
    }
}
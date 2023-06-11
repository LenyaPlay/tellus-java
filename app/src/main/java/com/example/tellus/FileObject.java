package com.example.tellus;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Response;

public class FileObject {

    public static String Send(byte[] data) throws IOException {
        Response response = HttpHelper.executePost("/files", data);
        return response.body().string();
    }

    public static byte[] Load (String filepath) throws IOException {
        Response response = HttpHelper.executeGet("/files/" + filepath);
        return response.body().bytes();
    }
}

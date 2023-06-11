package com.example.tellus;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Response;

public class FileModel {
    public String _id;
    public String name;
    public long size;

    public byte[] file;
    public String filepath;

    public void Send() throws IOException {
        Response response = HttpHelper.executePost("/filemodels", this);
        _id = new Gson().fromJson(response.body().string(), this.getClass())._id;
    }

    public FileModel Load() throws IOException {
        Response response = HttpHelper.executeGet("/filemodels/" + _id);
        return new Gson().fromJson(response.body().string(), FileModel.class);
    }
}

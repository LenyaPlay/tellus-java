package com.example.tellus;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import okhttp3.Response;

public class Message {
    public String _id;
    public User user;
    public String text;
    public Date sentDate;


    public void Send() throws IOException {
        Response response = HttpHelper.executePost("/messages", this);
        _id = new Gson().fromJson(response.body().string(), this.getClass())._id;
    }

    public Message Load() throws IOException {
        Response response = HttpHelper.executeGet("/messages/" + _id);
        return new Gson().fromJson(response.body().string(), Message.class);
    }

}

package com.example.tellus;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class Group {
    public String _id;
    public String name;
    public List<User> users = new ArrayList<>();
    public List<Scene> scenes = new ArrayList<>();
    public List<Message> messages = new ArrayList<>();
    public List<FileModel> files = new ArrayList<>();
    public transient byte[] image;
    public String imagepath;
    public Message lastMessage;

    public void Send() throws IOException {
        Response response = HttpHelper.executePost("/groups", this);
        _id = new Gson().fromJson(response.body().string(), this.getClass())._id;
    }

    public Group Load() throws IOException {
        Response response = HttpHelper.executeGet("/groups/" + _id);
        return new Gson().fromJson(response.body().string(), Group.class);
    }
}



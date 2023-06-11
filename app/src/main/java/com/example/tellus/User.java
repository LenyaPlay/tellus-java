package com.example.tellus;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class User {
    public String _id;
    public String login;
    public String password;
    public String email;

    public transient byte[] image;
    public String imagepath;
    public List<Group> groups;

    public void Send() throws IOException {
        Response response = HttpHelper.executePost("/users", this);
        _id = new Gson().fromJson(response.body().string(), this.getClass())._id;
    }

    public User Load() throws IOException {
        Response response = HttpHelper.executeGet("/users/" + _id);
        return new Gson().fromJson(response.body().string(), User.class);
    }
}

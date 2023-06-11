package com.example.tellus;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class Scene {
    public String _id;
    String name;
    public List<SceneModel> models = new ArrayList<>();

    public void Send() throws IOException {
        Response response = HttpHelper.executePost("/scenes", this);
        _id = new Gson().fromJson(response.body().string(), this.getClass())._id;
    }

    public Scene Load() throws IOException {
        Response response = HttpHelper.executeGet("/scenes/" + _id);
        return new Gson().fromJson(response.body().string(), Scene.class);
    }
}

package com.example.tellus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

public class GroupScenesActivity extends AppCompatActivity {
    SceneAdapter sceneAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_scenes);


        RecyclerView recyclerView = findViewById(R.id.scenes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sceneAdapter = new SceneAdapter();
        sceneAdapter.setListener(scene -> {
            View3d.scene = scene;
            Intent intent = new Intent(this, View3d.class);
            startActivity(intent);
        });
        recyclerView.setAdapter(sceneAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sceneAdapter.setScenes(GroupActivity.group.scenes);
    }

    public void onCreateSceneClick(View view) {
        new Thread(() -> {
            try {
                Scene scene = new Scene();
                scene.Send();

                GroupActivity.group.scenes.add(scene);
                GroupActivity.group.Send();

                runOnUiThread(() -> sceneAdapter.setScenes(GroupActivity.group.scenes));

                Toaster.showToastOnUiThread(this, "Сцена создана");
            } catch (IOException e) {
                Toaster.showToastOnUiThread(this, "GroupSceneActivity.java 54 e:" + e.getMessage());
            }
        }).start();
    }
}
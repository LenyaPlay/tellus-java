package com.example.tellus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.ar.core.*;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.RequestBody;

public class View3d extends AppCompatActivity {

    private Gson gson;
    public static Scene scene;

    Dictionary<String, ModelRenderable> renderables = new Hashtable<>();
    Map<String, byte[]> models = new HashMap<>();
    String filepath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view3d);
        gson = new Gson();


    }

    @Override
    protected void onResume() {
        super.onResume();

        setupFileModelRecyclerView();
        workWith3d();
    }

    private void loadScene() {
        new Thread(() -> {
            try {
                scene =  scene.Load();
                for(SceneModel model : scene.models){
                    models.put(model.filepath, FileObject.Load(model.filepath));

                    runOnUiThread(() -> {
                        bytesToModel(model.filepath, models.get(model.filepath));
                        addModel(model);
                    });
                }
                Toaster.showToastOnUiThread(this, "Сцена загружена");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


    }

    public void addModel(SceneModel model) {
        ArFragment arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        Session session = arFragment.getArSceneView().getSession();

        Anchor anchor = session.createAnchor(model.pose);
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());


        // Create the transformable andy and add it to the anchor.
        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        andy.setParent(anchorNode);
        andy.setRenderable(renderables.get(model.filepath));
        andy.select();

        Toast.makeText(this, "Моделька добавлена", Toast.LENGTH_SHORT).show();
    }

    private void setupFileModelRecyclerView() {
        RecyclerView fileModelRecyclerView = findViewById(R.id.file_model_recycler_view);
        fileModelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FileFragmentAdapter adapter = new FileFragmentAdapter();

        adapter.setOnClickListener(this::onFileModelClick);
        fileModelRecyclerView.setAdapter(adapter);

        adapter.setItems(GroupActivity.group.files);

    }

    private void onFileModelClick(FileModel fileModel) {
        filepath = fileModel.filepath;

        new Thread(() -> {
            try {
                if (!models.containsKey(filepath)) {
                    Toaster.showToastOnUiThread(this, "Скачивание модели");
                    models.put(filepath, FileObject.Load(filepath));
                }

                Toaster.showToastOnUiThread(this, "Импорт модели");
                bytesToModel(filepath, models.get(filepath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();

    }

    private void bytesToModel(String _id, byte[] data) {
        // Create a temporary file in the app's cache directory
        File file = new File(getCacheDir(), "model.glb");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
        } catch (IOException e) {
            Toast.makeText(this, "View3d 161", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Create RenderableSource from the temporary file
        RenderableSource renderableSource = RenderableSource.builder()
                .setSource(this, Uri.fromFile(file), RenderableSource.SourceType.GLB)
                .build();


        ModelRenderable.builder()
                .setSource(this, renderableSource)
                .build()
                .thenAccept(modelRenderable -> {
                    // Model successfully loaded
                    renderables.put(_id, modelRenderable);
                    Toast.makeText(this, "Модель загружена", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(throwable -> {
                    // Error occurred while loading model
                    Toast.makeText(this, "Не удалось загрузить модель", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                    return null;
                });

    }

    private void workWith3d() {
        ArFragment arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        assert arFragment != null;


        arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
            if (filepath == null || renderables.get(filepath) == null) {
                return;
            }

            // Create the Anchor.
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());


            Pose pose = hitResult.getHitPose();

            Log.w("POSE", hitResult.getHitPose().getTranslation().toString());

            SceneModel sceneModel = new SceneModel();
            sceneModel.filepath = filepath;
            sceneModel.pose = pose;
            scene.models.add(sceneModel);

            Toast.makeText(this, gson.toJson(pose), Toast.LENGTH_SHORT).show();

            // Create the transformable andy and add it to the anchor.
            TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
            andy.setParent(anchorNode);
            andy.setRenderable(renderables.get(filepath));
            andy.select();
        });
    }

    public void onSaveClick(View view) {
        new Thread(() -> {
            try {
                scene.Send();
                Toaster.showToastOnUiThread(this, "Сцена сохранена на сервере");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void onLoadSceneClick(View view) {
        loadScene();
    }
}
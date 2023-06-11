package com.example.tellus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupCreateActivity extends AppCompatActivity {
    Gson gson = new Gson();
    UserSelectorAdapter userSelectorAdapter = new UserSelectorAdapter();
    private ActivityResultLauncher<String> imagePickerLauncher;
    byte[] image = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        RecyclerView rv = findViewById(R.id.user_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(userSelectorAdapter);

        getUsers();

        ImageView imageView = findViewById(R.id.group_image);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        try {
                            // Convert the selected image to a byte array
                            InputStream inputStream = getContentResolver().openInputStream(result);
                            image = getBytes(inputStream);
                            inputStream.close();

                            // Set the selected image to the ImageView
                            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                            imageView.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Set the click listener on the ImageView
        imageView.setOnClickListener(view -> {
            // Launch the image picker
            imagePickerLauncher.launch("image/*");
        });
    }

    public void getUsers() {
        HttpHelper.get("/users", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toaster.showToastOnUiThread(GroupCreateActivity.this, "Ошибка e: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Toaster.showToastOnUiThread(GroupCreateActivity.this, "Ошибка при создании группы res:" + response.body().string());
                    return;
                }

                User[] users = gson.fromJson(response.body().string(), User[].class);

                new Thread(() -> {

                    try {
                        for (User user : users)
                            if(user.imagepath != null)
                                user.image = FileObject.Load(user.imagepath);

                        runOnUiThread(() -> userSelectorAdapter.notifyDataSetChanged());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                runOnUiThread(() ->
                {
                    userSelectorAdapter.setUsers(Arrays.asList(users));
                });
            }
        });
    }

    public void onCreateClick(View view) {
        EditText name = findViewById(R.id.group_name);

        Group group = new Group();
        group.name = name.getText().toString();
        group.users = userSelectorAdapter.getSelectedUsers();

        new Thread(() -> {
            try {
                group.imagepath = FileObject.Send(image);
                group.Send();
                group.users = null;

                for (User user : userSelectorAdapter.getSelectedUsers()) {
                    if (user.groups == null)
                        user.groups = new ArrayList<>();

                    user.groups.add(group);
                    user.Send();
                }

                Toaster.showToastOnUiThread(this, "Новая группа создана");

                ProfileActivity.user = ProfileActivity.user.Load();

                finish();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }


    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}


















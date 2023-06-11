package com.example.tellus;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private ImageView imageView;

    public static User user;

    private byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        imageView = findViewById(R.id.imageView);


        if(user == null){
            Toast.makeText(this, "Для того, чтобы посмотреть профиль необходимо войти в аккаунт", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(user == null)
            return;

        if (user.login != null) {
            loginEditText.setText(user.login);
        }
        if (user.password != null) {
            passwordEditText.setText(user.password);
        }
        if (user.email != null) {
            emailEditText.setText(user.email);
        }

        if (user.imagepath != null) {
            new Thread(() -> {
                try {
                    if(user.image == null)
                        user.image = FileObject.Load(user.imagepath);
                    runOnUiThread(() -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(user.image, 0, user.image.length);
                        imageView.setImageBitmap(bitmap);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        }
    }

    public void onSaveClick(View view) {
        String newLogin = loginEditText.getText().toString();
        String newPassword = passwordEditText.getText().toString();
        String newEmail = emailEditText.getText().toString();


        user.login = newLogin;
        user.password = newPassword;
        user.email = newEmail;

        new Thread(() -> {
            try {
                if(image != null)
                    user.imagepath = FileObject.Send(image);

                user.Send();
                Toaster.showToastOnUiThread(ProfileActivity.this, "Данные успешно обновлены");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Load the image from the Uri into a Bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        // Set the Bitmap to the ImageView
                        imageView.setImageBitmap(bitmap);
                        // Convert the Bitmap to a byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        image = stream.toByteArray();
                    } catch (IOException e) {
                        Toaster.showToastOnUiThread(this, "e: " + e.getMessage());
                    }
                }
            });

    public void onImageViewClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}
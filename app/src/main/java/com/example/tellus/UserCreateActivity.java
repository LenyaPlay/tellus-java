package com.example.tellus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);
    }

    public void onCreateAccountClick(View view) {
        EditText login = findViewById(R.id.login_edit_text);
        EditText email = findViewById(R.id.email_edit_text);
        EditText password = findViewById(R.id.password_edit_text);

        User user = new User();
        user.login = login.getText().toString();
        user.email = email.getText().toString();
        user.password = password.getText().toString();

        new Thread(() -> {
            try {
                user.Send();
                Toaster.showToastOnUiThread(UserCreateActivity.this, "Аккаунт успешно создан");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
















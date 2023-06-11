package com.example.tellus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void onSignInBntClick(View view) {
        User user = new User();
        user.login = ((EditText) findViewById(R.id.loginEditText)).getText().toString();
        user.password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

        OkHttpClient lastClient = HttpHelper.getHttpCliebt();
        HttpHelper.setHttpCliebt(new OkHttpClient.Builder().build());

        HttpHelper.post("/signin", user, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toaster.showToastOnUiThread(SignInActivity.this, "Ошибка SignInActivity.java 44");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    HttpHelper.setHttpCliebt(lastClient);
                    Toaster.showToastOnUiThread(SignInActivity.this, "Проблемы с запросом res: " + response.body().string());
                    return;
                }
                String json = response.body().string();
                JWTModel jwtModel = gson.fromJson(json, JWTModel.class);

                AuthInterceptor interceptor = new AuthInterceptor(jwtModel.token);
                OkHttpClient newClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                HttpHelper.setHttpCliebt(newClient);

                Toaster.showToastOnUiThread(SignInActivity.this, "Вы успешно авторизовались");

                ProfileActivity.user = jwtModel.user;
                finish();
            }
        });

        ProfileActivity.user = user;
    }

}




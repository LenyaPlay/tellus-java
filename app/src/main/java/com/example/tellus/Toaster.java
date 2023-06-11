package com.example.tellus;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class Toaster {
    public static void showToastOnUiThread(final Context context, final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

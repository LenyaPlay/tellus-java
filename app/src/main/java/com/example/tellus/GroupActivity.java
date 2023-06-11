package com.example.tellus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

public class GroupActivity extends AppCompatActivity {
    public static Group group = null;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        RecyclerView recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        if (group == null) {
            finish();
            return;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        new Thread(() -> {
            try {
                ProfileActivity.user = ProfileActivity.user.Load();
                group = group.Load();

                runOnUiThread(() -> messageAdapter.setMessages(group.messages));

                for(Message msg : group.messages){
                    User user = msg.user;
                    if(user.imagepath != null && user.image == null)
                        user.image = FileObject.Load(user.imagepath);
                    msg.user = user;
                }

                runOnUiThread(() -> messageAdapter.notifyDataSetChanged());

            } catch (IOException e) {
                Toaster.showToastOnUiThread(this, "GroupActivity.java 69 e:" +e.getMessage());
            }
        }).start();
    }

    public void onSendClick(View view) {
        String text = ((EditText) findViewById(R.id.message_edit_text)).getText().toString();

        Message message = new Message();
        message.text = text;

        new Thread(() -> {
            try {
                message.user = ProfileActivity.user;

                message.user.groups = null;
                for(User user : group.users)
                    user.groups = null;

                message.Send();
                group.messages.add(message);
                group.Send();
                runOnUiThread(() -> messageAdapter.setMessages(group.messages));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void onFilesClick(View view) {
        startActivity(new Intent(this, GroupFilesActivity.class));
    }

    public void onScenesClick(View view) {
        startActivity(new Intent(this, GroupScenesActivity.class));
    }
}
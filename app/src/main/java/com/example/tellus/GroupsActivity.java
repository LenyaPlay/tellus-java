package com.example.tellus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Request;

public class GroupsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GroupsAdapter groupAdapter;
    private List<Group> groups;

    View createGroupButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        createGroupButton = findViewById(R.id.createGroupButton);

        recyclerView = findViewById(R.id.groups_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupAdapter = new GroupsAdapter();
        groupAdapter.setOnClickListener(group -> {
            GroupActivity.group = group;

            startActivity(new Intent(this, GroupActivity.class));
        });
        recyclerView.setAdapter(groupAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ProfileActivity.user != null)
            if(ProfileActivity.user.login.equals("admin"))
                createGroupButton.setVisibility(View.VISIBLE);
            else
                createGroupButton.setVisibility(View.INVISIBLE);

        if (ProfileActivity.user != null && ProfileActivity.user.groups != null)
            loadGroups();
    }

    public void loadGroups() {
        if (ProfileActivity.user.groups != null && !ProfileActivity.user.groups.isEmpty()) {
            findViewById(R.id.groups_not_found_view).setVisibility(View.INVISIBLE);
            groupAdapter.setGroups(ProfileActivity.user.groups);

            new Thread(() -> {
                try {
                    groups = ProfileActivity.user.groups;

                    for (Group group : groups)
                        if (group.imagepath != null && group.image == null)
                            group.image = FileObject.Load(group.imagepath);

                    runOnUiThread(() -> groupAdapter.notifyDataSetChanged());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }


    public void onCreateGroupClick(View view) {
        try {
            Intent intent = new Intent(this, GroupCreateActivity.class);

            startActivity(intent);
        } catch (Exception e) {
            String s = e.getMessage();
            Toast.makeText(this, e.getMessage(), 0).show();

        }

    }
}
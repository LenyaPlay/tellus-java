package com.example.tellus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    protected List<User> userList = new ArrayList<>();

    public UserAdapter() {

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_view, parent, false);
        return new UserViewHolder(view);
    }


    public void setUsers(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }
    public void addUser(User user){
        userList.add(user);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userLogin.setText(user.login);
        if (user.image != null) {
            holder.userImage.setImageBitmap(BitmapFactory.decodeByteArray(user.image, 0, user.image.length));
        } else {
            holder.userImage.setImageResource(R.drawable.default_group_image);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView userLogin;

        public UserViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userLogin = itemView.findViewById(R.id.user_login);
        }
    }
}
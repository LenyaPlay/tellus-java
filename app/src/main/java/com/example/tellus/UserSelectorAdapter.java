package com.example.tellus;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UserSelectorAdapter extends UserAdapter {

    private List<User> selectedUsers = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(User user);
    }


    private OnItemClickListener listener;

    public void setOnClick(OnItemClickListener listener) {
        this.listener = listener;
    }


    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);


        holder.itemView.setBackgroundColor(Color.WHITE);

        holder.itemView.setOnClickListener(v -> {
            User user = userList.get(position);

            if (selectedUsers.contains(user)) {
                selectedUsers.remove(user);
                holder.itemView.setBackgroundColor(Color.WHITE);
            } else {
                selectedUsers.add(user);
                holder.itemView.setBackgroundColor(Color.rgb(240, 240, 240));
            }

            if (listener != null)
                listener.onItemClick(userList.get(position));
        });
    }
}

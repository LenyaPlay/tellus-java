package com.example.tellus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages = new ArrayList<>();

    public MessageAdapter() {

    }

    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item_view, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textTextView.setText(message.text);

        User user = message.user;

        if(user != null && user.image != null && user.image.length != 0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.image, 0, user.image.length);
            holder.userImageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView userImageView;
        TextView textTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.user_image_view);
            textTextView = itemView.findViewById(R.id.text_text_view);
        }
    }
}
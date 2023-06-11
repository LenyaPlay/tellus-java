package com.example.tellus;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {
    private List<Group> groups = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Group group);
    }

    OnItemClickListener listener;
    public GroupsAdapter() {

    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_view, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupName.setText(group.name);

        if(group.lastMessage != null)
            holder.groupLastMessage.setText(group.lastMessage.text);

        if(listener != null)
            holder.itemView.setOnClickListener(v -> listener.onItemClick(group));

        if (group.image != null && group.image.length > 0) {
            holder.groupImage.setImageBitmap(BitmapFactory.decodeByteArray(group.image, 0, group.image.length));
        } else {
            holder.groupImage.setImageResource(R.drawable.default_group_image);
        }
    }


    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public Group getGroup(int index){
        return groups.get(index);
    }
    public List<Group> getGroups(){
        return groups;
    }

    public void setGroups(List<Group> groups){
        this.groups = groups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        ImageView groupImage;
        TextView groupName;
        TextView groupLastMessage;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.group_image);
            groupName = itemView.findViewById(R.id.group_name);
            groupLastMessage = itemView.findViewById(R.id.group_last_message);
        }
    }
}

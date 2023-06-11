package com.example.tellus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tellus.R;
import com.example.tellus.Scene;

import java.util.List;

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneViewHolder> {

    private List<Scene> sceneList;


    public interface OnItemClickListener {
        void onItemClick(Scene scene);
    }

    OnItemClickListener listener;

    public SceneAdapter(){

    }

    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setScenes( List<Scene> sceneList) {
        this.sceneList = sceneList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_item_view, parent, false);
        return new SceneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SceneViewHolder holder, int position) {
        Scene scene = sceneList.get(position);
        holder.nameTextView.setText(scene._id);

        if(listener != null)
            holder.itemView.setOnClickListener(v -> listener.onItemClick(scene));
    }

    @Override
    public int getItemCount() {
        return sceneList.size();
    }

    public static class SceneViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;

        public SceneViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }

    }
}
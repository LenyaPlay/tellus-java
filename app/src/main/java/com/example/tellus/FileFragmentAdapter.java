package com.example.tellus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileFragmentAdapter extends RecyclerView.Adapter<FileFragmentAdapter.FileViewHolder> {

    private List<FileModel> fileModelList = new ArrayList<>();

    public FileFragmentAdapter() {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(FileModel fileModel);
    }

    private OnItemClickListener listener;

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_file, parent, false);
        return new FileViewHolder(view);
    }

    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void addItem(FileModel fileModel){
        fileModelList.add(fileModel);
        notifyDataSetChanged();
    }

    public void setItems(List<FileModel> fileModelCollection) {
        fileModelList = fileModelCollection;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.bind(fileModelList.get(position));
        holder.itemView.setOnClickListener(v -> {
            if(listener != null)
                listener.onItemClick(fileModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return fileModelList.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private TextView fileSize;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileNameTextView);
            fileSize = itemView.findViewById(R.id.fileSizeTextView);
        }

        public void bind(FileModel fileModel){
            fileName.setText(fileModel.name);
            fileSize.setText(getFileSize(fileModel.size));
        }

        public String getFileSize(long filesize) {
            long bytes = filesize;
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            double gigabytes = (megabytes / 1024);

            if (gigabytes > 1) {
                return String.format("%.2f GB (%d bytes)", gigabytes, bytes);
            } else if (megabytes > 1) {
                return String.format("%.2f MB (%d bytes)", megabytes, bytes);
            } else if (kilobytes > 1) {
                return String.format("%.2f KB (%d bytes)", kilobytes, bytes);
            } else {
                return String.format("%d bytes", bytes);
            }
        }

    }
}

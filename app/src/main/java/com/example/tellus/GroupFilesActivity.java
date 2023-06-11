package com.example.tellus;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupFilesActivity extends AppCompatActivity {

    Gson gson = new Gson();
    RecyclerView filesRecyclerView;
    FileFragmentAdapter filesAdapter = new FileFragmentAdapter();

    private ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if(uri != null)
                    sendFile(uri);
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_files);

        filesRecyclerView = findViewById(R.id.files_recycler_view);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filesRecyclerView.setAdapter(filesAdapter);

        filesAdapter.setOnClickListener(this::onFileClick);

        if (GroupActivity.group == null) {
            finish();
            return;
        }
    }

    public void onFileClick(FileModel fm) {
        downloadFile(fm);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadFiles();
    }

    public void downloadFile(FileModel fm) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        new Thread(() -> {
            try {
                Toaster.showToastOnUiThread(this, "Скачивание файла");
                String filepath = getUniqueFileInDownloads(fm.name);
                FileOutputStream stream = new FileOutputStream(filepath);
                if(fm.file == null)
                    fm.file = FileObject.Load(fm.filepath);
                stream.write(fm.file);
                stream.close();
                Toaster.showToastOnUiThread(this, "Файл успешно сохранён. " + filepath);
            } catch (IOException e) {
                Toaster.showToastOnUiThread(this, "Не удалось загрузить файл. " + e.getMessage());
            }

        }).start();

    }

    public void loadFiles() {
        filesAdapter.setItems(GroupActivity.group.files);
    }

    public void onUploadClick(View view) {
        filePickerLauncher.launch("*/*");
    }

    private void sendFile(Uri uri) {
        final byte[] file;

        try {
            file = getBytesFromUri(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (OutOfMemoryError e) {
            Toast.makeText(this, "Выберите файл меньше 100 мб", Toast.LENGTH_SHORT).show();
            return;
        }


        FileModel fm = new FileModel();
        fm.name = getFilenameFromUri(uri);
        fm.size = file.length;

        new Thread(() -> {
            try {
                fm.filepath = FileObject.Send(getBytesFromUri(uri));
                fm.Send();


                GroupActivity.group.files.add(fm);
                GroupActivity.group.image = null;
                GroupActivity.group.Send();

                Toaster.showToastOnUiThread(this, "Файл загружен");
                finish();
            } catch (IOException e) {
                Toaster.showToastOnUiThread(this, "GroupFilesActivity.java 171" + e.getMessage());
            }


        }).start();
    }


    private String getUniqueFileInDownloads(String fileName) {
        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File tellusFolder = new File(downloadsFolder, "Tellus");
        if (!tellusFolder.exists()) {
            tellusFolder.mkdirs();
        }
        File file = new File(tellusFolder, fileName);

        if (file.exists()) {
            int number = 1;
            String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            String ext = fileName.substring(fileName.lastIndexOf('.'));
            while (file.exists()) {
                fileName = nameWithoutExt + " (" + number + ")" + ext;
                file = new File(tellusFolder, fileName);
                number++;
            }
        }
        return file.getAbsolutePath();
    }


    private byte[] getBytesFromUri(Uri uri) throws IOException, OutOfMemoryError {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);

        }
        byte[] bytes = outputStream.toByteArray();
        inputStream.close();
        outputStream.close();
        return bytes;
    }

    private String getFilenameFromUri(Uri uri) {
        String filename = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            filename = cursor.getString(column_index);
            cursor.close();
        }
        return filename;
    }

}

















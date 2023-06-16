package com.example.sharemusic;

import static android.content.ContentValues.TAG;
import static android.os.Environment.DIRECTORY_MUSIC;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private final ArrayList<Song> songs = new ArrayList<>();

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted, so proceed with accessing the external storage
            accessExternalStorage();
        }

        ArrayAdapter<Song> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);

        // Set a click listener for the ListView items to play the songs
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }// Assume that you have a music file "music.mp3" stored in the device's Downloads folder

            String songPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath() + "/shortsong.mp3";
            MediaPlayer mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "Error playing music file", e);
            }

        }
        );
    }

    private void accessExternalStorage() {
        ArraySet<Song> songsList = new ArraySet<>();

        String path = Environment.getExternalStorageDirectory().toString() + "/Music/";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                String filePath = file.getAbsolutePath();
                String songName = file.getName();
                String artistName = "Unknown";
                // Create a new Song object with the retrieved information
                Song song = new Song(filePath, songName, artistName);
                // Add the song to the ArrayList
                songsList.add(song);
            }
            // Add all the songs from the ArraySet to the ArrayList
            songs.addAll(songsList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


package com.ganesh.mymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView songsText,noMusicFound;
    RecyclerView songsListHolder;
    ArrayList<AudioModel> songsLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initializing the views in mainActivity.java by calling their id's
        songsText = findViewById(R.id.titleSongs);
        songsListHolder = findViewById(R.id.songsList);
        noMusicFound = findViewById(R.id.noSongFound);

        // permission checker
        if(checkPermission()== false){ requestPermission(); return; }

        // Creating string array to store Title, Path, Duration of the song....
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION

        }; String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        // Creating cursor to access database....
        // Using getContentResolver.query we have converted the data in form of list by passing projection and selection
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);

        //Loading all the songs to our ArrayList (songLists)
        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if (new File(songData.getPath()).exists()){ songsLists.add(songData);} //if user deletes the song then it won't get added to the list.
        }
        if(songsLists.size()==0){
            noMusicFound.setVisibility(View.VISIBLE);
        }
        else{
            songsListHolder.setLayoutManager(new LinearLayoutManager(this));
            songsListHolder.setAdapter(new SongListAdapter(songsLists,getApplicationContext()));
        }
    }

    // to check the storage access permission
    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){ return true;}
        else { return false;} }

    // to request the permission if not given
    void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this, "Permission Required! Change the settings", Toast.LENGTH_SHORT).show();
        }else {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
    }}
    @Override
    protected void onResume() {
        super.onResume();
        if(songsListHolder!=null){
            songsListHolder.setAdapter(new SongListAdapter(songsLists,getApplicationContext()));
        }
    }

}
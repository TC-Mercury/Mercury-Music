package com.mercury.mercurymusic;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    RelativeLayout miniPlayerLayout;
    TextView miniPlayerTitle;
    ImageView miniPlayerPlayPause, miniPlayerNext, miniPlayerPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_music_text);
        miniPlayerLayout = findViewById(R.id.mini_player);
        miniPlayerTitle = findViewById(R.id.mini_player_title);
        miniPlayerPlayPause = findViewById(R.id.mini_player_play_pause);
        miniPlayerNext = findViewById(R.id.mini_player_next);
        miniPlayerPrev = findViewById(R.id.mini_player_prev);

        miniPlayerLayout.setOnClickListener(v -> {
            if(MyMediaPlayer.currentIndex != -1) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("LIST", songsList);
                intent.putExtra("POSITION", MyMediaPlayer.currentIndex);
                intent.putExtra("fromMiniPlayer", true);
                startActivity(intent);
            }
        });

        miniPlayerPlayPause.setOnClickListener(v -> {
            if(MyMediaPlayer.getInstance().isPlaying()){
                MyMediaPlayer.getInstance().pause();
                miniPlayerPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }else{
                MyMediaPlayer.getInstance().start();
                miniPlayerPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        miniPlayerNext.setOnClickListener(v -> {
            if(MyMediaPlayer.currentIndex == songsList.size() - 1) return;
            MyMediaPlayer.currentIndex += 1;
            playMusicFromMini();
        });

        miniPlayerPrev.setOnClickListener(v -> {
            if(MyMediaPlayer.currentIndex == 0) return;
            MyMediaPlayer.currentIndex -= 1;
            playMusicFromMini();
        });

        if(checkPermission() == false){
            requestPermission();
            return;
        }
        loadSongs();
    }

    void playMusicFromMini(){
        try {
            AudioModel currentSong = songsList.get(MyMediaPlayer.currentIndex);
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.getInstance().setDataSource(currentSong.getPath());
            MyMediaPlayer.getInstance().prepare();
            MyMediaPlayer.getInstance().start();

            miniPlayerTitle.setText(currentSong.getTitle());
            miniPlayerPlayPause.setImageResource(android.R.drawable.ic_media_pause);

            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(currentSong.getAlbum_ID()));
            Glide.with(this)
                    .load(albumArtUri)
                    .apply(new RequestOptions().placeholder(android.R.drawable.ic_menu_agenda).error(android.R.drawable.ic_menu_agenda))
                    .into((ImageView) findViewById(R.id.mini_player_icon));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null)
            recyclerView.setAdapter(new AudioAdapter(songsList, MainActivity.this));

        if(MyMediaPlayer.currentIndex != -1){
            miniPlayerLayout.setVisibility(View.VISIBLE);
            miniPlayerTitle.setText(songsList.get(MyMediaPlayer.currentIndex).getTitle());

            AudioModel modelSong = songsList.get(MyMediaPlayer.currentIndex);
            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(modelSong.getAlbum_ID()));

            Glide.with(this)
                    .load(albumArtUri)
                    .apply(new RequestOptions().placeholder(android.R.drawable.ic_media_play).error(android.R.drawable.ic_media_play))
                    .into((ImageView) findViewById(R.id.mini_player_icon));

            if(MyMediaPlayer.getInstance().isPlaying()){
                miniPlayerPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            }else{
                miniPlayerPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }
        } else {
            miniPlayerLayout.setVisibility(View.GONE);
        }
    }

    void loadSongs(){
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);

        while(cursor.moveToNext()) {
            AudioModel musicData = new AudioModel(
                    cursor.getString(1),
                    cursor.getString(0),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );
            songsList.add(musicData);
        }

        if(songsList.size() == 0){
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new AudioAdapter(songsList, MainActivity.this));
        }
    }

    boolean checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        else
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    void requestPermission(){
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 123);
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==123){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                loadSongs();
        }
    }
}
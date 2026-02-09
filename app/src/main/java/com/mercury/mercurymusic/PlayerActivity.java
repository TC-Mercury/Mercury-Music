package com.mercury.mercurymusic;

import android.content.ContentUris;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    TextView titleTv, artistTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlayBtn, nextBtn, previousBtn, musicIcon, backBtn;
    CardView imageCard;

    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        titleTv = findViewById(R.id.song_title);
        artistTv = findViewById(R.id.song_artist);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlayBtn = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        backBtn = findViewById(R.id.back_btn);
        imageCard = findViewById(R.id.image_card);
        titleTv.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        x = getIntent().getIntExtra("POSITION", 0);

        backBtn.setOnClickListener(v -> finish());

        setResourcesWithMusic();

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if(mediaPlayer.isPlaying()){
                        pausePlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                        imageCard.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
                    }else{
                        pausePlayBtn.setImageResource(android.R.drawable.ic_media_play);
                        imageCard.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadAlbumArt(String albumId){
        Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(albumId));
        Glide.with(this)
                .load(albumArtUri)
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_foreground).error(android.R.drawable.ic_media_play))
                .into(musicIcon);
    }
    void setResourcesWithMusic(){
        currentSong = songsList.get(x);

        titleTv.setText(currentSong.getTitle());
        artistTv.setText(currentSong.getArtist());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));
        loadAlbumArt(currentSong.getAlbum_ID());

        pausePlayBtn.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v-> playNextSong());
        previousBtn.setOnClickListener(v-> playPreviousSong());

        Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(currentSong.getAlbum_ID()));
        Glide.with(this)
                .load(albumArtUri)
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_foreground))
                .into(musicIcon);

        boolean fromMiniPlayer = getIntent().getBooleanExtra("fromMiniPlayer", false);

        if(fromMiniPlayer) {
            if(mediaPlayer!=null) {
                seekBar.setMax(mediaPlayer.getDuration());
            }
            if(mediaPlayer.isPlaying()){
                pausePlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                imageCard.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
            }
        }
        else if(mediaPlayer.isPlaying()) {
            if(x == MyMediaPlayer.currentIndex) {
                if(mediaPlayer!=null) {
                    seekBar.setMax(mediaPlayer.getDuration());
                }
                pausePlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                imageCard.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
            }
            else {
                playMusic();
            }
        }
        else {
            playMusic();
        }
    }

    void updateUIOnly(){
        if(mediaPlayer!=null) {
            seekBar.setMax(mediaPlayer.getDuration());

            if(mediaPlayer.isPlaying()){
                pausePlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                imageCard.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
            } else {
                pausePlayBtn.setImageResource(android.R.drawable.ic_media_play);
                imageCard.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
            }
        }
    }

    void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            MyMediaPlayer.currentIndex = x;

            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnCompletionListener(mp -> playNextSong());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void playNextSong(){
        if(x == songsList.size() -1) return;

        x += 1;
        MyMediaPlayer.currentIndex = x;
        currentSong = songsList.get(x);
        playMusic();

        titleTv.setText(currentSong.getTitle());
        artistTv.setText(currentSong.getArtist());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));
        loadAlbumArt(currentSong.getAlbum_ID());

        pausePlayBtn.setImageResource(android.R.drawable.ic_media_pause);
        imageCard.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
    }

    void playPreviousSong(){
        if(x == 0) return;
        x -= 1;
        MyMediaPlayer.currentIndex = x;
        currentSong = songsList.get(x);
        playMusic();

        titleTv.setText(currentSong.getTitle());
        artistTv.setText(currentSong.getArtist());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));
        loadAlbumArt(currentSong.getAlbum_ID());

        pausePlayBtn.setImageResource(android.R.drawable.ic_media_pause);
        imageCard.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
    }

    void pausePlay(){
        if(mediaPlayer.isPlaying()) mediaPlayer.pause();
        else mediaPlayer.start();
    }

    public static String convertToMMSS(String duration){
        try {
            Long millis = Long.parseLong(duration);
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        } catch (Exception e) {
            return "00:00";
        }
    }
}
package com.mercury.mercurymusic;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    ArrayList<AudioModel> music_list;
    Context context;

    public AudioAdapter(ArrayList<AudioModel> music_list,Context context){
        this.music_list = music_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioModel model = music_list.get(position);
        holder.title_text_view.setText(model.getTitle());
        holder.artist_text_view.setText(model.getArtist());

        Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(model.getAlbum_ID()));
        Glide.with(context)
                .load(albumArtUri)
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_foreground))
                .into(holder.album_image_view);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("LIST", music_list);
            intent.putExtra("POSITION", position);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return music_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title_text_view, artist_text_view;
        ImageView album_image_view;

        public ViewHolder(View itemView) {
            super(itemView);
            title_text_view = itemView.findViewById(R.id.music_title_text);
            artist_text_view = itemView.findViewById(R.id.artist_text);
            album_image_view = itemView.findViewById(R.id.icon_view);
        }
    }
}
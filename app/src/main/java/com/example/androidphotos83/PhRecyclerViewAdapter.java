package com.example.androidphotos83;
import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhRecyclerViewAdapter extends RecyclerView.Adapter<PhRecyclerViewAdapter.ViewHolder> {

    private List<String> strings;
    private List<Uri> uris;
    private LayoutInflater layoutInflater;
    private ItemClickListener clickListener;
    private Boolean isSearching;

    // data is passed into the constructor
    PhRecyclerViewAdapter(Context context, List<String> str, List<Uri> uris, boolean isSearching) {
        this.layoutInflater = LayoutInflater.from(context);
        this.strings = str;
        this.uris = uris;
        this.isSearching = isSearching;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.albumview_recycler_row, parent, false);
        return new ViewHolder(view, isSearching);
    }

    // binds the correct data to each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        holder.photoCaption.setText(strings.get(pos));
        holder.photoView.setImageURI(null);
        holder.photoView.setImageURI(uris.get(pos));

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return strings.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView photoCaption;
        ImageView photoView;
        Button del;
        Button mov;

        ViewHolder(View itemView, boolean isSearching) {
            super(itemView);
            photoCaption = itemView.findViewById(R.id.photoCaption);
            photoView = itemView.findViewById(R.id.imageView);
            del = itemView.findViewById(R.id.deletePhoto);
            mov = itemView.findViewById(R.id.movePhoto);
            photoView.setOnClickListener(this);

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) clickListener.onDelClick(view, getAdapterPosition());
                }
            });

            mov.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) clickListener.onMovClick(view, getAdapterPosition());
                }
            });

            if(isSearching){
                del.setVisibility(View.GONE);
                mov.setVisibility(View.GONE);
            }
            else{
                del.setVisibility(View.VISIBLE);
                mov.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

    }

    String getItem(int index) {
        return strings.get(index);
    }

    Uri getUri(int index){ return uris.get(index);}

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onDelClick(View view, int position);
        void onMovClick(View view, int position);
    }
}
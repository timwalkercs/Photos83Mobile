package com.example.androidphotos83;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import Model.Photo;
import Model.Album;
import Model.User;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class SlideshowView extends AppCompatActivity {
    private Photo currentPhoto;
    private Album currentAlb;
    private User user;
    private Button next;
    private Button prev;
    private TextView slideshowCaption;

    final Context context = this;

    private int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow_view);

        //Establish the data needed
        user=AlbumList.user;
        currentAlb = user.getCrntAlbum();
        setTitle(user.getCrntAlbum().getAlbumName());
        currentPhoto = currentAlb.getPhotos().get(index);

        //Set up ImageView
        ImageView displayImage = findViewById(R.id.displayImage);
        displayImage.setImageURI(Uri.parse(currentPhoto.getImagePath()));

        //Set up TextView
        slideshowCaption = findViewById(R.id.slideshowCaption);
        slideshowCaption.setText(currentPhoto.getFileName());

        //Prev Photo
        prev = (Button) findViewById(R.id.lastPhoto);
        prev.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view) {
                index --;
                if(index < 0) { //loop around photolist
                    index = currentAlb.getPhotoCount()-1;
                }
                currentPhoto = currentAlb.getPhotos().get(index);
                displayImage.setImageURI(Uri.parse(currentPhoto.getImagePath()));
                slideshowCaption.setText(currentPhoto.getFileName());
            }
        });

        //Next Photo
        next = (Button) findViewById(R.id.nextPhoto);
        next.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view) {
                index ++;
                if(index == currentAlb.getPhotoCount()) { //loop around photolist
                    index = 0;
                }
                currentPhoto = currentAlb.getPhotos().get(index);
                displayImage.setImageURI(Uri.parse(currentPhoto.getImagePath()));
                slideshowCaption.setText(currentPhoto.getFileName());
            }
        });

    }

}
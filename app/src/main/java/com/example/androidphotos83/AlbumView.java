package com.example.androidphotos83;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import Model.Album;
import Model.Photo;
import Model.User;

public class AlbumView extends AppCompatActivity implements PhRecyclerViewAdapter.ItemClickListener {
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private boolean isSearching;

    //Lists of Captions & Uris
    private ArrayList<String> captionList = new ArrayList<>();
    private ArrayList<Uri> uriList = new ArrayList<>();

    private FloatingActionButton addPhoto;
    private FloatingActionButton slideshow;
    private RecyclerView recyclerView;
    private PhRecyclerViewAdapter recyclerViewAdapter;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view);
        isSearching = getIntent().getExtras().getBoolean("are searching?");
        System.out.println("OPENING ALBUM: " + AlbumList.user.getCrntAlbum().getAlbumName());
        setTitle(AlbumList.user.getCrntAlbum().getAlbumName());

        //Populate Lists
        for(int i=0; i<AlbumList.user.getCrntAlbum().getPhotoCount(); i++){
            //Add Each Photo's File Name to Caption List
            captionList.add(AlbumList.user.getCrntAlbum().getPhotos().get(i).getFileName());

            //Add Uris to their list
            Uri toAdd = Uri.parse(AlbumList.user.getCrntAlbum().getPhotos().get(i).getImagePath());
            uriList.add(toAdd);

        }

        //set up Recycler View & Adapter
        //PhRecyclerViewAdapter recyclerViewAdapter;
        recyclerView = findViewById(R.id.photoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new PhRecyclerViewAdapter(this, captionList, uriList, isSearching);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        //recyclerView.getAdapter().

        //Add Photo Button
        addPhoto = (FloatingActionButton) findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.out.println("ADD PHOTO CLICKED");

                //FileChooser
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);


                startActivityForResult(Intent.createChooser(intent,"Pick an Image"), PICK_IMAGE);
            }
        });

        slideshow = (FloatingActionButton) findViewById(R.id.slideshow);
        slideshow.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(AlbumList.user.getCrntAlbum().getPhotoCount() == 0){
                    Toast.makeText(getApplicationContext(), "No photos to show!", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    showSlideShow(AlbumList.user.getCrntAlbum());
                }
            }
        });

        tagSearchView();
    }

    public void tagSearchView(){

        if(isSearching) {
            setTitle("Search Results");
            addPhoto.setVisibility(View.GONE);
            slideshow.setVisibility(View.GONE);
        }
        else {
            addPhoto.setVisibility(View.VISIBLE);
            slideshow.setVisibility(View.VISIBLE);
        }
    }

    //When Recycler View Item Clicked...
    @Override
    public void onItemClick(View view, int pos) {
        System.out.println("Show Photo #" + pos);
        showPhoto(pos);
    }

    @Override
    public void onDelClick(View view, int pos) {
        System.out.println("Del Photo #" + pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete Photo?");
        //Confirm Deletion
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePhoto(pos);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onMovClick(View view, int pos) {
        System.out.println("Mov Photo #" + pos);

        ArrayList<String> albumNames = getIntent().getExtras().getStringArrayList("list of album names");
        if(albumNames.size()<=1) {
            Toast.makeText(getApplicationContext(), "Not enough albums!", Toast.LENGTH_LONG).show();
            return;
        }
        albumNames.remove(getIntent().getExtras().getInt("crnt album pos")); //remove current album from list

        Photo photo = AlbumList.user.getCrntAlbum().getPhotos().get(pos);

        final Spinner dropdown = (Spinner) findViewById(R.id.dropdown);
        ViewGroup oldparent = ((ViewGroup)dropdown.getParent());
        oldparent.removeView(dropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.album_move_dropdown_text, R.id.text, albumNames);
        dropdown.setVisibility(View.VISIBLE);
        dropdown.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Where do you want to move \"" + photo.getFileName() + "\"?");
        builder.setView(dropdown);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                deletePhoto(pos);
                String albumName = dropdown.getSelectedItem().toString();

                for (int i=0; i < AlbumList.user.getAlbums().size(); i++){
                    if(AlbumList.user.getAlbums().get(i).getAlbumName().equals(albumName)){
                        AlbumList.user.getAlbums().get(i).addPhoto(photo);
                        try {
                            User.save(AlbumList.user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                System.out.println("dismissed");
                prepareBuilderClose(dropdown, oldparent);
            }
        });

        builder.show();
    }

    public void prepareBuilderClose(Spinner dropdown, ViewGroup oldparent){
        ((ViewGroup)dropdown.getParent()).removeView(dropdown);
        dropdown.setVisibility(View.GONE);
        oldparent.addView(dropdown);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedUri = data.getData();
                imageUri = selectedUri;
                addPhoto(imageUri);
            }
        }
    }

    @SuppressLint("Range")
    public String nameFromUri(Uri uri) {
        String name = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (name == null) {
            name = uri.getPath();
            int cut = name.lastIndexOf('/');
            if (cut != -1) {
                name = name.substring(cut + 1);
            }
        }
        return name;
    }

    public void addPhoto(Uri uri){
        Photo newPhoto = new Photo();


        String fileName = nameFromUri(imageUri);
        newPhoto.setFileName(fileName);

        String path = imageUri.toString();

        //newPhoto.setImageUri(imageUri);
        newPhoto.setImagePath(path);

        AlbumList.user.getCrntAlbum().addPhoto(newPhoto);
        captionList.add(newPhoto.getFileName());

        uriList.add(imageUri);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);

        try {
            User.save(AlbumList.user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePhoto(int pos){

        AlbumList.user.getCrntAlbum().getPhotos().remove(pos);
        captionList.remove(pos);
        uriList.remove(pos);

        try {
            User.save(AlbumList.user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);
    }



    private void showPhoto(int index) {
        //intent
        Intent intent = new Intent(this, PhotoView.class);

        //bundle
        intent.putExtra("index",index);
        intent.putExtra("are searching?", isSearching);

        //bundle+intent
        startActivity(intent);
    }

    private void showSlideShow(Album crnt) {
        //intent
        Intent intent = new Intent(this, SlideshowView.class);

        //bundle+intent
        startActivity(intent);
    }

}
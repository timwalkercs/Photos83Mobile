package com.example.androidphotos83;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;

import Model.Photo;
import Model.User;

public class PhotoView extends AppCompatActivity {
    private Photo photo;
    private User user;
    private Button addLocation;
    private Button addPerson;
    private ArrayAdapter<String> locAdapter;
    private ArrayAdapter<String> persAdapter;
    private boolean isSearching;
    final Context context = this;

    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Photo Details");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);

        user=AlbumList.user;

        int index = getIntent().getExtras().getInt("index");
        photo = user.getCrntAlbum().getPhotos().get(index);
        isSearching=getIntent().getExtras().getBoolean("are searching?");

        ImageView myImage = findViewById(R.id.imageView);
        myImage.setImageURI(Uri.parse(photo.getImagePath()));

        TextView captionView = findViewById(R.id.textView);
        captionView.setText(photo.getFileName());

        setTagButtonAdapters();

        addLocation = (Button) findViewById(R.id.addLocationButton);
        addLocation.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view) { addTags(false); }
        });

        addPerson = (Button) findViewById(R.id.addPersonButton);
        addPerson.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view) { addTags(true); }
        });

        tagSearchView();
    }

    public void tagSearchView(){
        if(isSearching){
            addLocation.setVisibility(View.GONE);
            addPerson.setVisibility(View.GONE);
        }
        else{
            addLocation.setVisibility(View.VISIBLE);
            addPerson.setVisibility(View.VISIBLE);
        }
    }

    public void setTagButtonAdapters(){
        if(photo.getLocationTags()!=null) {
            locAdapter = new ArrayAdapter<String>(this, R.layout.album_text_view, R.id.text, photo.getLocationTags());
            ListView locationTagListView = findViewById(R.id.locationTagListView);
            locationTagListView.setAdapter(locAdapter);
            locationTagListView.setOnItemLongClickListener((p, V, pos, id) ->
                    deleteTag(pos,false));
        }

        if(photo.getPersonTags()!=null) {
            persAdapter = new ArrayAdapter<String>(this, R.layout.album_text_view, R.id.text, photo.getPersonTags());
            ListView personTagListView = findViewById(R.id.personTagListView);
            personTagListView.setAdapter(persAdapter);
            personTagListView.setOnItemLongClickListener((p, V, pos, id) ->
                    deleteTag(pos,true));
        }
    }

    public boolean deleteTag(int index, boolean forPerson){
        if(isSearching) return false;
        String tagValue;
        if(forPerson) tagValue=photo.getPersonTags().get(index);
        else tagValue=photo.getLocationTags().get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Do you want to delete \"" + tagValue + "\"?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String old = photo.removeTag(forPerson,index);
                AlbumList.editAutoCompleteValues(forPerson,false,old);
                persAdapter.notifyDataSetChanged();
                locAdapter.notifyDataSetChanged();

                try {
                    User.save(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        return true;

    }

    public void addTags(boolean forPerson){
        System.out.println("ADDING TAGS");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(forPerson) builder.setTitle("Enter Person Value");
        else builder.setTitle("Enter Location Value");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString().trim();
                if(value.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Empty tag value!",
                            Toast.LENGTH_LONG).show();
                    return;
                };

                if(!photo.addTag(forPerson, value)){ //adds to photo
                    Toast.makeText(getApplicationContext(), "Tag exists!", Toast.LENGTH_LONG).show();
                    return;
                }

                AlbumList.editAutoCompleteValues(forPerson,true,value);

                try {
                    User.save(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),
                        "TIP: Press+Hold a tag to delete",
                        Toast.LENGTH_SHORT).show();
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
}

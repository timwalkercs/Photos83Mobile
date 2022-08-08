package com.example.androidphotos83;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import Model.Album;
import Model.Photo;
import Model.User;

//Main Window Shown at Launch
public class AlbumList extends AppCompatActivity {

    //Add and Search Buttons
    private FloatingActionButton addAlbum;
    private Button search;

    //ListView and Adapter
    ListView listView;
    private static ArrayList<String> albumList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    //Search tags
    private static ArrayAdapter<String> locAdapter;
    private static ArrayAdapter<String> persAdapter;
    public ArrayList<String> allPersTags = new ArrayList<String>();
    public ArrayList<String> allLocTags = new ArrayList<String>();
    private static AutoCompleteTextView searchPersonBar;
    private static AutoCompleteTextView searchLocationBar;

    final Context context = this;
    String albumName;

    //Establish User
    public static User user = new User();
    File datafile = new File("/data/data/com.example.androidphotos83/files/youruserdata.dat");

    String namespace = "";
    //Runtime Functionality
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Album List");
        //Load User's Data
        try {
            user = User.load();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list_view);

        //Check if Data File Exists
        if(!datafile.exists()) {
            Context context = this;
            File file = new File(context.getFilesDir(), "youruserdata.dat");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Fill Native Album List and set ListView Adapter
        fillAlbumList();
        listView = findViewById(R.id.albumList);
        registerForContextMenu(listView);
        adapter = new ArrayAdapter<String>(this, R.layout.album_text_view, R.id.text, albumList);
        listView.setAdapter(adapter);

        //search bar adapter
        System.out.println("refilling");
        for(int i =0; i < user.getAlbums().size(); i++){
            Album crnt = user.getAlbums().get(i);
            for(int j=0; j<crnt.getPhotoCount(); j++){
                allPersTags.addAll(crnt.getPhotos().get(j).getPersonTags());
                allLocTags.addAll(crnt.getPhotos().get(j).getLocationTags());
            }
        }

        searchPersonBar = findViewById(R.id.searchPerson);
        searchLocationBar = findViewById(R.id.searchLocation);
        persAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allPersTags);
        locAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allLocTags);
        searchPersonBar.setAdapter(persAdapter);
        searchLocationBar.setAdapter(locAdapter);

        //When ListView Clicked
        listView.setOnItemClickListener((p, V, pos, id) ->
                showAlbum(pos));


        search = (Button) findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startSearchResults(
                        searchPersonBar.getText().toString().trim(),
                        searchLocationBar.getText().toString().trim()
                );
            }
        });

        addAlbum = (FloatingActionButton) findViewById(R.id.addAlbum);
        addAlbum.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Enter New Album Name");
                searchPersonBar.clearFocus();
                searchLocationBar.clearFocus();

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //take input
                        namespace = input.getText().toString();
                        if(namespace.trim().equalsIgnoreCase("")){
                            Toast.makeText(getApplicationContext(),
                                    "Album name cannot be blank!",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        //Check for duplicates...
                        for(int i = 0; i < user.getAlbums().size(); i++){
                            if(user.getAlbums().get(i).getAlbumName().equalsIgnoreCase(namespace)){
                                Toast.makeText(getApplicationContext(),
                                        "Album by that name already exists!",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        user.addAlbum(new Album(namespace));

                        try {
                            User.save(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        listView = (ListView) findViewById(R.id.albumList);
                        fillAlbumList();
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                        Toast.makeText(getApplicationContext(),
                                "TIP: Press+Hold an album for more options.",
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
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.album_options_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int listPosition;
        AdapterView.AdapterContextMenuInfo info;
        switch(item.getItemId()){
            //DELETE ALBUM
            case R.id.delete_album:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                listPosition = info.position;
                deleteAlbum(listPosition);
                return true;

            //RENAME ALBUM
            case R.id.rename_album:_album:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                listPosition = info.position;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Enter New Album Name");


                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //take input
                        namespace = input.getText().toString();

                        //check for duplicates
                        for(int i = 0; i < user.getAlbums().size(); i++){
                            if(user.getAlbums().get(i).getAlbumName().equalsIgnoreCase(namespace)){
                                Toast.makeText(getApplicationContext(),
                                        "Album by that name already exists!",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        user.getAlbums().get(listPosition).setAlbumName(namespace);

                        try {
                            User.save(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        listView = (ListView) findViewById(R.id.albumList);
                        fillAlbumList();
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
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
                default:
                    return super.onContextItemSelected(item);
        }
    }

    private boolean containsIgnoreCase(String text, ArrayList<String> list) {return list.stream().anyMatch(element -> element.equalsIgnoreCase(text));}

    private void startSearchResults(String pSearchInput, String lSearchInput){
        if(pSearchInput.isEmpty() && lSearchInput.isEmpty()) return;
        searchPersonBar.clearFocus();
        searchLocationBar.clearFocus();

        Album album = new Album("temp");
        for(int i=0; i < user.getAlbums().size(); i++){
            for(int j=0; j < user.getAlbums().get(i).getPhotoCount(); j++){
                Photo photo = user.getAlbums().get(i).getPhotos().get(j);

                if(containsIgnoreCase(pSearchInput, photo.getPersonTags())) {
                    if(!album.getPhotos().contains(photo)) album.addPhoto(photo);
                }

                if(containsIgnoreCase(lSearchInput, photo.getLocationTags())) {
                    if(!album.getPhotos().contains(photo)) album.addPhoto(photo);
                }

            }
        }
        user.setCrntAlbum(album);


        //intent
        Intent intent = new Intent(this, AlbumView.class);
        intent.putExtra("are searching?",true);

        //start intent
        startActivity(intent);
    }

    private boolean deleteAlbum(int pos) {
        user.deleteAlbum(pos);
        try {
            User.save(user);
        } catch (IOException e) {
            return false;
        }

        listView = (ListView) findViewById(R.id.albumList);
        fillAlbumList();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        return true;
    }

    private void showAlbum(int pos) {
        Bundle bundle = new Bundle();
        user.setCrntAlbum(user.getAlbums().get(pos));
        Album album = user.getCrntAlbum();
        bundle.putBoolean("are searching?",false);
        bundle.putInt("crnt album pos", pos);
        bundle.putStringArrayList("list of album names", albumList);
        Intent intent = new Intent(this, AlbumView.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private static void fillAlbumList(){
        albumList.clear();
        for(int i =0; i < user.getAlbums().size(); i++){
            albumList.add(user.getAlbums().get(i).getAlbumName());
        }

    }

    public static void editAutoCompleteValues(boolean forPerson, boolean adding, String val){
        if(forPerson) {
            searchPersonBar.setText("");
            searchPersonBar.clearFocus();
            if(adding) persAdapter.add(val);
            else persAdapter.remove(val);
            persAdapter.getFilter().filter(searchPersonBar.getText(), null);
        }
        else {
            searchLocationBar.setText("");
            searchLocationBar.clearFocus();
            if(adding) locAdapter.add(val);
            else locAdapter.remove(val);
            locAdapter.getFilter().filter(searchLocationBar.getText(), null);
        }
    }

}
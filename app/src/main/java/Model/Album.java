package Model;
//Timothy Walker tpw32
//Hasin Choudhury hmc94

import java.io.Serializable;
import java.util.ArrayList;


public class Album implements Serializable {


    private static final long serialVersionUID = 1L;

    String albumName;

    public ArrayList<Photo> photos = new ArrayList<Photo>();

    public Album(String albumName) {
        this.albumName = albumName;
    }

    public void addPhoto(Photo photo){
        photos.add(photo);
    }

    public ArrayList<Photo> getPhotos(){
        return photos;
    }


    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }


    public String getAlbumName() {
        return albumName;
    }


    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }


    public int getPhotoCount() {
        return photos.size();
    }



}

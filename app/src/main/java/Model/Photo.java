//Timothy Walker tpw32
//Hasin Choudhury hmc94

package Model;

import android.media.Image;
import android.net.Uri;

import java.util.ArrayList;
import java.io.IOException;
import java.io.Serializable;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Enumeration;

/**
 * Photo class, maintains all of a Photo's details and data
 * @author Timothy Walker and Hasin Choudhury
 *
 */
public class Photo implements Serializable{


    private static final long serialVersionUID = 1L;

    private String imagePath;
    private String name;
    private ArrayList<String> locationTag;
    private ArrayList<String> personTag;

    public Photo(){
        locationTag = new ArrayList<String>();
        personTag = new ArrayList<String>();
    }

    public ArrayList<String> getPersonTags(){
        return personTag;
    }

    public ArrayList<String> getLocationTags(){
        return locationTag;
    }

    private boolean containsIgnoreCase(String text, ArrayList<String> list) {return list.stream().anyMatch(element -> element.equalsIgnoreCase(text));}

    public boolean addTag(boolean forPerson, String entry) {
        if(!containsIgnoreCase(entry, personTag) && forPerson) personTag.add(entry);
        else if(!containsIgnoreCase(entry, locationTag) && !forPerson) locationTag.add(entry);
        else return false;
        return true;
    }


    public String removeTag(boolean forPerson, int index){
        String value;
        if(forPerson) {
            value = personTag.get(index);
            personTag.remove(index);
        }
        else {
            value = locationTag.get(index);
            locationTag.remove(index);
        }
        return value;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFileName() {
        return name;
    }

    public void setFileName(String name) {
        this.name = name;
    }
}

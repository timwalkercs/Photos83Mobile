package Model;
import java.io.*;
import java.util.*;
import android.content.Context;
import com.example.androidphotos83.AlbumList;

public class User implements Serializable {

    public static final long serialVersionUID = 1L;
    private ArrayList<Album> usersAlbums;
    private Album crntAlbum;

    public User() { usersAlbums = new ArrayList<Album>(); }

    public void addAlbum(Album album) {
        usersAlbums.add(album);
    }

    public void deleteAlbum(int index) {
        usersAlbums.remove(index);
    }

    public List<Album> getAlbums() {
        return usersAlbums;
    }

    public Album getCrntAlbum() {
        return crntAlbum;
    }

    public void setCrntAlbum(Album temp) {
        crntAlbum = temp;
    }


    public static void save(User data) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/data/data/com.example.androidphotos83/files/youruserdata.dat"));
        oos.writeObject(data);
        oos.close();
    }

    public static User load() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/data/data/com.example.androidphotos83/files/youruserdata.dat"));
        User data = (User) ois.readObject();
        ois.close();
        return data;
    }

}

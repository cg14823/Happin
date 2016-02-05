package mosaic.happin;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private LatLng location;
    private String name;
    private String description;
    private String img;
    private int likes;
    private String user;
    //add image

    public Place(LatLng location, String name, String description,String user){
        this.location = location;
        this.name = name;
        this. description = description;
        likes = 0;
        this.user=user;
    }
    public Place(LatLng location, String name, String description, String img,String user){
        this.location = location;
        this.name = name;
        this. description = description;
        this.img =img;
        likes = 0;
        this.user = user;
    }
    public Place(){}

    //Create an image setter and getter

    //getters
    public LatLng getLatlng(){
        return location;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getImage(){return img;}
    public int getLikes(){return likes;}
    public String getUser(){return user;}

    public void addLike (){likes +=1;}

}

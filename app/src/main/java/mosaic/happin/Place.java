package mosaic.happin;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private LatLng location;
    private String name;
    private String description;
    private String img;
    private int likes;
    //add image

    public Place(LatLng location, String name, String description){
        this.location = location;
        this.name = name;
        this. description = description;
        likes = 0;
    }
    public Place(LatLng location, String name, String description, String img){
        this.location = location;
        this.name = name;
        this. description = description;
        this.img =img;
        likes = 0;
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

    public void addLike (){likes +=1;}

}

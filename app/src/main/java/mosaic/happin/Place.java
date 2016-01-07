package mosaic.happin;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private LatLng location;
    private String name;
    private String description;
    private Bitmap img;
    //add image

    public Place(LatLng location, String name, String description){
        this.location = location;
        this.name = name;
        this. description = description;
    }

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

    public Bitmap getImg(){
        return img;
    }

}

package mosaic.happin;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Place {


    private double lat;
    private double lon;
    private String name;
    private String description;
    private String img;
    private int likes;
    private String user;
    //add image

    public Place(double lat, double lon, String name, String description,String user){
        this.lat = lat;
        this.lon =lon;
        this.name = name;
        this. description = description;
        likes = 0;
        this.user=user;
    }
    public Place(double lat, double lon, String name, String description, String img,String user){
        this.lat = lat;
        this.lon =lon;
        this.name = name;
        this. description = description;
        this.img =img;
        likes = 0;
        this.user = user;
    }
    public Place(){}


    //getters
    public double getLat(){
        return lat;
    }
    public double getLon(){
        return lon;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getImg(){return img;}
    public int getLikes(){return likes;}
    public String getUser(){return user;}

    public void addLike (){likes +=1;}

    @Override
    public String toString(){
        return name+ " "+ lat+","+lon+" "+description;
    }

}

package mosaic.happin;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Place implements Parcelable{


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


    protected Place(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
        name = in.readString();
        description = in.readString();
        img = in.readString();
        likes = in.readInt();
        user = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(img);
        parcel.writeInt(likes);
        parcel.writeString(user);
    }
}

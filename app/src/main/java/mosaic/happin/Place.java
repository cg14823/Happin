package mosaic.happin;

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

    public Place(double lat, double lon, String name, String description, String user) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.description = description;
        likes = 0;
        this.user = user;
    }

    public Place(double lat, double lon, String name, String description, String img, String user) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.description = description;
        this.img = img;
        likes = 0;
        this.user = user;
    }

    public Place() {
    }

    @Override
    public boolean equals(Object o) {
        if (this.lat == ((Place) o).getLat() && this.lon == ((Place) o).getLon()) return true;
        else return false;
    }


    public Place(Place p) {
        this.lat = p.getLat();
        this.lon = p.getLon();
        this.name = p.getName();
        this.description = p.getDescription();
        this.img = p.getImg();
        this.likes = p.getLikes();
        this.user = p.getUser();
    }


    //getters
    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }

    public int getLikes() {
        return likes;
    }

    public String getUser() {
        return user;
    }

    public void setImage(String userimg) {
        if (userimg.equals("0")) img = null;
        else img = userimg;
    }

    public String latLng2Id(LatLng location) {
        String lat = String.valueOf(location.latitude);
        String lon = String.valueOf(location.longitude);
        String strLoc = (lat + "L" + lon).replace(".", "p");
        return strLoc;
    }

    public String latLng2Id(double latitude, double longitude) {
        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);
        String strLoc = (lat + "L" + lon).replace(".", "p");
        return strLoc;
    }

    public String latLng2Id() {
        return latLng2Id(lat, lon);

    }

    public void addLike() {
        likes += 1;
    }

    @Override
    public String toString() {
        return name + " " + lat + "," + lon + " " + description;
    }

    // This method should somehow compute the score(number of stars to display)
    public int comupteScore() {
        if (likes > 50) return 5;
        else return likes / 10;
    }

    public Place setLikes(int likes) {
        this.likes = likes;
        return this;
    }
}

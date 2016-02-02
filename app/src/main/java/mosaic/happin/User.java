package mosaic.happin;

import android.graphics.Bitmap;

/**
 * Created by Carlos on 02/02/2016.
 */
public class User {
    private String name;
    private String email;
    private int points;
    private String provider;
    private boolean verified;
    private Bitmap profilePic;
    public User(){

    }
    public User(String name, String email, String provider){
        this.name = name;
        this.email =email;
        this.provider = provider;
        points = 0;
        verified = false;
    }
    public User(User user){
        name = user.getName();
        email =user.getEmail();
        points = user.getPoints();
        verified = user.getVerified();
        profilePic = user.getProfilePic();
        provider = user.getProvider();

    }

    public String getName(){return name;}
    public String getEmail(){return email;}
    public int getPoints(){return points;}
    public boolean getVerified(){return verified;}
    public String getProvider(){return provider;}
    public Bitmap getProfilePic(){return profilePic;}

    public void changeName(String name){this.name = name;}
    public void incrementPoints(int points){this.points += points;}
    public void changeEmail(String email){this.email = email;}
    public void verify(){verified = true;}

    @Override
    public String toString(){
        return email+" "+name+" "+points+" "+provider+" "+verified;
    }

}

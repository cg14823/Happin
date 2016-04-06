package mosaic.happin;



/**
 * Created by Carlos on 02/02/2016.
 */
public class User {
    private String name;
    private String email;
    private int points;
    private String provider;
    private boolean verified;
    private String profileImage;
    public User(){

    }
    public User(String name, String email, String provider, String profileImage){
        this.name = name;
        this.email =email;
        this.provider = provider;
        points = 0;
        verified = false;
        this.profileImage = profileImage;
    }
    public User(User user){
        name = user.getName();
        email =user.getEmail();
        points = user.getPoints();
        verified = user.getVerified();
        provider = user.getProvider();
        profileImage = user.getProfileImage();

    }

    public String getName(){return name;}
    public String getEmail(){return email;}
    public int getPoints(){return points;}
    public String getProvider(){return provider;}
    public boolean getVerified(){return verified;}
    public String getProfileImage(){return profileImage;}


    public void changeName(String name){this.name = name;}
    public void incrementPoints(int points){this.points += points;}
    public void changeEmail(String email){this.email = email;}
    public void verify(){verified = true;}
    public void changeProfileImage(String image){
        profileImage = image;
    }

    @Override
    public String toString(){
        return email+" "+name+" "+points+" "+provider+" "+verified;
    }

}

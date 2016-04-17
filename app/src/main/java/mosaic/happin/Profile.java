
package mosaic.happin;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class Profile extends Fragment {
    private Firebase myFirebaseRef;
    private User user;
    private final int image_width=400;
    private final int image_height=350;
    String userId;
    View profileView;
    private List<Place> likedPlaces=new ArrayList<Place>();
    private List<Place> addedPlaces=new ArrayList<Place>();
    private List<Bitmap> imagesOflikedPlaces=new ArrayList<>();

    //the images to display
    Integer[] imageIDs = {
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic4,
            R.drawable.pic5,
            R.drawable.pic6,
            R.drawable.pic7
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        profileView = inflater.inflate(R.layout.fragment_profile_temp, container, false);
        userId = MainActivity.userId;
        showToast(userId);
        new PopulateUser().execute();
        new PopulatePlaces().execute();



        //PopulatePlaces populatePlaces=new PopulatePlaces();
        //populatePlaces.execute();

        /*Gallery gallery2 = (Gallery) profileView.findViewById(R.id.gallery2);

        gallery2.setAdapter(new ImageAdapter(this.getContext()));
        ViewGroup.MarginLayoutParams mlp2 = (ViewGroup.MarginLayoutParams) gallery2.getLayoutParams();
        mlp2.setMargins(-(metrics.widthPixels / 2 + image_width-90),
                mlp2.topMargin,
                mlp2.rightMargin,
                mlp2.bottomMargin
        );
        gallery2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,long id)
            {
                Toast.makeText(getBaseContext(), "pic" + (position + 1) + " selected",
                        Toast.LENGTH_SHORT).show();
                // display the images selected
                ImageView imageView = (ImageView) findViewById(R.id.image1);
                imageView.setImageResource(imageIDs[position]);
           }
        });
        */
        return profileView;
    }

    class PopulateUser extends AsyncTask<Void,Void,User> {
        @Override
        protected User doInBackground(Void... params) {
            // FIND USER'S INFORMATION
            myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/users/" + userId);
            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        showToast("the user name is(1): " + user.getName());
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            //Populate user's views
            super.onPostExecute(user);
            showToast("postExecute USER");
            if (user == null) {
                showToast("the user name is(2): " + "user is null");
            } else {
                TextView userName = (TextView) profileView.findViewById(R.id.user_name);
                showToast("the user name is(2): " + user.getName());
                userName.setText(user.getName());
                TextView userPoints = (TextView) profileView.findViewById(R.id.user_points);
                userPoints.setText(user.getPoints() + " Points");
                ImageView profile_picture = (ImageView) profileView.findViewById(R.id.profile_picture);
                String profileImage = user.getProfileImage();
                if (profileImage == null) {
                    profile_picture.setImageResource(R.drawable.empty_profile);
                } else {
                    profile_picture.setImageBitmap(StringToBitMap(profileImage));
                }
                ImageView header_ImageView = (ImageView) profileView.findViewById(R.id.header_imageview);
                if (likedPlaces.isEmpty()) {
                    header_ImageView.setImageResource(R.drawable.bristol);
                } else {
                    header_ImageView.setImageBitmap(StringToBitMap(likedPlaces.get(0).getImg()));
                }
            }
        }
    }
    class PopulatePlaces extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //FIND LIKED PLACES
            myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/likes/" + userId);
            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        //showToast(child.getKey());
                        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
                        myFirebaseRef.child(child.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    Place currentPlace = dataSnapshot.getValue(Place.class);
                                    showToast(currentPlace.getName());
                                    likedPlaces.add(currentPlace);
                                    //showToast("Likes places: " + likedPlaces.toString());
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }

            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showToast("postExecute PLACES");
            if (likedPlaces.isEmpty()==false) {
                showToast("postExecute PLACES: number of places is" + likedPlaces.size());
                for (Place place:likedPlaces) {
                    imagesOflikedPlaces.add(StringToBitMap(place.getImg()));
                }

                Gallery gallery1 = (Gallery) profileView.findViewById(R.id.gallery1);
                gallery1.setAdapter(new ImageAdapter(getContext()));

                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gallery1.getLayoutParams();
                mlp.setMargins(-(metrics.widthPixels / 2 + image_width - 90),
                        mlp.topMargin,
                        mlp.rightMargin,
                        mlp.bottomMargin
                );
                gallery1.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                /*Toast.makeText(getBaseContext(), "pic" + (position + 1) + " selected",
                        Toast.LENGTH_SHORT).show();
                // display the images selected
                ImageView imageView = (ImageView) findViewById(R.id.image1);
                imageView.setImageResource(imageIDs[position]);*/
                    }
                });
            }
            else showToast("PLACES EMPTY");
        }

    }
    public Bitmap StringToBitMap(String encodedString){
            try {
                byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch(Exception e) {
                e.getMessage();
                return null;
            }
        }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a =c.obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }
        // returns the number of images
        public int getCount() {
            return imageIDs.length;
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            if(imagesOflikedPlaces.size()>position)imageView.setImageBitmap(imagesOflikedPlaces.get(position));
            imageView.setLayoutParams(new Gallery.LayoutParams(image_width, image_height));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }
    private void showToast(String message) {
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}





















/*package mosaic.happin;


import android.content.Context;
import android.graphics.Typeface;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.

public class Profile extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public int tab=0;
    private List<Place> places =new ArrayList<>();
    String userId;
    View profileView;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        userId = MainActivity.userId;
        setProfile(profileView);
        setTab(profileView);
        getLiked();
        return profileView;

    }
    private void getLiked(){
        Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/likes/"+userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> placesId = new ArrayList<String>();
                final long num = dataSnapshot.getChildrenCount();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    placesId.add(d.getKey());
                }
                if (!placesId.isEmpty()) {
                    for (String s : placesId) {
                        Firebase ref1 = new Firebase("https://flickering-torch-2192.firebaseio.com/places/"
                                + s);
                        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Place p = dataSnapshot.getValue(Place.class);
                                places.add(p);
                                if (places.size() == num) setGrid();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getYours(){

        Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
        Query refQuery = ref.orderByChild("user").equalTo(userId);
        refQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> placesId = new ArrayList<String>();
                final long num = dataSnapshot.getChildrenCount();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    placesId.add(d.getKey());
                }
                if (!placesId.isEmpty()) {
                    for (String s : placesId) {
                        Firebase ref1 = new Firebase("https://flickering-torch-2192.firebaseio.com/places/"
                                + s);
                        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Place p = dataSnapshot.getValue(Place.class);
                                places.add(p);
                                if (places.size() == num) setGrid();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setTab(View profileView){
        TextView likedPlaceTxtV = (TextView) profileView.findViewById(R.id.likedPlacestab);
        TextView yourPlacesTX = (TextView) profileView.findViewById(R.id.yourPlacestab);
        likedPlaceTxtV.setTypeface(null, Typeface.BOLD);
        tab=1;
        likedPlaceTxtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTabs(1);
            }
        });
        yourPlacesTX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTabs(2);
            }
        });
    }

    private void setGrid(){
        ArrayAdapter<Place> adapter = new CustomAdapter(places);
        GridView grid = (GridView) profileView.findViewById(R.id.profileGrid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Place p = places.get(position);
                String ref = "https://flickering-torch-2192.firebaseio.com/places/"
                        + p.latLng2Id(p.getLat(), p.getLon());
                showDetails(ref);

            }
        });
    }

    private void showDetails(String ref){
        Intent detailShow = new Intent(getContext(), ShowPlacesDetail.class);
        detailShow.putExtra("ref", ref);
        detailShow.putExtra("USER_ID", userId);
        startActivity(detailShow);
    }

    private void setProfile(View view){
        // Retrive Name from database
        final TextView nameField = (TextView) view.findViewById(R.id.details);
        final TextView points = (TextView) view.findViewById(R.id.points);
        final ImageView profilePicView = (ImageView) view.findViewById(R.id.profilePic);
        Firebase myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/users/"+userId+"/");
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    nameField.setText(user.getName());
                    points.setText("Points:" + user.getPoints());
                    String image = user.getProfileImage();
                    if (image.equals("null Image"))
                        profilePicView.setImageResource(R.drawable.empty_profile);
                    else {
                        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profilePicView.setImageBitmap(decodedByte);
                    }
                } else
                    showToast("ERROR!");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }


    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void changeTabs(int source){
        if(source == 1){
            if(tab == 2){
                tab = 1;
                View underline1 = profileView.findViewById(R.id.underline1);
                View underline2 = profileView.findViewById(R.id.underline2);
                underline1.setVisibility(View.VISIBLE);
                underline2.setVisibility(View.INVISIBLE);
                TextView likedPlaceTxtV = (TextView) profileView.findViewById(R.id.likedPlacestab);
                TextView yourPlacesTX = (TextView) profileView.findViewById(R.id.yourPlacestab);
                likedPlaceTxtV.setTypeface(null,Typeface.BOLD);
                yourPlacesTX.setTypeface(null, Typeface.NORMAL);
                GridView grid = (GridView)profileView.findViewById(R.id.profileGrid);
                grid.setAdapter(null);
                getLiked();

            }
        }
        else if (source == 2){
            if (tab == 1) {
                tab = 2;
                View underline1 = profileView.findViewById(R.id.underline1);
                View underline2 = profileView.findViewById(R.id.underline2);
                underline1.setVisibility(View.INVISIBLE);
                underline2.setVisibility(View.VISIBLE);
                TextView likedPlaceTxtV = (TextView) profileView.findViewById(R.id.likedPlacestab);
                TextView yourPlacesTX = (TextView) profileView.findViewById(R.id.yourPlacestab);
                likedPlaceTxtV.setTypeface(null, Typeface.NORMAL);
                yourPlacesTX.setTypeface(null, Typeface.BOLD);
                GridView grid = (GridView)profileView.findViewById(R.id.profileGrid);
                grid.setAdapter(null);
                getYours();

            }
        }

    }

    private class CustomAdapter extends ArrayAdapter<Place>{

        public CustomAdapter(List<Place> places) {
            super(getActivity().getApplicationContext(), R.layout.profileplacepreview, places);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView== null){
                itemView=getActivity().getLayoutInflater().inflate(R.layout.profileplacepreview,parent,false);
            }
            Place currentPlace = places.get(position);
            ImageView img = (ImageView) itemView.findViewById(R.id.placePreviewImage);
            TextView placeName = (TextView) itemView.findViewById(R.id.placePreviewName);
            TextView placeID = (TextView) itemView.findViewById(R.id.previewId);
            placeName.setText(currentPlace.getName());
            placeID.setText(currentPlace.latLng2Id(currentPlace.getLat(),currentPlace.getLon()));
            byte[] decodedString = Base64.decode(currentPlace.getImg(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            img.setImageBitmap(decodedByte);
            return itemView;
        }
    }
}
*/
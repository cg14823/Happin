package mosaic.happin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    Firebase myFirebaseRef;
    String userId;
    View profileView;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/");
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        userId = MainActivity.userId;
        setProfile(profileView);
        getLP();
        return profileView;

    }

    private void setProfile(View view){
        // Retrive Name from database
        final TextView nameField = (TextView) view.findViewById(R.id.details);
        final TextView points = (TextView) view.findViewById(R.id.points);
        final ImageView profilePicView = (ImageView) view.findViewById(R.id.profilePic);
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/users/"+userId+"/");
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

    // Goes to the server and gets the Places you´ve liked
    private void getLP(){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/likes/"+
                userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> placesIDs = new ArrayList<String>();
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    placesIDs.add(d.getKey());
                }
                if (placesIDs.isEmpty()){
                    LinearLayout parent = (LinearLayout) profileView.findViewById(R.id.containerLP);
                    TextView empty = new TextView(getContext());
                    empty.setText("You haven't liked any places");
                    parent.addView(empty);

                }
                else {
                    for (String s : placesIDs) {
                        Firebase placesRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places/" +
                                s);
                        placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    LinearLayout parent = (LinearLayout) profileView.findViewById(R.id.containerLP);

                                    Place p = dataSnapshot.getValue(Place.class);

                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    View placepreview = (inflater.inflate(R.layout.profileplacepreview, null));

                                    TextView previewText = (TextView) placepreview.findViewById(R.id.placePreviewName);
                                    TextView previewId = (TextView) placepreview.findViewById(R.id.previewId);
                                    ImageView imgView = (ImageView) placepreview.findViewById(R.id.placePreviewImage);

                                    previewText.setText(p.getName());
                                    previewId.setText(p.latLng2Id(p.getLat(),p.getLon()));

                                    byte[] decodedString = Base64.decode(p.getImg(), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imgView.setImageBitmap(decodedByte);

                                    parent.addView(placepreview);
                                }
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


    // Goes to the server and gets the Places you´ve added
    private ArrayList<Place> getYP(){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        final ArrayList<Place> places = new ArrayList<Place>();
        return places;
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

}

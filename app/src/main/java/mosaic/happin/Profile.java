package mosaic.happin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private User user;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setProfile(view);
        addLP(view);
        addYP(view);

        return view;

    }

    private void setProfile(View view){
        // Retrive Name from database
        String userId = MainActivity.userId;
        final TextView nameField = (TextView) view.findViewById(R.id.details);
        final TextView points = (TextView) view.findViewById(R.id.points);
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/users/"+userId+"/");
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    nameField.setText(user.getName());
                    points.setText("Points:" +user.getPoints());
                }
                else
                    showToast("ERROR!");
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


    }

    private void addLP(View view){
        ArrayList<Place> places = getLP();
        ViewGroup parent = (ViewGroup) view.findViewById(R.id.container);
        if (places.isEmpty()){
            TextView empty = new TextView(getContext());
            empty.setText("You haven't liked any places!");
            empty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            parent.addView(empty);
        }
        else showToast("You have places MATE!");
    }
    private void addYP(View view){
        ArrayList<Place> places = getYP();
        ViewGroup parent = (ViewGroup) view.findViewById(R.id.container2);
        if (places.isEmpty()){
            TextView empty = new TextView(getContext());
            empty.setText("You haven't added any places!");
            empty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            parent.addView(empty);
        }
    }
    // Goes to the server and gets the Places you´ve liked
    private ArrayList<Place> getLP(){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        ArrayList<Place> places = new ArrayList<Place>();

        return places;
    }

    // Goes to the server and gets the Places you´ve added
    private ArrayList<Place> getYP(){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        final ArrayList<Place> places = new ArrayList<Place>();
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
        Query addedPQ = myFirebaseRef.orderByChild("user").equalTo(MainActivity.userId);
        showToast(MainActivity.userId);

        addedPQ.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot querySnapshot) {
                showToast("Hey1");
                for (DataSnapshot d : querySnapshot.getChildren()) {
                    showToast("Hey2");
                    myFirebaseRef.child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                showToast("Hey3");
                                Place p = dataSnapshot.getValue(Place.class);
                                places.add(p);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                showToast(error.getMessage());
            }
        });
        showToast(String.valueOf(places.size()));
        return places;
    }

    private void setUser(User user){
        this.user = new User(user);
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

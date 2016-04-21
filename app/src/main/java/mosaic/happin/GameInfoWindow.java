package mosaic.happin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;

/**
 * Created by Tom on 02/03/2016.
 */
public class GameInfoWindow extends AppCompatActivity {
    private MapView mapView;
    private GoogleMap mMap;
    private Place place;
    private String userId;
    private int distance;
    private User user;
    private Firebase uref;
    private Button button;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info_window);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // title of toolbar in verdana bold as required by Happy City
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/verdanab.ttf");
        title.setTypeface(custom_font);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent i = getIntent();
        url = i.getStringExtra("ref");
        userId = i.getStringExtra("USER_ID");
        distance = i.getIntExtra("distance",-1);
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase(url);
        uref = new Firebase("https://flickering-torch-2192.firebaseio.com/users/" + userId);
        mapView = (MapView) findViewById(R.id.placeMapView);
        mapView.onCreate(savedInstanceState);
        button = (Button) findViewById(R.id.visitButton);
        if (distance < 0) {
            showToast("Error getting distance");
        }
        else {
            if (distance <= 50) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                place = dataSnapshot.getValue(Place.class);
                if ((place != null)){
                    addDetails();
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            mMap.getUiSettings().setAllGesturesEnabled(false);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLat(), place.getLon()), 17));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLat(), place.getLon()))
                                    .title(place.getName()).snippet(place.getDescription())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.happy_marker)));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                showToast(error.getMessage());
            }
        });

        uref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(FirebaseError error) {
                showToast(error.getMessage());
            }
        });



    }
    private void addDetails(){
        TextView text = (TextView)findViewById(R.id.placeText);
        text.setText(place.getName() + "\nDistance: "+ distance + "m");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                final int pointsGained = data.getIntExtra("points",1);
                if (pointsGained == 1){
                    showToast("You earned 1 point!");
                }
                else {
                    showToast("You earned "+pointsGained+" points!");
                }
                button.setEnabled(false);
                button.setText("VISITED");
                uref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user.incrementPoints(pointsGained);
                        java.util.Map<String, Object> points = new HashMap<>();
                        points.put("points", user.getPoints());
                        uref.updateChildren(points);
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                        showToast(error.getMessage());
                    }
                });
            }
            else {
                userId = data.getStringExtra("userId");
                uref = new Firebase("https://flickering-torch-2192.firebaseio.com/users/" + userId);
                showToast(uref.toString());
                uref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                        showToast(error.getMessage());
                    }
                });
                showToast(user.toString());
            }
        }
    }

    public void visited(View view){
        Intent simonSays = new Intent(this, SimonSaysGame.class);
        simonSays.putExtra("userId",userId);
        simonSays.putExtra("place",url);
        startActivityForResult(simonSays, 1);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }



}

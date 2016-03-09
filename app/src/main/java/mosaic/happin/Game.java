package mosaic.happin;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Game extends Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    public Game() {
        // Required empty public constructor
    }
    private Circle circle;
    private GoogleMap mMap;
    private MapView mapView;
    Firebase ref;
    ArrayList<Place> places;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Firebase.setAndroidContext(getContext());
        ref = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        try {
            mMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e) {}
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()), 17));
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latlng = marker.getPosition();

                Intent detailShow = new Intent(getActivity(), GameInfoWindow.class);
                detailShow.putExtra("ref", "https://flickering-torch-2192.firebaseio.com/places/" + latLng2Id(latlng));
                detailShow.putExtra("USER_ID", MainActivity.userId);
                detailShow.putExtra("distance",(int) distanceFromCurrent(latlng));
                startActivity(detailShow);
                return true;
            }
        });
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(0, 0))
                .visible(false)
                .strokeWidth(5)
                .radius(50));



        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getPlaces();

        /* setLocationCheck creates a location button listener, if someone clicks it will check if
        * the location service is enabled, if it is not it ask you if you want to activate it
        * otherwise it will just zoom to your current location (High-precision not best choice?)*/
        setLocationCheck();

        return view;
    }

    private String latLng2Id(LatLng location){
        String lat = String.valueOf(location.latitude);
        String lon = String.valueOf(location.longitude);
        String strLoc = (lat+"L"+lon).replace(".", "p");
        return strLoc;
    }

    private float distanceFromCurrent(LatLng latLng) {

        Location loc1 = new Location(LocationManager.GPS_PROVIDER);

        loc1.setLatitude(latLng.latitude);
        loc1.setLongitude(latLng.longitude);


        return loc1.distanceTo(mLocation);
    }

    private void setLocationCheck() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                // Acquire a reference to the system Location Manager
                LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // If Location disable create a alert dialog
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("Location is disabled in your device. Would you like to enable it?")
                            // Have to respond to this message not cancelable
                            .setCancelable(false)
                                    // If yes open setting page to enable location
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // intent calls the android activity of location settings
                                            Intent callGPSSettingIntent = new Intent(
                                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivity(callGPSSettingIntent);
                                        }
                                    });
                    //if no close the dialog
                    alertDialogBuilder.setNegativeButton("Maybe Later",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }

                return false;
            }
        });
    }


    /* This function should ge the top rated places from the server*/
    private void getPlaces(){
        places = new ArrayList<Place>();
        ref = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
        Query likeQuery = ref.orderByChild("likes").limitToLast(10);
        likeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot querySnapshot) {
                for (DataSnapshot d : querySnapshot.getChildren()) {
                    ref.child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Place p = dataSnapshot.getValue(Place.class);
                                mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLon()))
                                        .title(p.getName()).snippet(p.getDescription()));
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

        LatLng bristol = new LatLng(51.465411, -2.585911);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bristol, 17));
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient.connect();
        }
        if (mLocation == null){
            mLocation = new Location(LocationManager.GPS_PROVIDER);
            mLocation.setLatitude(51.465411);
            mLocation.setLongitude(-2.585911);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 17));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,mLocationRequest,this);
        }
        catch (SecurityException e){}
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.

    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location l) {
        mLocation = l;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(),l.getLongitude()), 17));
        circle.setCenter(new LatLng(l.getLatitude(), l.getLongitude()));
        if (!(circle.isVisible())) {
            circle.setVisible(true);
        }
    }

}

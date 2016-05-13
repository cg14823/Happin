package mosaic.happin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { interface
 * to handle interaction events.
 * Use the {@link Map#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Map extends Fragment implements GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private MapView mapView;
    private View thisView;
    private int markerCount;
    Firebase ref;

    public Map() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        markerCount = 0;

        Firebase.setAndroidContext(getContext());
        ref = new Firebase("https://flickering-torch-2192.firebaseio.com/places");

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        thisView = view;
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mapView.getMap();
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            showToast(e.getMessage());
        }

        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addPlace(latLng);
            }
        });


        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
            LatLng bristol = new LatLng(51.465411, -2.585911);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bristol, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.findViewById(R.id.maploadingbar).setVisibility(View.VISIBLE);
        getPlaces();

        /* setLocationCheck creates a location button listener, if someone clicks it will check if
        * the location service is enabled, if it is not it ask you if you want to activate it
        * otherwise it will just zoom to your current location (High-precision not best choice?)*/
        setLocationCheck();

        return view;
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

    public List<String> reverseGeo(double lat, double lng) {
        try {
            List<String> location = new ArrayList<>();
            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
            Address address = addresses.get(0);
            location.add(0, address.getThoroughfare());
            location.add(1, address.getSubThoroughfare());
            return location;
        } catch (IOException e) {
            List<String> location = new ArrayList<>();
            location.add(0, "Can't");
            location.add(1, "find location");
            return location;
        }
    }

    private void addPlace(LatLng location) {
        //Creates dialog to input place detail
        ref = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
        final LatLng placeloc = new LatLng(location.latitude, location.longitude);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog.Builder recPassDialog = new AlertDialog.Builder(getContext());
        final View dialogView = (inflater.inflate(R.layout.dialog_add_place, null));
        recPassDialog.setView(dialogView);
        TextView locfield = (TextView) dialogView.findViewById(R.id.location);
        List<String> s = reverseGeo(location.latitude, location.longitude);
        String locationStr = s.get(1) + " " + s.get(0);
        locationStr = locationStr.replace("null", "");
        locfield.setText(locationStr);

        recPassDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText nameField = (EditText) dialogView.findViewById(R.id.name);
                EditText description = (EditText) dialogView.findViewById(R.id.description);
                ImageView image = (ImageView) dialogView.findViewById(R.id.placeImg);
                Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                bmp.recycle();
                byte[] byteArray = bYtE.toByteArray();
                String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                final Place place = new Place(placeloc.latitude, placeloc.longitude,
                        nameField.getText().toString(),
                        description.getText().toString(), imageFile, MainActivity.userId);
                //pushes to database with new unique id
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ref.child(place.latLng2Id(placeloc)).setValue(place);
                        showToast("Place added");
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });

            }
        });

        recPassDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = recPassDialog.create();
        alert.show();

    }


    /* This function should ge the top rated places from the server*/
    private void getPlaces() {
        ref = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
        Query likeQuery = ref.orderByChild("likes").limitToLast(10);
        likeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot querySnapshot) {
                final long count = querySnapshot.getChildrenCount();
                for (DataSnapshot d : querySnapshot.getChildren()) {
                    ref.child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                try {
                                    markerCount++;
                                    if (markerCount == count)
                                        thisView.findViewById(R.id.maploadingbar).setVisibility(View.GONE);
                                    Place p = dataSnapshot.getValue(Place.class);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLon()))
                                            .title(p.getName()).snippet(p.getDescription())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.happy_marker)));
                                } catch (Exception e) {
                                    showToast(e.getMessage());
                                }
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
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        LatLng latlng = marker.getPosition();
        Intent detailShow = new Intent(this.getActivity(), ShowPlacesDetail.class);
        detailShow.putExtra("ref", "https://flickering-torch-2192.firebaseio.com/places/" + latLng2Id(latlng));
        detailShow.putExtra("USER_ID", MainActivity.userId);
        startActivity(detailShow);
        return true;
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

    private void showToast(String message) {
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static String latLng2Id(LatLng location) {
        String lat = String.valueOf(location.latitude);
        String lon = String.valueOf(location.longitude);
        return (lat + "L" + lon).replace(".", "p");
    }


}

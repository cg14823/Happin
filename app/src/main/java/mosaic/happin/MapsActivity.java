package mosaic.happin;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    public MapsActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fm = getChildFragmentManager();
        mapFragment  = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Bristol and move the camera
        LatLng Bristol = new LatLng(51.465411, -2.585911);
        mMap.addMarker(new MarkerOptions().position(Bristol).title("Bristol"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Bristol, 13));
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        killOldMap();
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        killOldMap();
    }
    private void killOldMap() {

        if(mapFragment != null) {
            FragmentManager fM = getFragmentManager();
            fM.beginTransaction().remove(mapFragment).commit();
        }
    }

}

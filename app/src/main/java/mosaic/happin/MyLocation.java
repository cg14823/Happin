package mosaic.happin;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

public class MyLocation implements
        LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    private Location location;
    GoogleApiClient mGoogleApiClient;
    Context context;
    LocationRequest mLocationRequest;

    public MyLocation(Context context){
        this.context = context;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,mLocationRequest,this);
        }
        catch (SecurityException e){}
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(context,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location l) {
        location = l;
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
        mGoogleApiClient.connect();
    }
    public void onStop(){
        mGoogleApiClient.disconnect();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setExpirationDuration(10000);
    }

    public Location getLocation(){
        if (location != null) return location;
        else {
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            }
            catch (SecurityException e) {}
        }
        if (location != null) return location;
        showToast("Problem getting location try again.");
        return null;
    }
}

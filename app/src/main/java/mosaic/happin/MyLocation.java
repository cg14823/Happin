package mosaic.happin;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Carlos on 05/02/2016.
 */
public class MyLocation implements
        ConnectionCallbacks, OnConnectionFailedListener {
    Location location;
    boolean gpsEnabled = false;
    boolean networkEnabled = false;
    boolean locationUpdated = false;
    GoogleApiClient mGoogleApiClient;
    Context context;

    public MyLocation(Context context){
        this.context = context;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (location != null) {
                locationUpdated = true;
            }
            else{
                showToast("FUUUUUUCK!");
            }


        }
        catch (SecurityException e){}
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(context,
                message, Toast.LENGTH_SHORT);
        toast.show();
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

}

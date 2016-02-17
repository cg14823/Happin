package mosaic.happin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.location.LocationListener;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;

/*Things that need to be worked in next iteration 2:
 *Password recovery email
 *Firstly need to make sure to places are not submitted twice.
 *Work on getting a better respond time on location retrival. (Maybe inverting order of calls or using another API).
 *Created a location calss to migrate all location stuff there.
 *Better way of storing the images in the server
 *Converting a string into an image
 *Displaying added places in the profile
 * Create user-places table to find places added/liked by users fast*/

/*For iteration 3:
* Add liking system
* Add ranking system
* Display liked places in the profile.
* */

/*For iteration 4:
* Commenting system
* Verification email
* Minimum password requirements
* */

/*For iteration 5:
* Different screen compatibility
* */


/* NEW APPROACH FOR LOCATION*/

public class MainActivity extends AppCompatActivity{

    private FragmentTabHost mTabHost;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private View dialogView;
    public static String userId;
    LocationManager manager;
    Firebase myFirebaseRef;
    MyLocation locationClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClass = new MyLocation(this);
        locationClass.onStart();

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/");
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");

        //add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // title of toolbar in verdana bold as required by Happy City
        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/verdanab.ttf");
        title.setTypeface(custom_font);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // create FragmentTabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        // add 4 tabs
        mTabHost.addTab(
                mTabHost.newTabSpec("Map").setIndicator("Map", null),
                Map.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Ranking").setIndicator("Ranking", null),
                Ranking.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Game").setIndicator("Game", null),
                Game.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Profile").setIndicator("Profile", null),
                Profile.class, null);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                break;
            case R.id.addbutton:
                //gets Location first.
                getLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocation(){
        // Check if GPS active
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // If Location disable create a alert dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Location is disabled in your device. Would you like to" +
                    " enable it? It is required to add a place.")
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
        else addPlace();
    }

    private boolean addPlace(){
        //Creates dialog to input place detail
        Location location = locationClass.getLocation();
        if (location != null) {

            final LatLng placeloc = new LatLng (location.getLatitude(),location.getLongitude());
            LayoutInflater inflater = this.getLayoutInflater();
            final AlertDialog.Builder recPassDialog = new AlertDialog.Builder(this);
            final View dialogView = (inflater.inflate(R.layout.dialog_add_place, null));
            recPassDialog.setView(dialogView);
            EditText locfield = (EditText) dialogView.findViewById(R.id.location);
            locfield.setText("Location:" + location.getLatitude() + ","
                            + location.getLongitude()
            );

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
                            description.getText().toString(), imageFile, userId);
                    //pushes to database with new unique id
                    myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places/");
                    myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            myFirebaseRef.push().setValue(place);
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
            return true;
        }
        return false;

    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void addImage(View view){
        dialogView = view;
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView imageView =(ImageView) dialogView.findViewById(R.id.placeImg);
            if (imageView == null) showToast("problem with null pointers in imageView");
            else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            }
        }
    }
    @Override
    public void onStop(){
        if (locationClass != null) locationClass.onStop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page, menu);
        return true;
    }
}


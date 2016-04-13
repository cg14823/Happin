package mosaic.happin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;

public class ShowPlacesDetail extends AppCompatActivity {
    private Place place;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_places_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // title of toolbar in verdana bold as required by Happy City
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/verdanab.ttf");
        title.setTypeface(custom_font);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String url = i.getStringExtra("ref");
        userId = i.getStringExtra("USER_ID");

        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase(url);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                place = dataSnapshot.getValue(Place.class);
                if ((place != null)) {
                    addDetails();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                showToast(error.getMessage());
            }
        });
    }

    private void addDetails(){
        TextView text = (TextView)findViewById(R.id.placeText);
        ImageView imgView = (ImageView) findViewById(R.id.placeImgview);
        if(place.getLikes()==1){
            String description = "<font color=#00000><b>"+place.getName()+"</b></font><br> <font color=#2088ca>"+ place.getLikes() + " like" + "</font><br> <i>" + place.getDescription() +"</i>";
            text.setText(Html.fromHtml(description));
        }
        else {
            String description = "<font color=#00000><b>" + place.getName() + "</b></font><br> <font color=#2088ca>" + place.getLikes() + " likes" + "</font><br> <i>" + place.getDescription() + "</i>";
            text.setText(Html.fromHtml(description));
        }
        byte[] decodedString = Base64.decode(place.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgView.setImageBitmap(decodedByte);
        Firebase ref = new Firebase( "https://flickering-torch-2192.firebaseio.com/comments/"
                +place.latLng2Id(place.getLat(), place.getLon()));
        final TextView vcomments = (TextView) findViewById(R.id.commentSection);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    final Comments post = postSnapshot.getValue(Comments.class);
                    Firebase usernameref = new Firebase("https://flickering-torch-2192.firebaseio.com/users/"+post.getUser()+"/name");
                    usernameref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            String text = "<font color=#3aada5><b>"+name+"</b></font> <font color=#000000>"+post.getComment()+"</font><br>";
                            vcomments.append(Html.fromHtml(text));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void comment(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final AlertDialog.Builder commentBox = new AlertDialog.Builder(this);
        final View dialogView = (inflater.inflate(R.layout.dialog_write_comment, null));
        commentBox.setView(dialogView);
        commentBox.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText text = (EditText) dialogView.findViewById(R.id.writecomment);
                final String comment = text.getText().toString();
                final Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/comments/"
                        + place.latLng2Id(place.getLat(), place.getLon()));
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Comments newComment = new Comments(comment,userId ,System.currentTimeMillis());
                        ref.push().setValue(newComment);
                    }

                    @Override
                    public void onCancelled (FirebaseError firebaseError) {

                    }
                });

            }
        });
        commentBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = commentBox.create();
        alert.show();
    }

    public void liked (View view){
        Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/likes/"+userId+"/"
                +place.latLng2Id(place.getLat(), place.getLon()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    showToast("<3");
                    place.addLike();
                    TextView text = (TextView) findViewById(R.id.placeText);
                    String description = "<font color=#00000><b>"+place.getName()+"</b></font><br> <font color=#2088ca>"+ place.getLikes() + " likes" + "</font><br> <i>" + place.getDescription() +"</i><br>";
                    text.setText(Html.fromHtml(description));
                    Firebase fref = new Firebase("https://flickering-torch-2192.firebaseio.com/likes/"
                            + userId);
                    fref.child((place.latLng2Id())).setValue(ServerValue.TIMESTAMP);
                    fref = new Firebase("https://flickering-torch-2192.firebaseio.com/places/"
                            + place.latLng2Id(place.getLat(), place.getLon()));
                    java.util.Map<String, Object> likes = new HashMap<>();
                    likes.put("likes", place.getLikes());
                    fref.updateChildren(likes);
                } else {
                    showToast("</3"); //en el futuro: unlike on second press of like button
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void viewimage(View view){
        String ref = "https://flickering-torch-2192.firebaseio.com/places/"
                +place.latLng2Id(place.getLat(), place.getLon())+"/img";
        Intent showImagebig = new Intent(this, showImage.class);
        showImagebig.putExtra("REF",ref);
        showImagebig.putExtra("TITLE",place.getName());
        startActivity(showImagebig);

    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

}

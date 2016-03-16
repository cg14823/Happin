package mosaic.happin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class showImage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        Firebase.setAndroidContext(this);

        Intent i = getIntent();

        String ref = i.getStringExtra("REF");
        String title = i.getStringExtra("TITLE");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleTextView = (TextView) toolbar.findViewById(R.id.showImageTitle);
        titleTextView.setText(title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/verdanab.ttf");
        titleTextView.setTypeface(custom_font);

        Firebase fireRef = new Firebase(ref);
        fireRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView view = (ImageView) findViewById(R.id.bigImage);
                String image = dataSnapshot.getValue(String.class);

                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                view.setImageBitmap(decodedByte);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                showToast(firebaseError.getMessage());
            }
        });


    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
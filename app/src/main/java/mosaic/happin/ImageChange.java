package mosaic.happin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageChange extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_IMAGE = 2;
    private String uid;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_change);
        Firebase.setAndroidContext(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        uid = i.getStringExtra("USER_ID");
        ImageView view = (ImageView) findViewById(R.id.changePicImage);
        mAttacher = new PhotoViewAttacher(view);
        mAttacher.setScaleType(ScaleType.CENTER_CROP);
        setProfile();
    }

    private void setProfile() {
        // Retrieve Name from database
        final TextView nameField = (TextView) findViewById(R.id.changePicName);
        final ImageView profilePicView = (ImageView) findViewById(R.id.changePicImage);
        Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/users/" + uid + "/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    nameField.setText(user.getName());
                    String image = user.getProfileImage();
                    if (image.equals("null Image"))
                        profilePicView.setImageResource(R.drawable.empty_profile);
                    else {
                        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profilePicView.setImageBitmap(decodedByte);
                    }
                    mAttacher.update();
                } else
                    showToast("ERROR!");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void changeImage(View view) {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                } else if (items[item].equals("Choose from Library")) {
                    Intent galleryIntent = Intent.createChooser(
                            new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                            "Choose an image");
                    startActivityForResult(galleryIntent, SELECT_IMAGE);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Boolean success = false;
        String image = " ";
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView imageView = (ImageView) findViewById(R.id.changePicImage);
            if (imageView == null) showToast("problem with null pointers in imageView");
            else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                mAttacher.update();

                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                byte[] byteArray = bYtE.toByteArray();
                image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                success = true;
            }
        }
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            ImageView imageView = (ImageView) findViewById(R.id.changePicImage);
            if (imageView == null) showToast("problem with null pointers in imageView");
            else {
                // Let's read picked image path using ParcelFileDescriptor
                Uri pickedImage = data.getData();
                if (pickedImage != null) {
                    ParcelFileDescriptor parcelFileDescriptor = null;
                    try {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(pickedImage, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        imageView.setImageBitmap(bitmap);
                        mAttacher.update();

                        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                        byte[] byteArray = bYtE.toByteArray();
                        image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        success = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (parcelFileDescriptor != null)
                                parcelFileDescriptor.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        if (success) {
            Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/users/" + uid
                    + "/profileImage");
            ref.setValue(image);
            showToast("Image changed!");
        } else showToast("Oops! Something went wrong");
    }

    public void viewImage(View view) {
        String ref = "https://flickering-torch-2192.firebaseio.com/users/" +
                uid + "/profileImage";
        String name = ((TextView) findViewById(R.id.changePicName)).getText().toString();
        Intent showImagebig = new Intent(this, showImage.class);
        showImagebig.putExtra("REF", ref);
        showImagebig.putExtra("TITLE", name);
        startActivity(showImagebig);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

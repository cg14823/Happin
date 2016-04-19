
package mosaic.happin;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class Profile extends Fragment {
    private Firebase myFirebaseRef;
    private User user;
    private final int image_width = 400;
    private final int image_height = 350;
    String userId;
    View profileView;
    private ArrayList<Place> likedPlaces = new ArrayList<Place>();
    private ArrayList<Place> addedPlaces = new ArrayList<Place>();
    private ArrayList<Bitmap> imagesOflikedPlaces = new ArrayList<>();
    private ArrayList<Bitmap> imagesOfAddedPlaces = new ArrayList<>();
    ImageAdapter imageAdapter;
    //the images to display

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        profileView = inflater.inflate(R.layout.fragment_profile_temp, container, false);
        userId = MainActivity.userId;
        imageAdapter = new ImageAdapter(getContext());
        showToast(userId);
        //new PopulateUser().execute();
        //new PopulatePlaces().execute();
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/users/" + userId);
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                showToast("the user name is(1): " + user.getName());
                if (user == null) {
                    showToast("the user name is(2): " + "user is null");
                } else {
                    TextView userName = (TextView) profileView.findViewById(R.id.user_name);
                    showToast("the user name is(2): " + user.getName());
                    userName.setText(user.getName());
                    TextView userPoints = (TextView) profileView.findViewById(R.id.user_points);
                    userPoints.setText(user.getPoints() + " Points");
                    ImageView profile_picture = (ImageView) profileView.findViewById(R.id.profile_picture);
                    String profileImage = user.getProfileImage();
                    if (profileImage == null) {
                        profile_picture.setImageResource(R.drawable.empty_profile);
                    } else {
                        profile_picture.setImageBitmap(StringToBitMap(profileImage));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Gallery gallery1 = (Gallery) profileView.findViewById(R.id.gallery1);
        final ImageAdapter adapterToLikedPlaces = new ImageAdapter(getContext(), imagesOflikedPlaces);
        gallery1.setAdapter(adapterToLikedPlaces);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gallery1.getLayoutParams();
        mlp.setMargins(-(metrics.widthPixels / 2 + image_width - 90),
                mlp.topMargin,
                mlp.rightMargin,
                mlp.bottomMargin
        );
        gallery1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Place currentPlace = likedPlaces.get(position);
                Intent detailShow = new Intent(getContext(), ShowPlacesDetail.class);
                detailShow.putExtra("ref", "https://flickering-torch-2192.firebaseio.com/places/" + currentPlace.latLng2Id());
                detailShow.putExtra("USER_ID", MainActivity.userId);
                startActivity(detailShow);
            }
        });

        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/likes/" + userId);
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    //showToast(child.getKey());
                    myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
                    myFirebaseRef.child(child.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                Place currentPlace = dataSnapshot.getValue(Place.class);
                                if (currentPlace != null && likedPlaces.contains(currentPlace) == false) {
                                    showToast(currentPlace.getName());
                                    likedPlaces.add(currentPlace);
                                    imagesOflikedPlaces.add(StringToBitMap(currentPlace.getImg()));
                                    //showToast("Likes places: " + likedPlaces.toString());
                                    adapterToLikedPlaces.notifyDataSetChanged();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                ImageView header_ImageView = (ImageView) profileView.findViewById(R.id.header_imageview);
                if (likedPlaces.isEmpty() == false) {
                    showToast("postExecute PLACES: number of places is" + likedPlaces.size());
                    header_ImageView.setImageBitmap(StringToBitMap(likedPlaces.get(0).getImg()));
                } else {
                    showToast("PLACES EMPTY");
                    header_ImageView.setImageResource(R.drawable.bristol);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });


        Gallery gallery2 = (Gallery) profileView.findViewById(R.id.gallery2);
        final ImageAdapter adapterToAddedPlaces = new ImageAdapter(this.getContext(), imagesOfAddedPlaces);
        gallery2.setAdapter(adapterToAddedPlaces);
        ViewGroup.MarginLayoutParams mlp2 = (ViewGroup.MarginLayoutParams) gallery2.getLayoutParams();
        mlp2.setMargins(-(metrics.widthPixels / 2 + image_width - 90),
                mlp2.topMargin,
                mlp2.rightMargin,
                mlp2.bottomMargin
        );
        gallery2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Place currentPlace = addedPlaces.get(position);
                Intent detailShow = new Intent(getContext(), ShowPlacesDetail.class);
                detailShow.putExtra("ref", "https://flickering-torch-2192.firebaseio.com/places/" + currentPlace.latLng2Id());
                detailShow.putExtra("USER_ID", MainActivity.userId);
                startActivity(detailShow);
            }
        });
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places");
        myFirebaseRef.orderByChild("user").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Place currentPlace = child.getValue(Place.class);
                        if (currentPlace != null && addedPlaces.contains(currentPlace) == false) {
                            showToast("ADDED PLACE=" + currentPlace.getName());
                            addedPlaces.add(currentPlace);
                            imagesOfAddedPlaces.add(StringToBitMap(currentPlace.getImg()));
                            adapterToAddedPlaces.notifyDataSetChanged();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return profileView;
    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        private ArrayList<Bitmap> arrayList = new ArrayList<>();

        public ImageAdapter(Context c) {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a = c.obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }

        public ImageAdapter(Context c, ArrayList<Bitmap> arrayList) {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a = c.obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
            this.arrayList = (ArrayList<Bitmap>) arrayList.clone();

        }

        // returns the number of images
        public int getCount() {
            return imagesOflikedPlaces.size();
        }

        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }

        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }

        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView = new ImageView(context);
            if (position < arrayList.size()) {
                imageView.setImageBitmap(arrayList.get(position));
            }
            imageView.setLayoutParams(new Gallery.LayoutParams(image_width, image_height));
            imageView.setBackgroundResource(itemBackground);
            return imageView;


        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

}

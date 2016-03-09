package mosaic.happin;


import android.content.Context;
import android.graphics.Typeface;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private List<Place> places =new ArrayList<>();
    String userId;
    View profileView;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        userId = MainActivity.userId;
        setProfile(profileView);
        setButtons();
        getLiked();
        return profileView;

    }
    private void getLiked(){
        Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com/likes/"+userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> placesId = new ArrayList<String>();
                final long num = dataSnapshot.getChildrenCount();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    placesId.add(d.getKey());
                }
                if (!placesId.isEmpty()) {
                    for (String s : placesId) {
                        Firebase ref1 = new Firebase("https://flickering-torch-2192.firebaseio.com/places/"
                                + s);
                        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Place p = dataSnapshot.getValue(Place.class);
                                places.add(p);
                                if (places.size() == num) setGrid();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setButtons(){
        RadioButton ypButton = (RadioButton) profileView.findViewById(R.id.ypButton);
        ypButton.setTextColor(getResources().getColor(R.color.grey));
        RadioButton lpButton = (RadioButton) profileView.findViewById(R.id.lpButton);
        lpButton.setTextColor(getResources().getColor(R.color.tabTitleColor));
        lpButton.setTypeface(null, Typeface.BOLD);
        lpButton.setChecked(true);
        RadioGroup group = (RadioGroup) profileView.findViewById(R.id.radialGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    checkedRadioButton.setTextColor(getResources().getColor(R.color.tabTitleColor));
                    checkedRadioButton.setTypeface(null, Typeface.BOLD);
                    if (R.id.lpButton == checkedId){
                        RadioButton ypButton = (RadioButton) profileView.findViewById(R.id.ypButton);
                        ypButton.setTextColor(getResources().getColor(R.color.grey));
                        ypButton.setTypeface(null, Typeface.NORMAL);
                    }
                    else{
                        RadioButton lpButton = (RadioButton) profileView.findViewById(R.id.lpButton);
                        lpButton.setTextColor(getResources().getColor(R.color.grey));
                        lpButton.setTypeface(null, Typeface.NORMAL);
                    }
                }
                else{
                    checkedRadioButton.setTextColor(getResources().getColor(R.color.grey));
                    checkedRadioButton.setTypeface(null, Typeface.NORMAL);
                    if (R.id.lpButton == checkedId){
                        RadioButton ypButton = (RadioButton) profileView.findViewById(R.id.ypButton);
                        ypButton.setTextColor(getResources().getColor(R.color.tabTitleColor));
                        ypButton.setTypeface(null, Typeface.BOLD);
                    }
                    else{
                        RadioButton lpButton = (RadioButton) profileView.findViewById(R.id.lpButton);
                        lpButton.setTextColor(getResources().getColor(R.color.tabTitleColor));
                        lpButton.setTypeface(null, Typeface.BOLD);
                    }
                }
            }
        });
    }

    private void setGrid(){
        ArrayAdapter<Place> adapter = new CustomAdapter(places);
        GridView grid = (GridView) profileView.findViewById(R.id.profileGrid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Place p = places.get(position);
                String ref = "https://flickering-torch-2192.firebaseio.com/places/"
                        +p.latLng2Id(p.getLat(),p.getLon());
                showDetails(ref);

            }
        });
    }

    private void showDetails(String ref){
        Intent detailShow = new Intent(getContext(), ShowPlacesDetail.class);
        detailShow.putExtra("ref", ref);
        detailShow.putExtra("USER_ID",userId);
        startActivity(detailShow);
    }

    private void setProfile(View view){
        // Retrive Name from database
        final TextView nameField = (TextView) view.findViewById(R.id.details);
        final TextView points = (TextView) view.findViewById(R.id.points);
        final ImageView profilePicView = (ImageView) view.findViewById(R.id.profilePic);
        Firebase myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/users/"+userId+"/");
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    nameField.setText(user.getName());
                    points.setText("Points:" + user.getPoints());
                    String image = user.getProfileImage();
                    if (image.equals("null Image"))
                        profilePicView.setImageResource(R.drawable.empty_profile);
                    else {
                        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profilePicView.setImageBitmap(decodedByte);
                    }
                } else
                    showToast("ERROR!");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }


    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class CustomAdapter extends ArrayAdapter<Place>{

        public CustomAdapter(List<Place> places) {
            super(getActivity().getApplicationContext(), R.layout.profileplacepreview, places);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView== null){
                itemView=getActivity().getLayoutInflater().inflate(R.layout.profileplacepreview,parent,false);
            }
            Place currentPlace = places.get(position);
            ImageView img = (ImageView) itemView.findViewById(R.id.placePreviewImage);
            TextView placeName = (TextView) itemView.findViewById(R.id.placePreviewName);
            TextView placeID = (TextView) itemView.findViewById(R.id.previewId);
            placeName.setText(currentPlace.getName());
            placeID.setText(currentPlace.latLng2Id(currentPlace.getLat(),currentPlace.getLon()));
            byte[] decodedString = Base64.decode(currentPlace.getImg(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            img.setImageBitmap(decodedByte);
            return itemView;
        }
    }


}

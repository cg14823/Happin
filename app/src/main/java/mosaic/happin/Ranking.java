package mosaic.happin;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import android.widget.AdapterView.OnItemSelectedListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Ranking extends Fragment {

    private ArrayList<PlaceorUser> poru;
    private MyCustomAdapter adapter;
    private View rankingView;
    private int current_filter;

    public Ranking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Firebase.setAndroidContext(getContext());
        rankingView = inflater.inflate(R.layout.fragment_ranking, container, false);

        current_filter = 0;
        poru = new ArrayList<>();

        ListView rankingList = (ListView) rankingView.findViewById(R.id.rankingList);
        rankingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (current_filter == 0) {
                    PlaceorUser p = poru.get(position);

                    if (p.poru == 0) {
                        Intent showPlaceDetails = new Intent(getContext(), ShowPlacesDetail.class);
                        showPlaceDetails.putExtra("ref", "https://flickering-torch-2192.firebaseio.com/places/" + p.p.latLng2Id());
                        showPlaceDetails.putExtra("USER_ID", MainActivity.userId);
                        startActivity(showPlaceDetails);
                    }
                }
            }
        });
        setSpinner();
        getRanked();

        return rankingView;
    }

    private void setSpinner() {
        Spinner spinner = (Spinner) rankingView.findViewById(R.id.spinnerRanking);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.Place_or_Person, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                if (current_filter == 1) {
                    if (parent.getItemAtPosition(pos).equals("Places")) {
                        current_filter = 0;
                        getRanked();
                    }
                } else {
                    if (parent.getItemAtPosition(pos).equals("People")) {
                        current_filter = 1;
                        getRanked();
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }

    private void getRankedTesting(){
        /* To avoid server delay this function is used instead of the server retrieval one
         *which has already been tested and proven to work.*/
        poru = new ArrayList<>();
        if (current_filter == 0){
            Place p0 = new Place(2,5,"Test 1","Test 1 description","null Image", "some user");
            Place p1 = new Place(2.2,5.1,"Test 2","Test 2 description","null Image", "some user");
            Place p2 = new Place(2.5,5.5,"Test 3","Test 3 description","null Image", "some user");
            Place p3 = new Place(2.0,5.1,"Test 4","Test 4 description","null Image", "some user");
            p0.addLike();
            p0.addLike();
            p1.addLike();
            p3.addLike();
            p3.addLike();
            p3.addLike();

            poru.add(new PlaceorUser(p0));
            poru.add(new PlaceorUser(p1));
            poru.add(new PlaceorUser(p2));
            poru.add(new PlaceorUser(p3));

        }
        else{
            User u0 = new User ("U0","u@g.com","password","null Image");
            User u1 = new User ("U1","u@g.com","password","null Image");
            User u2 = new User ("U2","u@g.com","password","null Image");
            User u3 = new User ("U3","u@g.com","password","null Image");
            u0.incrementPoints(100);
            u1.incrementPoints(1000);
            u2.incrementPoints(700);

            poru.add(new PlaceorUser(u0));
            poru.add(new PlaceorUser(u1));
            poru.add(new PlaceorUser(u2));
            poru.add(new PlaceorUser(u3));
        }
        setList();
    }

    private void getRanked() {
        ProgressBar loading = (ProgressBar) rankingView.findViewById(R.id.progressBar1);
        loading.setVisibility(View.VISIBLE);
        poru = new ArrayList<>();
        String ref = "https://flickering-torch-2192.firebaseio.com/places/";
        String child = "likes";
        if (current_filter == 1) {
            ref = "https://flickering-torch-2192.firebaseio.com/users/";
            child = "points";
        }
        final Firebase fireRef = new Firebase(ref);
        Query queryRef = fireRef.orderByChild(child);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final long count = dataSnapshot.getChildrenCount();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        fireRef.child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (current_filter == 0) {
                                        Place p = dataSnapshot.getValue(Place.class);
                                        PlaceorUser pu = new PlaceorUser(p);
                                        poru.add(pu);
                                    } else {
                                        User u = dataSnapshot.getValue(User.class);
                                        PlaceorUser up = new PlaceorUser(u);
                                        poru.add(up);
                                    }
                                    if (poru.size() >= count) {
                                        setList();
                                    }
                                }
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

    private void setList() {
        Collections.reverse(poru);
        adapter = new MyCustomAdapter(getContext(),poru);
        ProgressBar loading = (ProgressBar) rankingView.findViewById(R.id.progressBar1);
        loading.setVisibility(View.GONE);
        ListView rankingList = (ListView) rankingView.findViewById(R.id.rankingList);
        rankingList.setAdapter(adapter);
    }


    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class PlaceorUser {
        public Place p;
        public User u;
        public int poru;

        public PlaceorUser(Place p) {
            this.p = p;
            poru = 0;
        }

        public PlaceorUser(User u) {
            this.u = u;
            poru = 1;
        }
    }

    public class MyCustomAdapter extends BaseAdapter {


        // Tag for Logging
        private static final String TAG = "MyCustomAdapter";

        int type;
        private static final int PLACE = 0;
        private static final int PERSON = 1;


        private ArrayList<PlaceorUser> placeorUsers;
        private LayoutInflater mInflater;


        private Context context;

        public MyCustomAdapter(Context context, ArrayList<PlaceorUser> placeoruser) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;

            this.placeorUsers = placeoruser;
        }

        @Override
        public Object getItem(int position){
            if (placeorUsers.size()> position){
                return placeorUsers.get(position);
            }
            else return null;
        }

        @Override
        public int getCount() {
            return placeorUsers.size();
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View slotView = convertView;
            if (convertView == null){
                PlaceorUser p = placeorUsers.get(position);
                if (p.poru == PERSON){
                    slotView = mInflater.inflate(R.layout.ranking_user,null);
                    TextView rankNum = (TextView) slotView.findViewById(R.id.rank_num_user);
                    TextView userName = (TextView) slotView.findViewById(R.id.user_name_rank);
                    TextView userPoints = (TextView) slotView.findViewById(R.id.user_points);
                    ImageView profileImage = (ImageView) slotView.findViewById(R.id.user_imageView_rank);

                    rankNum.setText(String.valueOf(position + 1));
                    userName.setText(p.u.getName());
                    userPoints.setText(String.valueOf(p.u.getPoints()));

                    if (p.u.getProfileImage().equals("null Image")){
                        profileImage.setImageResource(R.drawable.empty_profile);
                    }
                    else{
                        profileImage.setImageBitmap(String2Image(p.u.getProfileImage()));
                    }
                }
                else{
                    slotView = mInflater.inflate(R.layout.ranking_item_view,null);
                    TextView rankNum = (TextView) slotView.findViewById(R.id.place_ranking);
                    TextView placeName = (TextView) slotView.findViewById(R.id.item_place_name_rank);
                    TextView placeLikes = (TextView) slotView.findViewById(R.id.item_likes_rank);
                    ImageView placeImage = (ImageView) slotView.findViewById(R.id.item_imageView_rank);

                    rankNum.setText(String.valueOf(position + 1));
                    placeName.setText(p.p.getName());
                    placeLikes.setText(String.valueOf(p.p.getLikes()));
                    if (p.p.getImg().equals("null Image")){
                        placeImage.setImageResource(R.mipmap.emptyplace);
                    }
                    else placeImage.setImageBitmap(String2Image(p.p.getImg()));

                }
                return slotView;
            }

            return convertView;
        }

        private Bitmap String2Image (String image){
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

    }
}

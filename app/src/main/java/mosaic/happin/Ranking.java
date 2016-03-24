package mosaic.happin;


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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Ranking extends Fragment {

    private List<Place> places;
    private List<User> users;
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

        places = new ArrayList<>();
        users = new ArrayList<>();

        getTop();

        Spinner spinner = (Spinner) rankingView.findViewById(R.id.spinnerRanking);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(),R.array.Place_or_Person,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showToast("Spinner change");
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals("Places")) {
                    if (current_filter == 1){
                        current_filter = 0;
                        getTop();
                    }
                }
                else{
                    if (current_filter == 0){
                        current_filter = 1;
                        getTop();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rankingView;
    }

    private void setList (){
        ListView rankingList = (ListView) rankingView.findViewById(R.id.rankingList);
        rankingList.setAdapter(null);

        if (current_filter == 0){
            ArrayAdapter<Place> adapter = new myListPlaceAdapter(places);
            rankingList.setAdapter(adapter);
        }
        else{
            ArrayAdapter<User> adapter = new myListUserAdapter(users);
            rankingList.setAdapter(adapter);
        }

    }

    private void getTop(){
        String fireRefStr = "https://flickering-torch-2192.firebaseio.com/places";
        String child = "likes";

        if (current_filter == 1){
            fireRefStr = "https://flickering-torch-2192.firebaseio.com/users";
            child = "points";
        }

        Firebase ref = new Firebase(fireRefStr);
        Query query = ref.orderByChild(child);
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showToast("PREPARE LOADING");
                    if (current_filter == 0) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Place p = d.getValue(Place.class);
                            places.add(p);
                            showToast("LOADING");
                        }
                    } else {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String uid = d.getKey();
                            Firebase ref2 = new Firebase("https://flickering-torch-2192.firebaseio.com/users/" + uid);
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User s = dataSnapshot.getValue(User.class);
                                    users.add(s);
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                            showToast("LOADING");                        }
                    }
                    Collections.reverse(places);
                    Collections.reverse(users);
                    setList();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class myListPlaceAdapter extends ArrayAdapter<Place> {

        public myListPlaceAdapter(List<Place> places) {
            super(getActivity().getApplicationContext(), R.layout.ranking_item_view, places);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView== null){
                itemView=getActivity().getLayoutInflater().inflate(R.layout.ranking_item_view,parent,false);
            }
            Place currentPlace= places.get(position);
            //Set the image of the button
            ImageView imageViewe = (ImageView) itemView.findViewById(R.id.item_imageView_rank);
            imageViewe.setImageBitmap(this.StringToBitMap(currentPlace.getImg()));
            //Set the name of the place
            TextView placeName= (TextView) itemView.findViewById(R.id.item_place_name_rank);
            placeName.setText(currentPlace.getName());
            //Set the number of likes
            TextView numOfLikes= (TextView) itemView.findViewById(R.id.item_likes_rank);
            numOfLikes.setText(Integer.toString(currentPlace.getLikes()));

            TextView placeRank = (TextView) itemView.findViewById(R.id.place_ranking);
            String rankStr = Integer.toString(position +1)+".";
            placeRank.setText(rankStr);


            return itemView;
        }

        public Bitmap StringToBitMap(String encodedString){
            try {
                byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch(Exception e) {
                e.getMessage();
                return null;
            }
        }
    }

    private class myListUserAdapter extends ArrayAdapter<User> {

        public myListUserAdapter(List<User> users) {
            super(getActivity().getApplicationContext(), R.layout.ranking_user, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView== null){
                itemView=getActivity().getLayoutInflater().inflate(R.layout.ranking_user,parent,false);
            }
            User cUser= users.get(position);
            //Set the image of the button
            ImageView imageViewe = (ImageView) itemView.findViewById(R.id.user_imageView_rank);
            String userProfilPic = cUser.getProfileImage();
            if (userProfilPic.equals("null Image")) imageViewe.setImageResource(R.drawable.empty_profile);
            else imageViewe.setImageBitmap(this.StringToBitMap(userProfilPic));
            //Set the name of the place
            TextView name= (TextView) itemView.findViewById(R.id.user_name_rank);
            name.setText(cUser.getName());
            //Set the number of likes
            TextView points= (TextView) itemView.findViewById(R.id.user_points);
            points.setText(Integer.toString(cUser.getPoints()));

            TextView placeRank = (TextView) itemView.findViewById(R.id.rank_num_user);
            String rankStr = Integer.toString(position +1)+".";
            placeRank.setText(rankStr);


            return itemView;
        }

        public Bitmap StringToBitMap(String encodedString){
            try {
                byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch(Exception e) {
                e.getMessage();
                return null;
            }
        }
    }
}

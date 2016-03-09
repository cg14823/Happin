package mosaic.happin;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by haniboudabous on 02/03/16.
 */
public class SearchResultsActivity extends Activity {
    Firebase myFirebaseRef;
    private List<Place> places=new ArrayList<Place>();
    private void showToast(String message) {
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search_list);
        handleIntent(getIntent());
        registerClickCallback();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    private void handleIntent(Intent intent) {
        /* places.add(new Place(0,0,"Hawai","magnifique","iVBORw0KGgoAAAANSUhEUgAAAoAAAARwCAIAAAD17XJXAAAAA3NCSVQICAjb4U","User1").setLikes(5));
        places.add(new Place(0,0,"Miami","magnifique","iVBORw0KGgoAAAANSUhEUgAAAoAAAARwCAIAAAD17XJXAAAAA3NCSVQICAjb4U","User1").setLikes(2));
        PopulateListView();*/
       if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY).toUpperCase();
            myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places/");
            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Place currentPlace= child.getValue(Place.class);
                        String currentName= currentPlace.getName().toUpperCase();
                        String description= currentPlace.getDescription().toUpperCase();
                        if ((currentName.contains(query))|| (query.contains(currentName))|| (description.contains(query))){
                            places.add(currentPlace);
                        }
                        PopulateListView();
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }

    private void PopulateListView(){
        ArrayAdapter<Place> adapter = new myListAdapter(places);
        ListView listFoundPlaces= (ListView) findViewById(R.id.places_listView);
        listFoundPlaces.setAdapter(adapter);
    }
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.places_listView);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                Place currentPlace = places.get(position);
                LatLng position_current_place= new LatLng(currentPlace.getLat(), currentPlace.getLon());
                Intent detailShow = new Intent(getApplicationContext(), ShowPlacesDetail.class);
                detailShow.putExtra("ref", "https://flickering-torch-2192.firebaseio.com/places/"+Map.latLng2Id(position_current_place));
                detailShow.putExtra("USER_ID",MainActivity.userId);
                startActivity(detailShow);

            }
        });
    }

    private class myListAdapter extends ArrayAdapter<Place> {

        public myListAdapter(List<Place> places) {
            super(SearchResultsActivity.this, R.layout.item_view, places);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView== null){
                itemView=getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }
            Place currentPlace= places.get(position);
            //Set the image of the button
            ImageView imageViewe = (ImageView) itemView.findViewById(R.id.item_imageView);
            imageViewe.setImageBitmap(this.StringToBitMap(currentPlace.getImg()));
            //Set the name of the place
            TextView placeName= (TextView) itemView.findViewById(R.id.item_place_name);
            placeName.setText(currentPlace.getName());
            //Set the rating
            RatingBar ratingBar= (RatingBar) itemView.findViewById(R.id.item_ratingBar);
            ratingBar.setNumStars(currentPlace.comupteScore());
            //Set the number of likes
            TextView numOfLikes= (TextView) itemView.findViewById(R.id.item_likes);
            numOfLikes.setText(Integer.toString(currentPlace.getLikes())+" Likes");
            return itemView;
        }

        public  Bitmap StringToBitMap(String encodedString){
            try {
                byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch(Exception e) {
                e.getMessage();
                return null;
            }
        }
    }
}

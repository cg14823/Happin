package mosaic.happin;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by haniboudabous on 02/03/16.
 */
public class SearchResultsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Firebase myFirebaseRef;
    private Spinner spinner;
    private List<Place> places=new ArrayList<Place>();
    private String current_filter;
    MyLocation locationClass;
    private ArrayAdapter<Place> adapter;

    private void showToast(String message) {
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        current_filter=getString(R.string.defaultFilter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listPlaces= (ListView) findViewById(R.id.places_listView);
        adapter = new myListAdapter(places);
        listPlaces.setAdapter(adapter);

        spinner = (Spinner) findViewById(R.id.spinner_sort);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Sort_places_by,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(SearchResultsActivity.this);

        findPlaces(getIntent());
        registerClickCallback();
        setIntent(getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        places.clear();
        setIntent(intent);
        findPlaces(intent);
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
        searchView.setSubmitButtonEnabled(true);
        return true;
    }

    private void findPlaces(Intent intent) {
       if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY).toUpperCase();
                    myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/places/");
                    myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Place currentPlace = child.getValue(Place.class);
                                String currentName = currentPlace.getName().toUpperCase();
                                String description = currentPlace.getDescription().toUpperCase();
                                if ((currentName.contains(query)) || (query.contains(currentName)) || (description.contains(query))) {
                                    places.add(currentPlace);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                                sort_places(current_filter);
                                            }
                            });
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                }
        }

    private void sort_places (String filter){
        if (filter.equals(getString(R.string.defaultFilter))||true){
            Collections.sort(places, new Comparator<Place>() {
                @Override
                public int compare(Place place1, Place place2) {
                    return Integer.compare(place2.getLikes(), place1.getLikes());
                }
            });
        }
        else{
            final Location myPosition= locationClass.getLocation();
            if(myPosition != null){
                Collections.sort(places, new Comparator<Place>() {
                    @Override
                    public int compare(Place place1, Place place2) {
                    float[] results1 = new float[1];
                    Location.distanceBetween(myPosition.getLatitude(), myPosition.getLongitude(),
                            place1.getLat(), place1.getLon(), results1);
                    float[] results2 = new float[1];
                    Location.distanceBetween(myPosition.getLatitude(), myPosition.getLongitude(),
                            place2.getLat(), place2.getLon(), results2);
                        return Float.compare(results2[0], results1[0]);
                    }
                });
            }
        }
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.places_listView);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                Place currentPlace = places.get(position);
                Intent detailShow = new Intent(getApplicationContext(), ShowPlacesDetail.class);
                detailShow.putExtra("ref", "https://flickering-torch-2192.firebaseio.com/places/" + currentPlace.latLng2Id());
                detailShow.putExtra("USER_ID", MainActivity.userId);
                startActivity(detailShow);
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView filter = (TextView) view;
            String textFilter = (String) filter.getText();
            current_filter=textFilter;
            sort_places(current_filter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

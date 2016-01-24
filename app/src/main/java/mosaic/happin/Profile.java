package mosaic.happin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        addLP(view);
        addYP(view);
        return view;

    }

    private void setProfile(View view){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        // get image name and age and put it in profile.
    }

    private void addLP(View view){
        ArrayList<Place> places = getLP();
        ViewGroup parent = (ViewGroup) view.findViewById(R.id.container);
        if (places.isEmpty()){
            TextView empty = new TextView(getContext());
            empty.setText("You haven't liked any places!");
            empty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            parent.addView(empty);
        }
    }
    private void addYP(View view){
        ArrayList<Place> places = getYP();
        ViewGroup parent = (ViewGroup) view.findViewById(R.id.container2);
        if (places.isEmpty()){
            TextView empty = new TextView(getContext());
            empty.setText("You haven't added any places!");
            empty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            parent.addView(empty);
        }
    }
    // Goes to the server and gets the Places you´ve liked
    private ArrayList<Place> getLP(){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        ArrayList<Place> places = new ArrayList<Place>();
        return places;
    }

    // Goes to the server and gets the Places you´ve added
    private ArrayList<Place> getYP(){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        ArrayList<Place> places = new ArrayList<Place>();
        return places;
    }

}

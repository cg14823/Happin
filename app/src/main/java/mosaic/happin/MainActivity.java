package mosaic.happin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*Aproach 2.0 Use FragmentTabHost instead of view pager and view adapter.
* Log 1: Espero que el mapa funcione por que sino voy a quemar mi jodida casa en un ataque de ira.
* Log 2: La ira me inunda he probado 4 combinaciones han pasado 3 horas. A ver si esta funciona
* Log 3: Utilizando MapView y FragmentTabHost parece funcionar!
* Log 4: Empezando a implementear la opcion para add lugares.
* Log 5: Created most fields for the place entry. Starting work on image adding
* Log 6: Failure to replace preset image for new image.
* Log 7 : Image now replaces the preset image but it gets a weird size ratio.*/

public class MainActivity extends AppCompatActivity {
    private FragmentTabHost mTabHost;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private View dialogView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        //add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // title of toolbar in verdana bold as required by Happy City
        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/verdanab.ttf");
        title.setTypeface(custom_font);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // create FragmentTabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        // add 4 tabs
        mTabHost.addTab(
                mTabHost.newTabSpec("Map").setIndicator("Map", null),
                Map.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Ranking").setIndicator("Ranking", null),
                Ranking.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Game").setIndicator("Game", null),
                Game.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Profile").setIndicator("Profile", null),
                Profile.class, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                break;
            case R.id.addbutton:
                if (!addPlace())
                    showToast("Could not add place");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addPlace(){
        LayoutInflater inflater = getLayoutInflater();
        // message for password recovery
        AlertDialog.Builder recPassDialog = new AlertDialog.Builder(this);
        recPassDialog.setView(inflater.inflate(R.layout.dialog_add_place, null));

        recPassDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //STUFF

            }
        });
        recPassDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = recPassDialog.create();
        alert.show();
        return false;
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void addImage(View view){
        dialogView = view;
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView imageView =(ImageView) dialogView.findViewById(R.id.placeImg);
            if (imageView == null) showToast("problem with null pointers in imageView");
            else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            }
        }
    }
}


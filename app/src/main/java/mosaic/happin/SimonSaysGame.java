package mosaic.happin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;

/**
 * Created by Tom on 07/04/2016.
 */
public class SimonSaysGame extends AppCompatActivity {
    private Button red;
    private Button yellow;
    private Button blue;
    private Button start;
    private Button replay;
    private int[] sequence;
    private TextView text;
    private int current;
    private Button one;
    private Button two;
    private Button three;
    private Button four;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simon_says_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // title of toolbar in verdana bold as required by Happy City
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/verdanab.ttf");
        title.setTypeface(custom_font);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent i = getIntent();
        user = i.getStringExtra("userId");
        red = (Button) findViewById(R.id.redButton);
        yellow = (Button) findViewById(R.id.yellowButton);
        blue = (Button) findViewById(R.id.blueButton);
        start = (Button) findViewById(R.id.startButton);
        replay = (Button) findViewById(R.id.replayButton);
        replay.setEnabled(false);
        red.setEnabled(false);
        blue.setEnabled(false);
        yellow.setEnabled(false);
        start.setEnabled(true);
        text = (TextView) findViewById(R.id.gameText);
        current = 0;
        one = (Button) findViewById(R.id.oneButton);
        two = (Button) findViewById(R.id.twoButton);
        three = (Button) findViewById(R.id.threeButton);
        four = (Button) findViewById(R.id.fourButton);
    }

    public void redClick(View view){
        red.setBackgroundColor(Color.parseColor("#ff0000"));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               red.setBackgroundColor(Color.parseColor("#b40000"));
            }
        },200);
        if (sequence[current] == 1){
            switch (current){
                case 0:one.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 1:two.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 2:three.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 3:four.setBackgroundColor(Color.parseColor("#00ff11")); break;
            }
            current++;
            if (current > 3){
                replay.setEnabled(false);
                red.setEnabled(false);
                yellow.setEnabled(false);
                blue.setEnabled(false);
                text.setText("Congratulations!");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("userId", user);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
        else {
            text.setText("Incorrect\nTry again");
            current = 0;
            one.setBackgroundColor(Color.parseColor("#ffffff"));
            two.setBackgroundColor(Color.parseColor("#ffffff"));
            three.setBackgroundColor(Color.parseColor("#ffffff"));
            four.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    public void yellowClick(View view){
        yellow.setBackgroundColor(Color.parseColor("#fdff00"));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                yellow.setBackgroundColor(Color.parseColor("#d7d800"));
            }
        },200);
        if (sequence[current] == 2){
            switch (current){
                case 0:one.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 1:two.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 2:three.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 3:four.setBackgroundColor(Color.parseColor("#00ff11")); break;
            }
            current++;
            if (current > 3){
                replay.setEnabled(false);
                red.setEnabled(false);
                yellow.setEnabled(false);
                blue.setEnabled(false);
                text.setText("Congratulations!");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("userId", user);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
        else {
            text.setText("Incorrect\nTry again");
            current = 0;
            one.setBackgroundColor(Color.parseColor("#ffffff"));
            two.setBackgroundColor(Color.parseColor("#ffffff"));
            three.setBackgroundColor(Color.parseColor("#ffffff"));
            four.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    public void blueClick(View view){
        blue.setBackgroundColor(Color.parseColor("#00d2ff"));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                blue.setBackgroundColor(Color.parseColor("#0790ae"));
            }
        },200);
        if (sequence[current] == 3){
            switch (current){
                case 0:one.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 1:two.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 2:three.setBackgroundColor(Color.parseColor("#00ff11")); break;
                case 3:four.setBackgroundColor(Color.parseColor("#00ff11")); break;
            }
            current++;
            if (current > 3){
                replay.setEnabled(false);
                red.setEnabled(false);
                yellow.setEnabled(false);
                blue.setEnabled(false);
                text.setText("Congratulations!");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("userId", user);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
        else {
            text.setText("Incorrect\nTry again");
            current = 0;
            one.setBackgroundColor(Color.parseColor("#ffffff"));
            two.setBackgroundColor(Color.parseColor("#ffffff"));
            three.setBackgroundColor(Color.parseColor("#ffffff"));
            four.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    public void started(View view){
        text.setText("Playing Sequence...");
        start.setEnabled(false);
        int colour;
        sequence = new int[4];
        for (int i=0;i<4;i++){
            colour = (int)((Math.random()*3)+1);
            sequence[i] = colour;
            Handler handler1 = new Handler();
            switch(colour){
                case 1: handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Handler handler = new Handler();
                                red.setBackgroundColor(Color.parseColor("#ff0000"));
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        red.setBackgroundColor(Color.parseColor("#b40000"));
                                    }
                                }, 1000);
                            }
                        }, (2000*i));
                        break;

                case 2: handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                yellow.setBackgroundColor(Color.parseColor("#fdff00"));
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        yellow.setBackgroundColor(Color.parseColor("#d7d800"));
                                    }
                                }, 1000);
                            }
                        }, (2000*i));
                        break;

                case 3: handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blue.setBackgroundColor(Color.parseColor("#00d2ff"));
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        blue.setBackgroundColor(Color.parseColor("#0790ae"));
                                    }
                                }, 1000);
                            }
                        }, (2000*i));
                        break;

            }
        }
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                replay.setEnabled(true);
                red.setEnabled(true);
                yellow.setEnabled(true);
                blue.setEnabled(true);
                text.setText("Repeat the sequence\nPress REPLAY to see it again");
            }
        },8000);
        /*Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[0]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[0]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (0));
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[1]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[1]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (2000));
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[2]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[2]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (4000));
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[3]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[3]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (6000));*/
    }

    public void replayClick(View view){
        replay.setEnabled(false);
        current = 0;
        one.setBackgroundColor(Color.parseColor("#ffffff"));
        two.setBackgroundColor(Color.parseColor("#ffffff"));
        three.setBackgroundColor(Color.parseColor("#ffffff"));
        four.setBackgroundColor(Color.parseColor("#ffffff"));
        text.setText("Playing Sequence...");
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[0]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[0]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (0));
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[1]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[1]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (2000));
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[2]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[2]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (4000));
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                switch (sequence[3]){
                    case 1:red.setBackgroundColor(Color.parseColor("#ff0000")); break;
                    case 2:yellow.setBackgroundColor(Color.parseColor("#fdff00")); break;
                    case 3:blue.setBackgroundColor(Color.parseColor("#00d2ff")); break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (sequence[3]){
                            case 1:red.setBackgroundColor(Color.parseColor("#b40000")); break;
                            case 2:yellow.setBackgroundColor(Color.parseColor("#d7d800")); break;
                            case 3:blue.setBackgroundColor(Color.parseColor("#0790ae")); break;
                        }
                    }
                }, 1000);
            }
        }, (6000));
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                replay.setEnabled(true);
                text.setText("Repeat the sequence\nPress REPLAY to see it again");
            }
        }, 8000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

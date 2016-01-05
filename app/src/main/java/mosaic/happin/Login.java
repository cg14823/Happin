package mosaic.happin;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // logo using Eveleth Dot Bold as required by happy city
        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/EvelethDotBold.otf");
        logo.setTypeface(custom_font);
    }

    public void login(View view) {

        boolean validUserAndPassword = validLogIn(view);
        EditText emailField = (EditText) findViewById(R.id.email);
        String email = emailField.getText().toString();
        if (validUserAndPassword) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Wrong username or password", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void forgotPwd(View view) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "You dumb Shit you shouldn't forget your password", Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean validLogIn(View view) {
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passwordField = (EditText) findViewById(R.id.password);
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        if (email.equals("dev@dev.com") && password.equals("dev")) return true;
        else return false;
    }

    /*-----------------------------------HASHING OF PASSWORD-------------------------------------*/

    private String convertToHex(byte[] data) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        String hex=null;
        hex= Base64.encodeToString(data, 0, data.length,0);
        sb.append(hex);

        return sb.toString();
    }


    public String computeSHAHash(String password) {
        MessageDigest mdSha1 = null;
        String SHAHash ="";
        try {
            mdSha1 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            Log.e("myapp", "Error initializing SHA1 message digest");
        }
        try {
            mdSha1.update(password.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] data = mdSha1.digest();
        try {
            SHAHash = convertToHex(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SHAHash;
    }

}
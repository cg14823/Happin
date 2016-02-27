package mosaic.happin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class Login extends AppCompatActivity {

    Firebase myFirebaseRef;
    String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/");

        setContentView(R.layout.activity_login);
        // logo using Eveleth Dot Bold as required by happy city
        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/EvelethDotBold.otf");
        logo.setTypeface(custom_font);

    }

    public void login(View view) {
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passwordField = (EditText) findViewById(R.id.password);
        String email = emailField.getText().toString();
        String pass = passwordField.getText().toString();
        myFirebaseRef.authWithPassword(email, pass, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                userToken = authData.getToken();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("USER_ID", authData.getUid());
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                showToast(firebaseError.getMessage());
            }
        });
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void forgotPwd(View view) {
        LayoutInflater inflater = getLayoutInflater();
        // message for password recovery
        final AlertDialog.Builder recPassDialog = new AlertDialog.Builder(this);
        final View dialogView = (inflater.inflate(R.layout.dialog_recpswrd, null));
        recPassDialog.setView(dialogView);
        recPassDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText text = (EditText) dialogView.findViewById(R.id.fgtemail);
                final String email = text.getText().toString();
                myFirebaseRef.resetPassword(email, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        // password reset email sent
                        showToast("Sent to " + email);
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // error encountered
                        showToast(email + "is an invalid email");
                    }
                });


            }
        });
        recPassDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = recPassDialog.create();
        alert.show();
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
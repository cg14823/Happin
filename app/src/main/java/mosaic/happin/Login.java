package mosaic.happin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
        myFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    userToken = authData.getToken();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("USER_ID", authData.getUid());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void loadingSwitch (boolean on){
        TextView forgotP = (TextView)findViewById(R.id.forgot);
        Button sign = (Button)findViewById(R.id.signUpButton);
        Button log = (Button)findViewById(R.id.loginButton);
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passwordField = (EditText) findViewById(R.id.password);
        com.wang.avi.AVLoadingIndicatorView loading = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.progressBarLogIn);
        if (on){
            emailField.setVisibility(View.INVISIBLE);
            passwordField.setVisibility(View.INVISIBLE);
            forgotP.setVisibility(View.INVISIBLE);
            sign.setVisibility(View.INVISIBLE);
            log.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
        }
        else{
            emailField.setVisibility(View.VISIBLE);
            passwordField.setVisibility(View.VISIBLE);
            forgotP.setVisibility(View.VISIBLE);
            sign.setVisibility(View.VISIBLE);
            log.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }

    }

    public void login(View view) {
        final EditText emailField = (EditText) findViewById(R.id.email);
        final EditText passwordField = (EditText) findViewById(R.id.password);
        final String email = emailField.getText().toString();
        String pass = passwordField.getText().toString();

        loadingSwitch(true);
        if (isValidEmail(email)) {
            myFirebaseRef.authWithPassword(email, pass, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    userToken = authData.getToken();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    loadingSwitch(false);
                    switch (firebaseError.getCode()) {
                        case FirebaseError.INVALID_EMAIL:
                            new AlertDialog.Builder(Login.this)
                                    .setTitle("Create an account")
                                    .setMessage("There are no account associated with this email. Please sign up")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            passwordField.setText("");
                                            Intent intent = new Intent(getApplicationContext(), SignUp.class);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            break;
                        case FirebaseError.INVALID_PASSWORD:
                            showToast("Incorrect password, try again");
                            break;
                        default:
                            showToast(firebaseError.getMessage());
                            break;
                    }
                }
            });
        } else {
            showToast("Enter a valid email"); loadingSwitch(false);}
    }

    public void signUp(View view) {
        final EditText emailField = (EditText) findViewById(R.id.email);
        final EditText passwordField = (EditText) findViewById(R.id.password);
        emailField.setText("");
        passwordField.setText("");
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
                        showToast(firebaseError.getMessage());
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

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
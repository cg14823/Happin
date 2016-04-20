package mosaic.happin;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class SignUp extends AppCompatActivity {

    Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://flickering-torch-2192.firebaseio.com/");
        // Set up font
        setContentView(R.layout.signup);
        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/EvelethDotBold.otf");
        logo.setTypeface(custom_font);
        Intent intent = getIntent();
        if (intent.hasExtra("email")) {
                String email = intent.getExtras().getString("email");
                EditText emailField = (EditText) findViewById(R.id.emailField);
                emailField.setText(email);
            }
        }

    public void signUp(View view) {
        EditText nameField = (EditText) findViewById(R.id.nameField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);
        EditText emailField = (EditText) findViewById(R.id.emailField);
        if (passwordConditions() && isValidEmail(emailField.getText().toString())){
            final String name = nameField.getText().toString();
            String password = passwordField.getText().toString();
            final String email = emailField.getText().toString();

            myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    showToast("Successfully created new user");
                    User newUser = new User(name, email, "password","null Image");
                    myFirebaseRef.child("users").child(result.get("uid").toString()).setValue(newUser);
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    showToast(firebaseError.getMessage());
                }
            });
        } else {
            showToast("Enter a valid email");
        }
    }

    private boolean passwordConditions() {
        EditText pswdFd = (EditText) findViewById(R.id.passwordField);
        EditText pswdFd2 = (EditText) findViewById(R.id.rptpassword);
        String Pswd = pswdFd.getText().toString();
        if (pswdFd.getText().toString().equals(pswdFd2.getText().toString())) {
            if (Pswd.length() > 7) {
                if (Pswd.matches(".*\\d+.*")) {
                    return true;
                } else showToast("The passwords must contain at least one digit");
            } else showToast("The passwords must be at least 8 characters long");
        } showToast("Passwords do not match");
        return false;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

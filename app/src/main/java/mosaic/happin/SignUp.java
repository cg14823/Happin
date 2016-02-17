package mosaic.happin;

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
        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/EvelethDotBold.otf");
        logo.setTypeface(custom_font);
    }

    public void signUp(View view){
        EditText nameField = (EditText) findViewById(R.id.nameField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);
        EditText emailField = (EditText) findViewById(R.id.emailField);
        if (passwordMatch()){
            final String name = nameField.getText().toString();
            String password = passwordField.getText().toString();
            final String email = emailField.getText().toString();

            myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    showToast("Successfully created new user");
                    User newUser = new User(name,email,"password");

                    myFirebaseRef.child("users").child(result.get("uid").toString()).setValue(newUser);
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    showToast(firebaseError.getMessage());
                }
            });
        }
        else{
            showToast("Passwords do not match");
        }
    }

    private boolean passwordMatch(){
        EditText pswdFd = (EditText) findViewById(R.id.passwordField);
        EditText pswdFd2 = (EditText) findViewById(R.id.rptpassword);
        if (pswdFd.getText().toString().equals(pswdFd2.getText().toString())) return true;
        return false;
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

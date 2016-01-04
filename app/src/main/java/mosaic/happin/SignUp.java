package mosaic.happin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
    }
    public void signUp(View view){
        EditText nameField = (EditText) findViewById(R.id.nameField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);
        EditText emailField = (EditText) findViewById(R.id.emailField);
        if (passwordMatch(view)){
            String name = nameField.getText().toString();
            String password = passwordField.getText().toString();
            String email = emailField.getText().toString();

            // encrypt and pack into a json and send to the server
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Passwords do not match", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean passwordMatch(View view){
        EditText pswdFd = (EditText) findViewById(R.id.passwordField);
        EditText pswdFd2 = (EditText) findViewById(R.id.rptpassword);
        if (pswdFd.getText().toString().equals(pswdFd2.getText().toString())) return true;
        return false;
    }
}

package mosaic.happin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
<<<<<<< HEAD
import android.widget.EditText;
=======
>>>>>>> 1545f6cb0bb7396ee24d6296917d1f9bae8f5b2e
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

<<<<<<< HEAD
    public void signUp(View view){

    }
    public void login (View view){
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passwordField = (EditText) findViewById(R.id.password);

        String emailStr = emailField.getText().toString();
        String  passwordStr = passwordField.getText().toString();

        // some verifaction here Alex and Senthy

        boolean correctLogin = true;

    }
    public void forgotPwd (View view){

    }
}
=======
    public void login(View view) {
        TextView email = (TextView) findViewById(R.id.email);
    }

    public void signUp(View view) {

    }

    public void forgotPwd(View view) {

    }

}
>>>>>>> 1545f6cb0bb7396ee24d6296917d1f9bae8f5b2e

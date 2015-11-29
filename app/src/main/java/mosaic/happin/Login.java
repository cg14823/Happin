package mosaic.happin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passwordField = (EditText) findViewById(R.id.password);
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        boolean validUserAndPassword = true;
        if (validUserAndPassword){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Wrong username or password",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void signUp(View view) {

    }

    public void forgotPwd(View view) {

    }

}
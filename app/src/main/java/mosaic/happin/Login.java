package mosaic.happin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // logo using Eveleth Dot Bold as required by happy city
        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/EvelethDotBold.otf");
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
            showToast("Wrong username or password");
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void forgotPwd(View view) {

        LayoutInflater inflater = getLayoutInflater();
        // message for password recovery
        AlertDialog.Builder recPassDialog = new AlertDialog.Builder(this);
        recPassDialog.setTitle("Password Recovery");
        recPassDialog.setMessage("Please enter your email");
        recPassDialog.setView(inflater.inflate(R.layout.dialog_recpswrd, null));
        recPassDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText email = (EditText)findViewById(R.id.email);
                if (verifyEmail(email.getText().toString()))
                    showToast("New password send to youir email");
                else
                    showToast("Invalid email");
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

    private boolean validLogIn(View view) {
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passwordField = (EditText) findViewById(R.id.password);
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        if (email.equals("dev@dev.com") && password.equals("dev")) return true;
        else return true;
    }
    private boolean verifyEmail (String email){
        // SERVER STUFF HERE! <---------------------------------------------------------------------
        return false;
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
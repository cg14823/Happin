package mosaic.happin;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class Settings extends AppCompatActivity {

    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        uid =i.getStringExtra("USER_ID");

        Firebase.setAndroidContext(this);
    }

    public void nameChange(View view){
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog.Builder nameChangeDlg = new AlertDialog.Builder(this);
        final View dialogView = (inflater.inflate(R.layout.dialog_change_name, null));
        nameChangeDlg.setView(dialogView);
        nameChangeDlg.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText nameField = (EditText) dialogView.findViewById(R.id.nameChangField);
                String name = nameField.getText().toString();
                if (name.length() > 2){

                }
                else showToast("Name must contain at least 3 characters");


            }
        });
        nameChangeDlg.setNegativeButton("Changed my mind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = nameChangeDlg.create();
        alert.show();
        
    }
    public void profileChange (View view){}


    private void serverPasswordChange(final String orPswd, final String newPswd){
        Firebase fireRef = new Firebase("https://flickering-torch-2192.firebaseio.com/users/"+uid);
        fireRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String email = user.getEmail();
                Firebase ref = new Firebase("https://flickering-torch-2192.firebaseio.com");
                ref.changePassword(email,orPswd,newPswd,new Firebase.ResultHandler(){
                    @Override
                    public void onSuccess() {
                        showToast("Password has been changed");
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        showToast("Unexpected error occured");
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void passwordChange(View view){
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog.Builder pswdChangeDlg = new AlertDialog.Builder(this);
        final View dialogView = (inflater.inflate(R.layout.dailog_changepswd, null));
        pswdChangeDlg.setView(dialogView);
        pswdChangeDlg.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText originalPswdField = (EditText) dialogView.findViewById(R.id.oldpswdRST);
                EditText newPswdField = (EditText) dialogView.findViewById(R.id.newpswdRST);
                EditText confPswdField = (EditText) dialogView.findViewById(R.id.newConpswdRST);
                String orPswd = originalPswdField.getText().toString();
                String newPswd = newPswdField.getText().toString();
                String conPswd = confPswdField.getText().toString();

                if (newPswd.equals(conPswd)) {
                    if (newPswd.length() > 7) {
                        if (newPswd.matches(".*\\d+.*")) {
                            serverPasswordChange(orPswd, newPswd);
                        } else showToast("The passwords must contain at least one digit");
                    } else showToast("The passwords must be at least 8 characters long");
                } else showToast("The passwords do not match");


            }
        });
        pswdChangeDlg.setNegativeButton("Changed my mind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = pswdChangeDlg.create();
        alert.show();
    }
}

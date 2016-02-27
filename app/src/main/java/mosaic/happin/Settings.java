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
    }

    public void nameChange(View view){

    }
    public void profileChange (View view){}
    public void passwordChange(View view){
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog.Builder pswdChangeDlg = new AlertDialog.Builder(this);
        final View dialogView = (inflater.inflate(R.layout.dialog_add_place, null));
        pswdChangeDlg.setView(dialogView);
        pswdChangeDlg.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText originalPswdField = (EditText) dialogView.findViewById(R.id.oldpswdRST);
                EditText newPswdField = (EditText) dialogView.findViewById(R.id.newpswdRST);
                EditText confPswdField = (EditText) dialogView.findViewById(R.id.newConpswdRST);
                String orPswd = originalPswdField.getText().toString();
                String newPswd = newPswdField.getText().toString();
                String conPswd = confPswdField.getText().toString();

                if (newPswd.equals(conPswd)){
                    if (newPswd.length() > 7){
                        if(newPswd.matches(".*\\d+.*")){
                            serverPasswordChange(orPswd,newPswd);
                        }
                        else showToast("The passwords must contain at least one digit");
                    } else showToast("The passwords must be at least 7 characters long");
                }
                else showToast("The passwords do not match");


            }
        });
    }

    private void serverPasswordChange(String orPswd, String newPswd){

    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this,
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

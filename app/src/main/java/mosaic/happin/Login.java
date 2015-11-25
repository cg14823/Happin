package mosaic.happin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        TextView email = (TextView) findViewById(R.id.email);
    }

    public void signUp(View view) {

    }

    public void forgotPwd(View view) {

    }

}
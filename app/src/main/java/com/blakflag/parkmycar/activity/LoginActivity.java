package com.blakflag.parkmycar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blakflag.parkmycar.R;

import com.blakflag.parkmycar.model.User;
import com.blakflag.parkmycar.util.App;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _emailText;
    EditText _passwordText;
    EditText phone;

    Button _loginButton;
    TextView _signupLink;

    FirebaseDatabase logindb;
    DatabaseReference loginref;

    private static final int SELECT_PHOTO = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText= (EditText) findViewById(R.id.input_email);
        _passwordText= (EditText) findViewById(R.id.input_password);

        _loginButton= (Button) findViewById(R.id.btn_login);
        _signupLink= (TextView) findViewById(R.id.link_signup);


        logindb = FirebaseDatabase.getInstance();
        loginref = logindb.getReference(App.USER_DB);


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("name","amit");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();


            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        findViewById(R.id.link_become_parker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RentUserSignUpActivity.class));
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Validate All");
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        loginref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean x=false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);

                    Log.d("User",user.email);
                    if(user.email.equals(email)&& user.password.equals(password))
                    {

                        x=true;
                        Log.d("User","Login Sucess");
                        onLoginSuccess(user);
                        progressDialog.dismiss();

                    }
                    else{


                    }
                    if(!x) onLoginFailed("Auth failed");
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {


                Snackbar.make(findViewById(android.R.id.content),  "Please Login", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(User user) {

        try{
            App.initUser();
            App.user=user;
            if(user.type.equals(App.CN_USER))
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            else if(user.type.equals(App.CN_RENT_USER))
                startActivity(new Intent(getApplicationContext(),RentPostActivity.class));
            finish();
        }catch (Exception ex){
            onLoginFailed("Error with parsing");
        }
    }

    public void onLoginFailed(String error) {
        //Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}

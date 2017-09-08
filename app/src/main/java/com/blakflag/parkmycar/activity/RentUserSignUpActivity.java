package com.blakflag.parkmycar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.model.User;
import com.blakflag.parkmycar.util.App;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RentUserSignUpActivity extends AppCompatActivity {

    FirebaseDatabase logindb;
    DatabaseReference loginref;

    private static final String TAG = "RentSignupActivity";
    EditText _nameText;

    EditText _emailText;

    EditText _passwordText;
    EditText _reEnterPasswordText;

    Button _signupButton;

    TextView _loginLink;

    EditText phone;
    EditText address;


    void onSignUpResult(final User newuser)
    {
        loginref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int x=0;

                if(x==0)
                {
                    onSignupSuccess(newuser);
                }
                else
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);

                    x++;
                    if(user.email.equals(newuser.email))
                    {
                        _emailText.setError("Change email");
                        onSignupFailed("User Exist");
                    }
                    else{

                        onSignupSuccess(newuser);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                onSignupFailed("Network Error");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_user_sign_up);

        logindb = FirebaseDatabase.getInstance();
        loginref = logindb.getReference(App.USER_DB);



        phone= (EditText) findViewById(R.id.input_phone);
        address= (EditText) findViewById(R.id.input_address);

        _loginLink= (TextView) findViewById(R.id.link_login);
        _signupButton= (Button) findViewById(R.id.btn_signup);
        _reEnterPasswordText= (EditText) findViewById(R.id.input_reEnterPassword);
        _passwordText= (EditText) findViewById(R.id.input_password);
        _emailText= (EditText) findViewById(R.id.input_email);
        _nameText= (EditText) findViewById(R.id.input_name);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });


        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Please validate the field ");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RentUserSignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();

        String email = _emailText.getText().toString();

        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if(password==reEnterPassword)
        {
            progressDialog.dismiss();
            Snackbar.make(findViewById(android.R.id.content), "Password are not same. ", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.RED)
                    .show();
            return;

        }
        else {

            User user=new User();
            user.name=name;
            user.email=email;
            user.password=password;
            user.address=address.getText().toString();
            user.carModel="";
            user.carNumber="";
            user.phone=phone.getText().toString();
            user.type=App.CN_RENT_USER;
            user.latitude="-1";
            user.longitude="-1";
            //loginref.push().setValue(user);
            onSignUpResult(user);
            progressDialog.dismiss();
        }
    }


    public void onSignupSuccess(User user) {
        loginref.push().setValue(user);
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(String error) {
        Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();


        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String pho = phone.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (pho.isEmpty() || pho.length() <10) {
            phone.setError("at least 10 characters");
            valid = false;
        } else {
            phone.setError(null);
        }



        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;

        } else {
            _passwordText.setError(null);

        }
        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

}

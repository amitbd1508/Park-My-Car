package com.blakflag.parkmycar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.model.User;
import com.blakflag.parkmycar.util.App;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AcountActivity extends AppCompatActivity {

    FirebaseDatabase logindb;
    DatabaseReference loginref;

    EditText phone,name,email;
    EditText address,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        name= (EditText) findViewById(R.id.input_name);
        phone= (EditText) findViewById(R.id.input_phone);
        address= (EditText) findViewById(R.id.input_address);
        email= (EditText) findViewById(R.id.input_email);
        password= (EditText) findViewById(R.id.input_password);


        name.setText(App.user.name);
        phone.setText(App.user.phone);
        address.setText(App.user.address);
        email.setText(App.user.email);
        logindb = FirebaseDatabase.getInstance();
        loginref = logindb.getReference(App.USER_DB);


        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child:dataSnapshot.getChildren())
                        {
                            User user=child.getValue(User.class);
                            if(user.email.equals(App.user.email))
                            {
                                user.email=email.getText().toString();
                                user.name=name.getText().toString();
                                user.address=address.getText().toString();
                                user.phone=phone.getText().toString();
                                user.password=password.getText().toString();
                                child.getRef().setValue(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}

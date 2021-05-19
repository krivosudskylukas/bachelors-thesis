package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class AutoSaveInDatabase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_activity_bacground);


        //Gettin instance and reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                // Initializing map which holds Values.
                String microName = getIntent().getStringExtra("name");
                String microDay = getIntent().getStringExtra("day");
                String result = getIntent().getStringExtra("area");

                //Checking if child exist in database
                //if yes add the day and measurement into database
                if(dataSnapshot.hasChild(microName)){
                    myRef.child(microName).child(microDay).setValue(result);
                }
                //if not create a new child and assign values
                else{
                    myRef.child(microName).setValue(result);
                }


                Intent intent = new Intent(AutoSaveInDatabase.this, MainActivity.class);
                startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }
}



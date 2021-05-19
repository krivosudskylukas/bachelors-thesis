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

public class Database extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        Button b = findViewById(R.id.send);
        Button button = findViewById(R.id.showButton);



        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String childrens = "";
                        for (DataSnapshot children : dataSnapshot.getChildren()) {
                            childrens += " " + children.getKey();
                        }
                        Toast.makeText(Database.this, childrens, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                       // Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Loading all Views
                EditText t = findViewById(R.id.textView);
                //TextView t1 = findViewById(R.id.textView2);
                EditText dayText = findViewById(R.id.measurementDay);
                EditText valueText = findViewById(R.id.measurementValue);

                //Gettin the value from input
                String day = dayText.getText().toString();
                String value = valueText.getText().toString();
                String micro = t.getText().toString();

                String newValue = value.replace(",",".");

                //Gettin instance and reference to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again whenever data at this location is updated.
                        // Initializing map which holds Values.

                        //Checking if child exist in database
                        //if yes add the day and measurement into database
                        if(dataSnapshot.hasChild(micro)){
                            myRef.child(micro).child(day).setValue(newValue);
                        }
                        //if not create a new child and assign values
                        else{
                            myRef.child(micro).setValue(newValue);
                        }
                        valueText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        Toast.makeText(Database.this, "Successfully saved.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Database.this, MainActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });
            }
        });
    }
}



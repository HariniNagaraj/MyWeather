package com.example.kiran.gps;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CloudService {

    private DatabaseReference citiesList;

    void uploadCitiesListToCloud(List<String> cities) {
        citiesList = FirebaseDatabase.getInstance().getReference("Users");
        citiesList.setValue(cities);

        citiesList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                };
                List<String> yourStringArray = dataSnapshot.getValue(t);
                Log.d("test", "Value is: " + yourStringArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

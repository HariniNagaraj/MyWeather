package com.example.kiran.gps;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CloudService implements ValueEventListener {


    private final CloudServiceDelegate delegate;
    private DatabaseReference citiesList;

    public CloudService(CloudServiceDelegate delegate) {
        this.delegate = delegate;
    }

    void sync() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        citiesList = FirebaseDatabase.getInstance().getReference("Cities/" + user.getUid());
        citiesList.addValueEventListener(this);
    }

    void uploadCitiesListToCloud(final List<String> cities) {
        if (citiesList != null) {
            citiesList.setValue(cities);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {
        };
        List<String> cloudCitiesList = dataSnapshot.getValue(genericTypeIndicator);
        Log.d("test", "Value is: " + cloudCitiesList);
        delegate.citiesDownloaded(cloudCitiesList);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    interface CloudServiceDelegate {
        void citiesDownloaded(List<String> citiesList);
    }
}


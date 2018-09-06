package com.example.kiran.gps;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CloudService {

    public CloudService() {
        uploadCitiesListToCloud();
    }

    private void uploadCitiesListToCloud() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference citiesList = database.getReference("citiesList");
        citiesList.setValue("Bellary,Manglore");
    }
}

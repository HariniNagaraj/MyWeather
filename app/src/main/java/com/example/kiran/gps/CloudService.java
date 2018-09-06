package com.example.kiran.gps;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CloudService {

    public CloudService() {
        uploadCitiesListTOCloud();
    }

    private void uploadCitiesListTOCloud() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference citiesList = database.getReference("citiesList");
        citiesList.setValue("Hello, World!");
    }
}

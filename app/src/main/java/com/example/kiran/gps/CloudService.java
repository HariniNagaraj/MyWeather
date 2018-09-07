package com.example.kiran.gps;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CloudService {


    void uploadCitiesListToCloud(List<String> cities) {
        DatabaseReference citiesList = FirebaseDatabase.getInstance().getReference("citiesList");
        citiesList.setValue(cities);
    }
}

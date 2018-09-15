package com.example.kiran.gps;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AnalyticsService {

    public static final String USER_CITIES_LIST = "userCitiesList";
    private static final String USER_REFRESH = "userRefresh";
    private final FirebaseAnalytics firebaseAnalytics;
    private final Bundle bundle;

    AnalyticsService(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle = new Bundle();
    }

    public void OnUserSignIn(FirebaseUser user) {
        bundle.putString(FirebaseAnalytics.Event.LOGIN, user.getDisplayName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public void onUserRefresh() {
        firebaseAnalytics.logEvent(USER_REFRESH, bundle);
    }

    public void uploadUserCitiesForAnalytics(List<String> userCitiesList) {
        StringBuilder userCities = new StringBuilder();
        for (String cities : userCitiesList) {
            userCities.append(cities).append(", ");
        }
        if (userCities.length() > 0)
            userCities = new StringBuilder(userCities.substring(0, userCities.length() - 2));
        bundle.putString(USER_CITIES_LIST, userCities.toString());
        firebaseAnalytics.logEvent(USER_CITIES_LIST, bundle);
    }
}

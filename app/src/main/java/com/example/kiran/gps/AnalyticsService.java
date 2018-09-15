package com.example.kiran.gps;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;

public class AnalyticsService {

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
}

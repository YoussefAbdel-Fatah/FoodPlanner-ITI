package com.example.foodplanner.presentation.splash.presenter;

import com.example.foodplanner.presentation.splash.view.SplashView;
import com.google.firebase.auth.FirebaseAuth;

import android.os.Handler;
public class SplashPresenter implements SplashPresenterInterface {
    private SplashView view;
    private FirebaseAuth mAuth;

    public SplashPresenter(SplashView view) {
        this.view = view;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void startTimer() {
        new Handler().postDelayed(() -> {
            // Check if user is already logged in
            if (mAuth.getCurrentUser() != null) {
                // User exists -> Go straight to Home
                view.navigateToHome();
            } else {
                // No user -> Go to Login
                view.navigateToLogin();
            }
        }, 3000);
    }
}
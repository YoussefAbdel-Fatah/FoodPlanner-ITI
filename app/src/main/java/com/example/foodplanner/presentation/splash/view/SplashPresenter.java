package com.example.foodplanner.presentation.splash.view;

import com.example.foodplanner.presentation.splash.presenter.SplashView;

import android.os.Handler;
public class SplashPresenter implements SplashPresenterInterface {
    private SplashView view;

    public SplashPresenter(SplashView view) {
        this.view = view;
    }
    @Override
    public void startTimer() {
        new Handler().postDelayed(() -> {
                view.navigateToNextScreen();
        },3000);
    }
}

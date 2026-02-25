package com.example.foodplanner.presentation.signup.view;

public interface SignUpView {
    void showLoading();
    void hideLoading();
    void onSignUpSuccess();
    void onSignUpError(String message);
}
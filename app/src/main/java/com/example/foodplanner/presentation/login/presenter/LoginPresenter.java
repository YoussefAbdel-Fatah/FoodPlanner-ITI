package com.example.foodplanner.presentation.login.presenter;

import com.example.foodplanner.presentation.login.view.LoginView;

public class LoginPresenter implements LoginPresenterInterface {
    private final LoginView view;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    @Override
    public void loginWithEmailAndPassword(String email, String password) {
        // Validation check for the beginner
        if (email.isEmpty() || password.isEmpty()) {
            view.onLoginFailure("Please fill all fields");
        } else {
            // Tomorrow we add the actual Firebase code here!
            view.onLoginSuccess();
        }
    }
}

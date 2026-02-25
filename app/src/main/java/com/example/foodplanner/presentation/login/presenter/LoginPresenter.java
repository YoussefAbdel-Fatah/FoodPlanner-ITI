package com.example.foodplanner.presentation.login.presenter;

import com.example.foodplanner.data.network.FirebaseRemoteSource;
import com.example.foodplanner.presentation.login.view.LoginView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenter implements LoginPresenterInterface {
    private LoginView view;
    private FirebaseRemoteSource repo;

    public LoginPresenter(LoginView view) {
        this.view = view;
        this.repo = new FirebaseRemoteSource();
    }

    @Override
    public void loginWithEmailAndPassword(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.onLoginFailure("Please enter email and password");
            return;
        }

        // Show loading (optional, if you added ProgressBar to Login xml)
        // view.showLoading();

        repo.signIn(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // Success!
                            view.onLoginSuccess();
                        },
                        error -> {
                            // Failure
                            view.onLoginFailure(error.getMessage());
                        }
                );
    }
}
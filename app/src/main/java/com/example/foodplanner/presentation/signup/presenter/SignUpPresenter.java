package com.example.foodplanner.presentation.signup.presenter;

import com.example.foodplanner.data.network.FirebaseRemoteSource;
import com.example.foodplanner.presentation.signup.view.SignUpView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignUpPresenter implements SignUpPresenterInterface {
    private final SignUpView view;
    private final FirebaseRemoteSource repo; // Our bridge to Firebase

    public SignUpPresenter(SignUpView view) {
        this.view = view;
        this.repo = new FirebaseRemoteSource();
    }

    @Override
    public void registerUser(String email, String password, String confirmPassword) {


        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.onSignUpError("Please fill all fields");
            return;
        }
        if (!password.equals(confirmPassword)) {
            view.onSignUpError("Passwords do not match");
            return;
        }

        view.showLoading();

        // RxJava Magic starts here
        repo.signUp(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading(); // 2. Stop spinner on success
                            view.onSignUpSuccess();
                        },
                        error -> {
                            view.hideLoading(); // 3. Stop spinner on error
                            view.onSignUpError(error.getMessage());
                        }
                );
    }
}
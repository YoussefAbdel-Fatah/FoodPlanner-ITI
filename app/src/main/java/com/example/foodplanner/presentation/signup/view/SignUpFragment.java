package com.example.foodplanner.presentation.signup.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.foodplanner.R;
import com.example.foodplanner.presentation.signup.presenter.SignUpPresenter;
import com.example.foodplanner.presentation.signup.presenter.SignUpPresenterInterface;

public class SignUpFragment extends Fragment implements SignUpView {

    private SignUpPresenterInterface presenter;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;

    private ProgressBar progressBar;

    public SignUpFragment() {
        super(R.layout.fragment_sign_up);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Presenter
        presenter = new SignUpPresenter(this);

        // Initialize UI Components
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        progressBar = view.findViewById(R.id.progressBar);

        btnSignUp.setOnClickListener(v -> {
            presenter.registerUser(
                    etEmail.getText().toString(),
                    etPassword.getText().toString(),
                    etConfirmPassword.getText().toString()
            );
        });
    }


    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false); // Disable button so they don't click twice
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnSignUp.setEnabled(true);
    }
    @Override
    public void onSignUpSuccess() {
        Toast.makeText(getContext(), "Account Created!", Toast.LENGTH_SHORT).show();

        // Navigate to Home
        Navigation.findNavController(requireView())
                .navigate(R.id.action_signUpFragment_to_homeFragment);
    }

    @Override
    public void onSignUpError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
package com.example.foodplanner.presentation.login.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.login.presenter.LoginPresenter;
import com.example.foodplanner.presentation.login.presenter.LoginPresenterInterface;

public class LoginFragment extends Fragment implements LoginView {

    private LoginPresenterInterface presenter;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    private TextView tvGoToSignUp;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new LoginPresenter(this);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvGoToSignUp = view.findViewById(R.id.tvGoToSignUp);

        btnLogin.setOnClickListener(v -> {
            presenter.loginWithEmailAndPassword(
                    etEmail.getText().toString(),
                    etPassword.getText().toString()
            );
        });

        tvGoToSignUp.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_loginFragment_to_signUpFragment);
        });
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFailure(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
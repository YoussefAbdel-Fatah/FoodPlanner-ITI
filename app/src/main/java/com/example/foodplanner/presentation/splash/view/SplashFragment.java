package com.example.foodplanner.presentation.splash.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.splash.presenter.SplashPresenter;
import com.example.foodplanner.presentation.splash.presenter.SplashPresenterInterface;

public class SplashFragment extends Fragment implements SplashView {
    private SplashPresenterInterface presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SplashPresenter presenter = new SplashPresenter(this);

        presenter.startTimer();
    }

    @Override
    public void navigateToHome() {
        // Use the new ID we just created in the nav_graph
        Navigation.findNavController(requireView())
                .navigate(R.id.action_splashFragment_to_nav_home);
    }

    @Override
    public void navigateToLogin() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_splashFragment_to_loginFragment);
    }
}

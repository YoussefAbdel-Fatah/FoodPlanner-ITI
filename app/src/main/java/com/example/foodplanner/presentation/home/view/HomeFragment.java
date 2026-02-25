package com.example.foodplanner.presentation.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.model.Category;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.presentation.home.presenter.HomePresenter;
import com.example.foodplanner.presentation.home.view.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeView {

    private HomePresenter presenter;

    // UI Elements
    private ImageView imgMeal;
    private TextView tvMealName, tvMealArea;

    // RecyclerView Elements
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize UI components
        imgMeal = view.findViewById(R.id.imgMeal);
        tvMealName = view.findViewById(R.id.tvMealName);
        tvMealArea = view.findViewById(R.id.tvMealArea);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);

        // 2. Setup RecyclerView (Horizontal)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategories.setLayoutManager(layoutManager);

        // Initialize Adapter with empty list first
        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>());
        recyclerViewCategories.setAdapter(categoryAdapter);

        // 3. Initialize Presenter & Fetch Data
        presenter = new HomePresenter(this);
        presenter.getHomeData(); // This fetches both Meal and Categories
    }

    @Override
    public void showRandomMeal(Meal meal) {
        tvMealName.setText(meal.getName());
        tvMealArea.setText(meal.getArea());
        Glide.with(this).load(meal.getImageUrl()).into(imgMeal);
    }

    @Override
    public void showCategories(List<Category> categories) {
        // Update the adapter with the list from API
        categoryAdapter.setList(categories);
    }

    @Override
    public void showError(String errorMsg) {
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        // You can add a ProgressBar here later if you want
    }

    @Override
    public void hideLoading() {
        // Hide ProgressBar
    }
}
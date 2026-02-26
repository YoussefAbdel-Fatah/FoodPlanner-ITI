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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.model.Category;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.presentation.home.presenter.HomePresenter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeView, CategoryAdapter.OnCategoryClickListener {

    private HomePresenter presenter;

    // UI Elements
    private ImageView imgMeal;
    private TextView tvMealName, tvMealArea;
    private MaterialCardView cardMealOfDay;

    // RecyclerView Elements
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;

    // Store the current random meal for navigation
    private Meal currentMeal;

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
        cardMealOfDay = view.findViewById(R.id.cardMealOfDay);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);

        // 2. Setup RecyclerView (Horizontal)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategories.setLayoutManager(layoutManager);

        // Initialize Adapter with empty list and click listener
        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>(), this);
        recyclerViewCategories.setAdapter(categoryAdapter);

        // 3. Set click listener on Meal of the Day card
        cardMealOfDay.setOnClickListener(v -> {
            if (currentMeal != null && currentMeal.getId() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("mealId", currentMeal.getId());
                Navigation.findNavController(v)
                        .navigate(R.id.action_nav_home_to_mealDetailsFragment, bundle);
            }
        });

        // 4. Initialize Presenter & Fetch Data
        presenter = new HomePresenter(this);
        presenter.getHomeData();
    }

    @Override
    public void showRandomMeal(Meal meal) {
        this.currentMeal = meal;
        tvMealName.setText(meal.getName());
        tvMealArea.setText(meal.getArea());
        Glide.with(this).load(meal.getImageUrl()).into(imgMeal);
    }

    @Override
    public void showCategories(List<Category> categories) {
        categoryAdapter.setList(categories);
    }

    @Override
    public void showError(String errorMsg) {
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putString("categoryName", category.getName());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_home_to_categoryFragment, bundle);
    }
}
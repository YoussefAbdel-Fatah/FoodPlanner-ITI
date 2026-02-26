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
import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.data.db.MealDAO;
import com.example.foodplanner.data.db.MealEntity;
import com.example.foodplanner.model.Category;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.presentation.home.presenter.HomePresenter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment implements HomeView, CategoryAdapter.OnCategoryClickListener {

    private HomePresenter presenter;

    // UI Elements
    private ImageView imgMeal, btnFavMealOfDay;
    private TextView tvMealName, tvMealArea;
    private MaterialCardView cardMealOfDay;

    // RecyclerView Elements
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;

    // Store the current random meal for navigation
    private Meal currentMeal;
    private boolean isMealFavorite = false;

    private MealDAO mealDAO;
    private CompositeDisposable disposables = new CompositeDisposable();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDAO = AppDatabase.getInstance(requireContext()).mealDAO();

        // 1. Initialize UI components
        imgMeal = view.findViewById(R.id.imgMeal);
        tvMealName = view.findViewById(R.id.tvMealName);
        tvMealArea = view.findViewById(R.id.tvMealArea);
        cardMealOfDay = view.findViewById(R.id.cardMealOfDay);
        btnFavMealOfDay = view.findViewById(R.id.btnFavMealOfDay);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);

        // 2. Setup RecyclerView (Horizontal)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategories.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>(), this);
        recyclerViewCategories.setAdapter(categoryAdapter);

        // 3. Meal of the Day card click → details
        cardMealOfDay.setOnClickListener(v -> {
            if (currentMeal != null && currentMeal.getId() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("mealId", currentMeal.getId());
                Navigation.findNavController(v)
                        .navigate(R.id.action_nav_home_to_mealDetailsFragment, bundle);
            }
        });

        // 4. Favorite button click
        btnFavMealOfDay.setOnClickListener(v -> {
            if (currentMeal == null)
                return;
            if (isMealFavorite) {
                removeFromFavorites();
            } else {
                addToFavorites();
            }
        });

        // 5. Initialize Presenter & Fetch Data
        presenter = new HomePresenter(this);
        presenter.getHomeData(requireContext());
    }

    @Override
    public void showRandomMeal(Meal meal) {
        this.currentMeal = meal;
        tvMealName.setText(meal.getName());
        tvMealArea.setText(meal.getArea());
        Glide.with(this).load(meal.getImageUrl()).into(imgMeal);

        // Check if this meal is already a favorite
        checkIfFavorite(meal.getId());
    }

    private void checkIfFavorite(String mealId) {
        disposables.add(
                mealDAO.isFavorite(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(favorite -> {
                            isMealFavorite = favorite;
                            updateFavIcon();
                        }));
    }

    private void addToFavorites() {
        if (currentMeal == null)
            return;
        MealEntity entity = new MealEntity(currentMeal.getId(), currentMeal.getName(), currentMeal.getImageUrl());
        entity.strArea = currentMeal.getArea();
        entity.strCategory = currentMeal.getCategory();
        entity.strInstructions = currentMeal.getInstructions();
        entity.strYoutube = currentMeal.getYoutubeUrl();

        disposables.add(
                mealDAO.insertMeal(entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            isMealFavorite = true;
                            updateFavIcon();
                            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    private void removeFromFavorites() {
        if (currentMeal == null)
            return;
        MealEntity entity = new MealEntity(currentMeal.getId(), currentMeal.getName(), currentMeal.getImageUrl());

        disposables.add(
                mealDAO.deleteMeal(entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            isMealFavorite = false;
                            updateFavIcon();
                            Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    private void updateFavIcon() {
        if (isMealFavorite) {
            btnFavMealOfDay.setColorFilter(getResources().getColor(R.color.chip_red_text, null));
        } else {
            btnFavMealOfDay.setColorFilter(getResources().getColor(R.color.text_grey, null));
        }
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

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}
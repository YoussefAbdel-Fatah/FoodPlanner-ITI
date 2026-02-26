package com.example.foodplanner.presentation.details.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.foodplanner.model.Ingredient;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.presentation.details.presenter.MealDetailsPresenter;
import com.example.foodplanner.presentation.details.presenter.MealDetailsPresenterImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsFragment extends Fragment implements MealDetailsView {

    private MealDetailsPresenter presenter;

    // UI
    private ImageView imgMealDetail, btnBack;
    private TextView tvMealDetailName, chipArea, chipCategory;
    private TextView tvIngredientCount;
    private RecyclerView rvIngredients;
    private LinearLayout layoutInstructions;
    private TextView tvVideoHeader;
    private YouTubePlayerView youtubePlayerView;
    private FloatingActionButton fabFavorite;

    private IngredientsAdapter ingredientsAdapter;

    private Meal currentMeal;
    private boolean isFavorite = false;

    private MealDAO mealDAO;
    private CompositeDisposable disposables = new CompositeDisposable();

    public MealDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Room DAO
        mealDAO = AppDatabase.getInstance(requireContext()).mealDAO();

        // Initialize UI
        imgMealDetail = view.findViewById(R.id.imgMealDetail);
        btnBack = view.findViewById(R.id.btnBack);
        tvMealDetailName = view.findViewById(R.id.tvMealDetailName);
        chipArea = view.findViewById(R.id.chipArea);
        chipCategory = view.findViewById(R.id.chipCategory);
        tvIngredientCount = view.findViewById(R.id.tvIngredientCount);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        layoutInstructions = view.findViewById(R.id.layoutInstructions);
        tvVideoHeader = view.findViewById(R.id.tvVideoHeader);
        youtubePlayerView = view.findViewById(R.id.youtubePlayerView);
        fabFavorite = view.findViewById(R.id.fabFavorite);

        // Register lifecycle observer
        getLifecycle().addObserver(youtubePlayerView);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false);
        rvIngredients.setLayoutManager(layoutManager);
        ingredientsAdapter = new IngredientsAdapter(getContext(), new ArrayList<>());
        rvIngredients.setAdapter(ingredientsAdapter);

        // Back button
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // FAB click — toggle favorite
        fabFavorite.setOnClickListener(v -> {
            if (currentMeal == null)
                return;
            if (isFavorite) {
                removeFromFavorites();
            } else {
                addToFavorites();
            }
        });

        // Get meal ID from arguments
        if (getArguments() != null) {
            String mealId = getArguments().getString("mealId");
            if (mealId != null) {
                presenter = new MealDetailsPresenterImpl(this);
                presenter.getMealById(mealId);
            }
        }
    }

    @Override
    public void showMealDetails(Meal meal) {
        this.currentMeal = meal;

        // Hero image
        Glide.with(this).load(meal.getImageUrl()).into(imgMealDetail);

        // Meal name
        tvMealDetailName.setText(meal.getName());

        // Chips
        chipArea.setText(meal.getArea());
        chipCategory.setText(meal.getCategory());

        // Ingredients
        List<Ingredient> ingredients = meal.getIngredientList();
        tvIngredientCount.setText(ingredients.size() + " items");
        ingredientsAdapter.setList(ingredients);

        // Instructions
        setupInstructions(meal.getInstructions());

        // Video
        setupVideo(meal.getYoutubeUrl());

        // Check if already favorite
        checkIfFavorite(meal.getId());
    }

    private void checkIfFavorite(String mealId) {
        disposables.add(
                mealDAO.isFavorite(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(favorite -> {
                            isFavorite = favorite;
                            updateFabIcon();
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
                            isFavorite = true;
                            updateFabIcon();
                            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                        }, error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT)
                                .show()));
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
                            isFavorite = false;
                            updateFabIcon();
                            Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        }, error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT)
                                .show()));
    }

    private void updateFabIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_heart_filled);
            fabFavorite.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.chip_red_text, null)));
        } else {
            fabFavorite.setImageResource(R.drawable.ic_heart_filled);
            fabFavorite.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_green, null)));
        }
    }

    private void setupInstructions(String instructions) {
        if (instructions == null || instructions.trim().isEmpty())
            return;

        layoutInstructions.removeAllViews();

        String[] steps = instructions.split("\\r?\\n");
        int stepNumber = 1;

        for (String step : steps) {
            String trimmed = step.trim();
            if (trimmed.isEmpty())
                continue;

            View stepView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_instruction_step, layoutInstructions, false);

            TextView tvNumber = stepView.findViewById(R.id.tvStepNumber);
            TextView tvText = stepView.findViewById(R.id.tvStepText);

            tvNumber.setText(String.valueOf(stepNumber));
            tvText.setText(trimmed);

            layoutInstructions.addView(stepView);
            stepNumber++;
        }
    }

    private void setupVideo(String youtubeUrl) {
        if (youtubeUrl != null && !youtubeUrl.trim().isEmpty()) {
            String videoId = extractYoutubeVideoId(youtubeUrl);
            if (videoId != null) {
                tvVideoHeader.setVisibility(View.VISIBLE);
                youtubePlayerView.setVisibility(View.VISIBLE);

                youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0);
                    }
                });
            }
        }
    }

    private String extractYoutubeVideoId(String url) {
        if (url == null)
            return null;
        if (url.contains("v=")) {
            String[] parts = url.split("v=");
            if (parts.length > 1) {
                String id = parts[1];
                int ampersandIndex = id.indexOf('&');
                if (ampersandIndex != -1) {
                    id = id.substring(0, ampersandIndex);
                }
                return id;
            }
        }
        if (url.contains("youtu.be/")) {
            String[] parts = url.split("youtu.be/");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return null;
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
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}

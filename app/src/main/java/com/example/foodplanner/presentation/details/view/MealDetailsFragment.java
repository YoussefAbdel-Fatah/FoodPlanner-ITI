package com.example.foodplanner.presentation.details.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsFragment extends Fragment implements MealDetailsView {

    private static final String TAG = "MealDetailsFragment";

    private MealDetailsPresenter presenter;

    // UI
    private ImageView imgMealDetail, btnBack;
    private TextView tvMealDetailName, chipArea, chipCategory;
    private TextView tvIngredientCount;
    private RecyclerView rvIngredients;
    private LinearLayout layoutInstructions;
    private TextView tvVideoHeader;
    private WebView youtubeWebView;
    private FloatingActionButton fabFavorite;

    private IngredientsAdapter ingredientsAdapter;

    private Meal currentMeal;
    private boolean isFavorite = false;

    private MealDAO mealDAO;
    private CompositeDisposable disposables = new CompositeDisposable();

    public MealDetailsFragment() {
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
        youtubeWebView = view.findViewById(R.id.youtubeWebView);
        fabFavorite = view.findViewById(R.id.fabFavorite);

        // Setup WebView
        setupWebView();

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

    private void setupWebView() {
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webSettings.setUserAgentString(
                "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                        "Chrome/120.0.0.0 Mobile Safari/537.36");

        youtubeWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        youtubeWebView.setWebChromeClient(new WebChromeClient());

        youtubeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "WebView error, fallback to YouTube app");
                openYoutubeExternally();
            }
        });
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
        setupYoutubeVideo(meal.getYoutubeUrl());

        // Check if already favorite
        checkIfFavorite(meal.getId());
    }

    private void setupYoutubeVideo(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            tvVideoHeader.setVisibility(View.GONE);
            youtubeWebView.setVisibility(View.GONE);
            return;
        }

        String videoId = extractVideoId(youtubeUrl);
        if (videoId.isEmpty()) {
            tvVideoHeader.setVisibility(View.GONE);
            youtubeWebView.setVisibility(View.GONE);
            return;
        }

        tvVideoHeader.setVisibility(View.VISIBLE);
        youtubeWebView.setVisibility(View.VISIBLE);

        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "html, body { margin: 0; padding: 0; background: black; width: 100%; height: 100%; }" +
                "iframe { width: 100%; height: 100%; border: 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<iframe src='https://www.youtube-nocookie.com/embed/" + videoId +
                "?playsinline=1&rel=0' " +
                "allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share' "
                +
                "allowfullscreen></iframe>" +
                "</body>" +
                "</html>";

        youtubeWebView.loadDataWithBaseURL(
                "https://www.youtube-nocookie.com",
                html,
                "text/html",
                "UTF-8",
                null);
    }

    private String extractVideoId(String url) {
        try {
            Uri uri = Uri.parse(url);

            if (uri.getQueryParameter("v") != null) {
                return uri.getQueryParameter("v");
            }

            if (url.contains("youtu.be/")) {
                return uri.getLastPathSegment();
            }
        } catch (Exception e) {
            Log.e(TAG, "Video ID extraction failed", e);
        }
        return "";
    }

    private void openYoutubeExternally() {
        if (currentMeal != null && currentMeal.getYoutubeUrl() != null) {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(currentMeal.getYoutubeUrl()));
            startActivity(intent);
        }
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
    public void onPause() {
        super.onPause();
        if (youtubeWebView != null) {
            youtubeWebView.onPause();
            youtubeWebView.pauseTimers();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (youtubeWebView != null) {
            youtubeWebView.onResume();
            youtubeWebView.resumeTimers();
        }
    }

    @Override
    public void onDestroyView() {
        if (youtubeWebView != null) {
            youtubeWebView.destroy();
        }
        disposables.clear();
        super.onDestroyView();
    }
}

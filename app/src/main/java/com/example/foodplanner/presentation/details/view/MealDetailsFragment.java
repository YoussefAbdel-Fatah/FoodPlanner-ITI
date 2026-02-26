package com.example.foodplanner.presentation.details.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import com.example.foodplanner.model.Ingredient;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.presentation.details.presenter.MealDetailsPresenter;
import com.example.foodplanner.presentation.details.presenter.MealDetailsPresenterImpl;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MealDetailsFragment extends Fragment implements MealDetailsView {

    private MealDetailsPresenter presenter;

    // UI
    private ImageView imgMealDetail, btnBack;
    private TextView tvMealDetailName, chipArea, chipCategory;
    private TextView tvIngredientCount;
    private RecyclerView rvIngredients;
    private LinearLayout layoutInstructions;
    private TextView tvVideoHeader;
    private MaterialCardView cardVideoPlayer;
    private WebView webViewVideo;

    private IngredientsAdapter ingredientsAdapter;

    private Meal currentMeal;

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
        cardVideoPlayer = view.findViewById(R.id.cardVideoPlayer);
        webViewVideo = view.findViewById(R.id.webViewVideo);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false);
        rvIngredients.setLayoutManager(layoutManager);
        ingredientsAdapter = new IngredientsAdapter(getContext(), new ArrayList<>());
        rvIngredients.setAdapter(ingredientsAdapter);

        // Back button
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

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
                cardVideoPlayer.setVisibility(View.VISIBLE);

                WebSettings webSettings = webViewVideo.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setMediaPlaybackRequiresUserGesture(false);
                webViewVideo.setWebChromeClient(new WebChromeClient());

                String embedHtml = "<html><body style=\"margin:0;padding:0;background:#000;\">"
                        + "<iframe width=\"100%\" height=\"100%\" "
                        + "src=\"https://www.youtube.com/embed/" + videoId + "\" "
                        + "frameborder=\"0\" "
                        + "allowfullscreen "
                        + "allow=\"autoplay; encrypted-media\"></iframe>"
                        + "</body></html>";

                webViewVideo.loadData(embedHtml, "text/html", "utf-8");
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
        // Could add a ProgressBar later
    }

    @Override
    public void hideLoading() {
        // Hide ProgressBar
    }

    @Override
    public void onDestroyView() {
        if (webViewVideo != null) {
            webViewVideo.destroy();
        }
        super.onDestroyView();
    }
}

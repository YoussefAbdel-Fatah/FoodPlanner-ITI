package com.example.foodplanner.presentation.category.view;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.data.db.MealDAO;
import com.example.foodplanner.data.db.MealEntity;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.presentation.category.presenter.CategoryPresenterImp;
import com.example.foodplanner.presentation.category.presenter.CategoryPresenterInterface;
import com.example.foodplanner.presentation.search.view.SearchAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoryFragment extends Fragment
        implements CategoryViewInterface, SearchAdapter.OnSearchItemClickListener {

    private CategoryPresenterInterface presenter;
    private RecyclerView rvCategoryMeals;
    private SearchAdapter adapter;
    private TextView tvCategoryTitle;
    private ImageView btnBack;
    private MealDAO mealDAO;
    private CompositeDisposable disposables = new CompositeDisposable();

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDAO = AppDatabase.getInstance(requireContext()).mealDAO();

        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle);
        btnBack = view.findViewById(R.id.btnBack);
        rvCategoryMeals = view.findViewById(R.id.rvCategoryMeals);

        rvCategoryMeals.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SearchAdapter(getContext(), this);
        rvCategoryMeals.setAdapter(adapter);

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Observe favorites to keep heart icons in sync
        loadFavoriteIds();

        // Get category name from arguments
        if (getArguments() != null) {
            String categoryName = getArguments().getString("categoryName");
            if (categoryName != null) {
                tvCategoryTitle.setText(categoryName);
                presenter = new CategoryPresenterImp(this);
                presenter.getMealsByCategory(categoryName);
            }
        }
    }

    private void loadFavoriteIds() {
        disposables.add(
                mealDAO.getAllMeals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mealEntities -> {
                            Set<String> ids = new HashSet<>();
                            for (MealEntity e : mealEntities) {
                                ids.add(e.idMeal);
                            }
                            adapter.setFavoriteIds(ids);
                        }));
    }

    @Override
    public void showMeals(List<Meal> meals) {
        adapter.setList(meals);
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
    public void onItemClick(Meal meal) {
        Bundle bundle = new Bundle();
        bundle.putString("mealId", meal.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_categoryFragment_to_mealDetailsFragment, bundle);
    }

    @Override
    public void onFavoriteClick(Meal meal, boolean isCurrentlyFavorite) {
        if (isCurrentlyFavorite) {
            MealEntity entity = new MealEntity(meal.getId(), meal.getName(), meal.getImageUrl());
            disposables.add(
                    mealDAO.deleteMeal(entity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT)
                                            .show(),
                                    error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT)
                                            .show()));
        } else {
            MealEntity entity = new MealEntity(meal.getId(), meal.getName(), meal.getImageUrl());
            entity.strArea = meal.getArea();
            entity.strCategory = meal.getCategory();
            disposables.add(
                    mealDAO.insertMeal(entity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show(),
                                    error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT)
                                            .show()));
        }
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}
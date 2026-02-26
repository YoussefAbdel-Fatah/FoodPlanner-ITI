package com.example.foodplanner.presentation.favorite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.foodplanner.presentation.search.view.SearchAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoriteFragment extends Fragment implements SearchAdapter.OnSearchItemClickListener {

    private RecyclerView rvFavorites;
    private TextView tvEmpty;
    private SearchAdapter adapter;
    private MealDAO mealDAO;
    private CompositeDisposable disposables = new CompositeDisposable();

    public FavoriteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDAO = AppDatabase.getInstance(requireContext()).mealDAO();

        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvEmpty = view.findViewById(R.id.tvEmptyFavorites);

        adapter = new SearchAdapter(getContext(), this);
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFavorites.setAdapter(adapter);

        loadFavorites();
    }

    private void loadFavorites() {
        disposables.add(
                mealDAO.getAllMeals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mealEntities -> {
                            List<Meal> meals = new ArrayList<>();
                            Set<String> favIds = new HashSet<>();
                            for (MealEntity entity : mealEntities) {
                                Meal meal = new Meal();
                                meal.setId(entity.idMeal);
                                meal.setName(entity.strMeal);
                                meal.setImageUrl(entity.strMealThumb);
                                meal.setArea(entity.strArea);
                                meal.setCategory(entity.strCategory);
                                meals.add(meal);
                                favIds.add(entity.idMeal);
                            }
                            adapter.setFavoriteIds(favIds);
                            adapter.setList(meals);
                            tvEmpty.setVisibility(meals.isEmpty() ? View.VISIBLE : View.GONE);
                            rvFavorites.setVisibility(meals.isEmpty() ? View.GONE : View.VISIBLE);
                        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    @Override
    public void onItemClick(Meal meal) {
        Bundle bundle = new Bundle();
        bundle.putString("mealId", meal.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_favorite_to_mealDetailsFragment, bundle);
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
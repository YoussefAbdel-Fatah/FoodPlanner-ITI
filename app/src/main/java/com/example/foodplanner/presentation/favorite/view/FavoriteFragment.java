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
import com.example.foodplanner.data.db.MealEntity;
import com.example.foodplanner.presentation.favorite.presenter.FavoritePresenterImp;
import com.example.foodplanner.presentation.favorite.presenter.FavortiePresenterInterface;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment
        implements FavoriteViewInterface, FavoriteAdapter.OnFavoriteClickListener {

    private RecyclerView rvFavorites;
    private TextView tvEmpty;
    private FavoriteAdapter adapter;
    private FavortiePresenterInterface presenter;

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

        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvEmpty = view.findViewById(R.id.tvEmptyFavorites);

        adapter = new FavoriteAdapter(getContext(), new ArrayList<>(), this);
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFavorites.setAdapter(adapter);

        // Initialize presenter with DAO
        presenter = new FavoritePresenterImp(this,
                AppDatabase.getInstance(requireContext()).mealDAO());

        presenter.loadFavorites();
    }

    // ── FavoriteViewInterface ──

    @Override
    public void showFavorites(List<MealEntity> meals) {
        adapter.setList(meals);
        tvEmpty.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        adapter.setList(new ArrayList<>());
        tvEmpty.setVisibility(View.VISIBLE);
        rvFavorites.setVisibility(View.GONE);
    }

    @Override
    public void showRemoveSuccess() {
        Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // ── FavoriteAdapter callbacks ──

    @Override
    public void onRemoveClick(MealEntity meal) {
        presenter.removeFavorite(meal);
    }

    @Override
    public void onItemClick(MealEntity meal) {
        Bundle bundle = new Bundle();
        bundle.putString("mealId", meal.idMeal);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_favorite_to_mealDetailsFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        if (presenter instanceof FavoritePresenterImp) {
            ((FavoritePresenterImp) presenter).onDestroy();
        }
        super.onDestroyView();
    }
}
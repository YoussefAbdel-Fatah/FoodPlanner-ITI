package com.example.foodplanner.presentation.search.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.example.foodplanner.presentation.search.presenter.SearchPresenter;
import com.example.foodplanner.presentation.search.presenter.SearchPresenterInterface;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends Fragment implements SearchViewInterface, SearchAdapter.OnSearchItemClickListener {

    private SearchPresenterInterface presenter;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private EditText etSearch;
    private MealDAO mealDAO;
    private CompositeDisposable disposables = new CompositeDisposable();

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDAO = AppDatabase.getInstance(requireContext()).mealDAO();

        etSearch = view.findViewById(R.id.etSearch);
        recyclerView = view.findViewById(R.id.rvSearchResults);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SearchAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        presenter = new SearchPresenter(this);

        // Observe favorite IDs to keep the heart icons in sync
        loadFavoriteIds();

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String query = etSearch.getText().toString();
                if (!query.isEmpty()) {
                    presenter.searchMeals(query);
                }
                return true;
            }
            return false;
        });
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
                .navigate(R.id.action_nav_search_to_mealDetailsFragment, bundle);
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
            entity.strInstructions = meal.getInstructions();
            entity.strYoutube = meal.getYoutubeUrl();
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
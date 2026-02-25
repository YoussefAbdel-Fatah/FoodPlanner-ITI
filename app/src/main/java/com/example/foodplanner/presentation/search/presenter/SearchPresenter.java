package com.example.foodplanner.presentation.search.presenter;

import com.example.foodplanner.data.network.MealClient;
import com.example.foodplanner.presentation.search.view.SearchViewInterface;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenter {
    private SearchViewInterface view;
    private MealClient client;

    public SearchPresenter(SearchViewInterface view) {
        this.view = view;
        this.client = MealClient.getInstance();
    }

    public void searchMeals(String query) {
        view.showLoading();

        client.searchMeals(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            view.hideLoading();
                            if (response.getMeals() != null) {
                                view.showMeals(response.getMeals());
                            } else {
                                view.showError("No meals found");
                            }
                        },
                        error -> {
                            view.hideLoading();
                            view.showError(error.getMessage());
                        }
                );
    }
}
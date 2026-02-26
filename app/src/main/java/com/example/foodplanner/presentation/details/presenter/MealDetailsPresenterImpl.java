package com.example.foodplanner.presentation.details.presenter;

import com.example.foodplanner.data.network.MealClient;
import com.example.foodplanner.presentation.details.view.MealDetailsView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsPresenterImpl implements MealDetailsPresenter {

    private MealDetailsView view;
    private MealClient client;

    public MealDetailsPresenterImpl(MealDetailsView view) {
        this.view = view;
        this.client = MealClient.getInstance();
    }

    @Override
    public void getMealById(String id) {
        view.showLoading();
        client.getMealById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            view.hideLoading();
                            if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                                view.showMealDetails(response.getMeals().get(0));
                            }
                        },
                        error -> {
                            view.hideLoading();
                            view.showError(error.getMessage());
                        });
    }
}
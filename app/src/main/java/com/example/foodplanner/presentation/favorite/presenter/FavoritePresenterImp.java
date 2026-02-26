package com.example.foodplanner.presentation.favorite.presenter;

import com.example.foodplanner.data.db.MealDAO;
import com.example.foodplanner.data.db.MealEntity;
import com.example.foodplanner.presentation.favorite.view.FavoriteViewInterface;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritePresenterImp implements FavortiePresenterInterface {

    private FavoriteViewInterface view;
    private MealDAO mealDAO;
    private CompositeDisposable disposables = new CompositeDisposable();

    public FavoritePresenterImp(FavoriteViewInterface view, MealDAO mealDAO) {
        this.view = view;
        this.mealDAO = mealDAO;
    }

    @Override
    public void loadFavorites() {
        disposables.add(
                mealDAO.getAllMeals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(meals -> {
                            if (meals.isEmpty()) {
                                view.showEmpty();
                            } else {
                                view.showFavorites(meals);
                            }
                        }, error -> view.showError(error.getMessage())));
    }

    @Override
    public void removeFavorite(MealEntity meal) {
        disposables.add(
                mealDAO.deleteMeal(meal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.showRemoveSuccess(),
                                error -> view.showError(error.getMessage())));
    }

    public void onDestroy() {
        disposables.clear();
    }
}

package com.example.foodplanner.presentation.search.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.presentation.search.presenter.SearchPresenter;
import com.example.foodplanner.presentation.search.view.SearchAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class SearchFragment extends Fragment implements SearchViewInterface {

    private SearchPresenter presenter;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private EditText etSearch;

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

        // 1. Initialize UI
        etSearch = view.findViewById(R.id.etSearch);
        recyclerView = view.findViewById(R.id.rvSearchResults);

        // 2. Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SearchAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // 3. Initialize Presenter
        presenter = new SearchPresenter(this);

        // 4. Setup Search Listener (When user presses ENTER on keyboard)
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String query = etSearch.getText().toString();
                if (!query.isEmpty()) {
                    presenter.searchMeals(query);
                }
                return true;
            }
            return false;
        });

        // Optional: Trigger a default search so the screen isn't empty
        // presenter.searchMeals("Chicken");
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
        // Optional: Show a ProgressBar
    }

    @Override
    public void hideLoading() {
        // Optional: Hide a ProgressBar
    }
}
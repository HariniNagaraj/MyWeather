package com.example.kiran.gps;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchManager {

    private final List<String> citiesList = new ArrayList<>();
    @BindView(R.id.citiesSuggestionsList)
    ListView citiesSuggestionsList;
    @BindView(R.id.citiesSearchView)
    SearchView citiesSearchView;
    private final SearchManagerDelegate delegate;
    private ListAdapter adapter;

    SearchManager(Activity activity, SearchManagerDelegate delegate) {
        this.delegate = delegate;
        ButterKnife.bind(this, activity);
        setupSearchBar();
    }

    private void setupSearchBarIcon() {
        citiesSearchView.setIconifiedByDefault(true);
        citiesSearchView.setMaxWidth(Integer.MAX_VALUE);
        citiesSearchView.setFocusable(false);
        citiesSearchView.setFocusableInTouchMode(true);
        citiesSearchView.setQueryHint("Search City");
        setupCityList();
    }

    private void setUpAdapterForSearchBar(final ListView listView) {
        adapter = new ListAdapter(citiesList);
        citiesSuggestionsList.setAdapter(adapter);
        citiesSuggestionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                citiesSuggestionsList.setVisibility(View.GONE);
                String city = adapter.filteredData.get(position);
                delegate.addCityToDrawer(city);
                delegate.cityChangedFromSearchBar(city);
            }
        });
    }

    private void setupOnQueryTextListener(final SearchView searchView, final ListView listView) {
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listView.setVisibility(View.GONE);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void setupCityList() {
        citiesList.add("Kolkata");
        citiesList.add("Bangalore");
        citiesList.add("Hyderabad");
    }

    private void setupSearchBar() {
        setupSearchBarIcon();
        setUpAdapterForSearchBar(citiesSuggestionsList);
        setupOnQueryTextListener(citiesSearchView, citiesSuggestionsList);
    }

    public void minimize() {
        citiesSearchView.clearFocus();
        citiesSearchView.setIconified(true);
        citiesSuggestionsList.setVisibility(View.GONE);
    }

    public boolean isExpanded() {
        return !citiesSearchView.isIconified();
    }

    interface SearchManagerDelegate {

        void cityChangedFromSearchBar(String city);

        void addCityToDrawer(String city);
    }
}
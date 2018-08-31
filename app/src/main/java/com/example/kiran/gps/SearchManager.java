package com.example.kiran.gps;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchManager {

    private final List<String> citiesList = new ArrayList<>();
    private ListAdapter adapter;
    private SearchManagerDelegate delegate;

    SearchManager(SearchManagerDelegate delegate) {
        this.delegate = delegate;
    }

    private void setupSearchBar(SearchView searchView) {
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setFocusable(false);
        searchView.setFocusableInTouchMode(true);
        searchView.setQueryHint("Search City");
        setupCityList();
    }

    private void setUpAdapterForSearchBar(final ListView listView) {
        adapter = new ListAdapter(citiesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                listView.setVisibility(View.GONE);
                String city = adapter.filteredData.get(position);
                delegate.cityChanged(city);
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
            public boolean onQueryTextSubmit(String Query) {
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

    void setupSearchBar(SearchView citiesSearchView, ListView citiesSuggestionsList) {
        setupSearchBar(citiesSearchView);
        setUpAdapterForSearchBar(citiesSuggestionsList);
        setupOnQueryTextListener(citiesSearchView, citiesSuggestionsList);
    }
}
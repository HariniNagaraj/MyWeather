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

    @BindView(R.id.citiesSuggestionsList)
    ListView citiesSuggestionsList;
    @BindView(R.id.citiesSearchView)
    SearchView citiesSearchView;
    private final List<String> citiesList = new ArrayList<>();
    private final SearchManagerDelegate delegate;
    private SearchBarAdapter citiesSuggestionsListAdaptor;

    SearchManager(Activity activity, SearchManagerDelegate delegate) {
        this.delegate = delegate;
        ButterKnife.bind(this, activity);
        setupSearchBar();
    }

    public void minimize() {
        citiesSearchView.clearFocus();
        citiesSearchView.setIconified(true);
        citiesSuggestionsList.setVisibility(View.GONE);
    }

    public boolean isExpanded() {
        return !citiesSearchView.isIconified();
    }

    private void setupSearchBarIcon() {
        citiesSearchView.setIconifiedByDefault(true);
        citiesSearchView.setMaxWidth(Integer.MAX_VALUE);
        citiesSearchView.setFocusable(false);
        citiesSearchView.setFocusableInTouchMode(true);
        citiesSearchView.setQueryHint("Search City");
        setupCityList();
    }

    private void setUpAdapterForSearchBar() {
        citiesSuggestionsListAdaptor = new SearchBarAdapter(citiesList);
        citiesSuggestionsList.setAdapter(citiesSuggestionsListAdaptor);
        citiesSuggestionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onCitySelected(position);
            }
        });
    }

    private void onCitySelected(int position) {
        citiesSuggestionsList.setVisibility(View.GONE);
        String city = citiesSuggestionsListAdaptor.filteredData.get(position);
        delegate.addCityToDrawer(city);
        delegate.cityChangedFromSearchBar(city);
    }

    private void setupOnQueryTextListener() {
        citiesSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                citiesSearchView.setVisibility(View.VISIBLE);
            }
        });
        searchBarOnCloseListener();
        searchBarTextListener();
    }

    private void searchBarOnCloseListener() {
        citiesSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                citiesSuggestionsList.setVisibility(View.GONE);
                return true;
            }
        });
    }

    private void searchBarTextListener() {
        citiesSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                citiesSuggestionsList.setVisibility(View.VISIBLE);
                citiesSuggestionsListAdaptor.getFilter().filter(newText);
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
        setUpAdapterForSearchBar();
        setupOnQueryTextListener();
    }

    interface SearchManagerDelegate {

        void cityChangedFromSearchBar(String city);

        void addCityToDrawer(String city);
    }
}

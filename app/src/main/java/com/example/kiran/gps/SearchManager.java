package com.example.kiran.gps;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.List;

public class SearchManager {

    ListAdapter adapter;
    List<String> citiesList = new ArrayList<String>();

    void setupSearchBar(SearchView searchView) {
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setFocusable(false);
        searchView.setFocusableInTouchMode(true);
        searchView.setQueryHint("Search City");
        setupCityList();
        }

    void setUpAdapterForSearchBar(final ListView listView,final MainActivity mainActivity) {
        adapter = new ListAdapter(citiesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               listView.setVisibility(View.GONE);
                String city = adapter.filteredData.get(position);
                mainActivity.updateWeather(city);
            }
        });
    }

    void setupOnQueryTextListener(final SearchView searchView, final ListView listView) {
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
    void setupCityList() {
        citiesList.add("Kolkata");
        citiesList.add("Bangalore");
        citiesList.add("Hyderabad");
    }
}
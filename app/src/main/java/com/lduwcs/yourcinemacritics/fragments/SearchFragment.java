package com.lduwcs.yourcinemacritics.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.lduwcs.yourcinemacritics.R;
import com.lduwcs.yourcinemacritics.adapters.SearchAdapter;
import com.lduwcs.yourcinemacritics.models.apiModels.Movie;
import com.lduwcs.yourcinemacritics.uiComponents.NeuButton;
import com.lduwcs.yourcinemacritics.utils.ApiUtils;
import com.lduwcs.yourcinemacritics.utils.Genres;
import com.lduwcs.yourcinemacritics.utils.listeners.ApiUtilsGetAllMoviesListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private static final String TAG = "DEBUG1";
    private RecyclerView searchRecView;
    static ArrayList<Movie> movies;
    static ArrayList<Movie> allResultMovies;
    static ArrayList<Integer> checkedGenres;
    static ArrayList<MaterialCheckBox> checkBoxes;
    @SuppressLint("StaticFieldLeak")
    private static TextView txtNoResult;
    private SearchView searchView;
    private NeuButton btnFilter;
    private NeuButton btnSort;
    private boolean isDescendingSorted = true;

    private ApiUtils utils;

    @SuppressLint("StaticFieldLeak")
    static public SearchAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRecView = view.findViewById(R.id.favoriteRecView);
        searchView = view.findViewById(R.id.edtSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnSort = view.findViewById(R.id.btnSort);
        txtNoResult = view.findViewById(R.id.txtNoResult);

        utils = new ApiUtils();
        movies = new ArrayList<>();
        allResultMovies = new ArrayList<>();
        checkedGenres = new ArrayList<>();
        checkBoxes = new ArrayList<>();

        adapter = new SearchAdapter(view.getContext(), movies);
        searchRecView.setAdapter(adapter);
        searchRecView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

        utils.setApiUtilsGetAllMoviesListener(new ApiUtilsGetAllMoviesListener() {
            @Override
            public void onGetAllMoviesDone(ArrayList<Movie> movies) {
                onSearchingDone(movies);
            }

            @Override
            public void onError(String error) {
                txtNoResult.setVisibility(View.VISIBLE);
                txtNoResult.setText("No Internet Connection");
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                utils.getAllMovies(s);
                isDescendingSorted = true;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() > 0){
                    utils.getAllMovies(s);
                }
                else if(s.length() == 0){
                    utils.getAllMovies("a");
                }
                isDescendingSorted = true;
                return false;
            }
        });

        btnFilter.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View layout;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.filter, null);
            builder.setTitle("Choose genres");
            builder.setView(layout);
            checkBoxes.clear();

                MaterialCheckBox checkBox1 = layout.findViewById(R.id.cb12);
                checkBoxes.add(checkBox1);
                MaterialCheckBox checkBox2 = layout.findViewById(R.id.cb14);
                checkBoxes.add(checkBox2);
                MaterialCheckBox checkBox3 = layout.findViewById(R.id.cb16);
                checkBoxes.add(checkBox3);
                MaterialCheckBox checkBox4 = layout.findViewById(R.id.cb18);
                checkBoxes.add(checkBox4);
                MaterialCheckBox checkBox5 = layout.findViewById(R.id.cb27);
                checkBoxes.add(checkBox5);
                MaterialCheckBox checkBox6 = layout.findViewById(R.id.cb28);
                checkBoxes.add(checkBox6);
                MaterialCheckBox checkBox7 = layout.findViewById(R.id.cb35);
                checkBoxes.add(checkBox7);
                MaterialCheckBox checkBox8 = layout.findViewById(R.id.cb36);
                checkBoxes.add(checkBox8);
                MaterialCheckBox checkBox9 = layout.findViewById(R.id.cb37);
                checkBoxes.add(checkBox9);
                MaterialCheckBox checkBox10 = layout.findViewById(R.id.cb53);
                checkBoxes.add(checkBox10);
                MaterialCheckBox checkBox11 = layout.findViewById(R.id.cb80);
                checkBoxes.add(checkBox11);
                MaterialCheckBox checkBox12 = layout.findViewById(R.id.cb99);
                checkBoxes.add(checkBox12);
                MaterialCheckBox checkBox13 = layout.findViewById(R.id.cb878);
                checkBoxes.add(checkBox13);
                MaterialCheckBox checkBox14 = layout.findViewById(R.id.cb9648);
                checkBoxes.add(checkBox14);
                MaterialCheckBox checkBox15 = layout.findViewById(R.id.cb10402);
                checkBoxes.add(checkBox15);
                MaterialCheckBox checkBox16 = layout.findViewById(R.id.cb10749);
                checkBoxes.add(checkBox16);
                MaterialCheckBox checkBox17 = layout.findViewById(R.id.cb10751);
                checkBoxes.add(checkBox17);
                MaterialCheckBox checkBox18 = layout.findViewById(R.id.cb10752);
                checkBoxes.add(checkBox18);
                MaterialCheckBox checkBox19 = layout.findViewById(R.id.cb10770);
                checkBoxes.add(checkBox19);

                for(MaterialCheckBox checkBox : checkBoxes){
                    if(checkedGenres.contains(Genres.changeNameToGenre(checkBox.getText().toString()))){
                        checkBox.setChecked(true);
                    }
                }
                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkedGenres.clear();
                        for(MaterialCheckBox checkBox : checkBoxes){
                            if(checkBox.isChecked()){
                                checkedGenres.add(Genres.changeNameToGenre(checkBox.getText().toString()));
                            }
                        }

                        //Debug
                        System.out.println(checkedGenres.size());
                        for(Integer item : checkedGenres){
                            System.out.println(item + " ");
                        }

                        ArrayList<Movie> filteredMovies = new ArrayList<Movie>();
                        for(Movie movie : allResultMovies){
                            if(movie.getGenres().containsAll(checkedGenres)){
                                filteredMovies.add(movie);
                            }
                        }
                        movies.clear();
                        movies.addAll(filteredMovies);
                        adapter.notifyDataSetChanged();
                        txtNoResult.setVisibility(View.INVISIBLE);
                        if(movies.size() == 0){
                            txtNoResult.setVisibility(View.VISIBLE);
                            txtNoResult.setText(R.string.no_result);
                        }
                        Toast.makeText(getContext(), "Filtered by genres", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //CANCELED
                    }
                });

                builder.show();
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Movie> sortedMovies = new ArrayList<Movie>();
                ArrayList<Movie> noReleaseDayMovies = new ArrayList<Movie>();
                int length = movies.size();
                for(int i = 0; i < length; i++){
                    Movie latestMovie = movies.get(0);
                    for(Movie movie : movies){
                        if(isDescendingSorted){
                            if(movie.getReleaseDay() != null && movie.getReleaseDay().compareTo(latestMovie.getReleaseDay()) > 0){
                                latestMovie = movie;
                            }
                        }
                        else{
                            if(movie.getReleaseDay() != null && movie.getReleaseDay().compareTo(latestMovie.getReleaseDay()) <= 0){
                                latestMovie = movie;
                            }
                        }
                    }
                    movies.remove(latestMovie);
                    sortedMovies.add(latestMovie);
                }
                noReleaseDayMovies.addAll(movies);
                movies.clear();
                movies.addAll(sortedMovies);
                movies.addAll(noReleaseDayMovies);
                adapter.notifyDataSetChanged();

                if(isDescendingSorted){
                    Toast.makeText(getContext(), "Sorted by Release Day, From Latest to Oldest", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Sorted by Release Day, From Oldest to Latest", Toast.LENGTH_SHORT).show();
                }

                isDescendingSorted = !isDescendingSorted;
            }
        });
    }

    public static void onSearchingDone(ArrayList<Movie> data) {
        movies.clear();
        allResultMovies.clear();
        movies.addAll(data);
        allResultMovies.addAll(movies);
        checkedGenres.clear();
        adapter.notifyDataSetChanged();
        txtNoResult.setVisibility(View.INVISIBLE);
        if(movies.size() == 0){
            txtNoResult.setVisibility(View.VISIBLE);
            txtNoResult.setText(R.string.no_result);
        }
        Log.d(TAG, "onSearchingDone: success");
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.initFirebaseListener();
        adapter.notifyDataSetChanged();
    }
}
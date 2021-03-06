package com.lduwcs.yourcinemacritics.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lduwcs.yourcinemacritics.R;
import com.lduwcs.yourcinemacritics.adapters.HomeAdapter;
import com.lduwcs.yourcinemacritics.models.apiModels.Movie;
import com.lduwcs.yourcinemacritics.models.roomModels.AppDatabase;
import com.lduwcs.yourcinemacritics.models.roomModels.MoviesDao;
import com.lduwcs.yourcinemacritics.uiComponents.NeuButton;
import com.lduwcs.yourcinemacritics.utils.ApiUtils;
import com.lduwcs.yourcinemacritics.utils.listeners.ApiUtilsCommentListener;
import com.lduwcs.yourcinemacritics.utils.listeners.ApiUtilsLatestListener;
import com.lduwcs.yourcinemacritics.utils.listeners.ApiUtilsTopRatedListener;
import com.lduwcs.yourcinemacritics.utils.listeners.ApiUtilsUpcomingListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "DEBUG1";
    private static final int IS_TRENDING = 1;
    private static final int IS_LATEST = 2;
    private static final int IS_TOP_RATED = 3;
    private static final int IS_COMING_UP = 4;

    private int contentMode = IS_TRENDING;

    private RecyclerView homeRecView;
    private NeuButton btnTrending, btnLatest, btnTopRated, btnComingUp;
    private TextView txtHomeContentMode;
    private ArrayList<Movie> movies;

    private ApiUtils utils;
    private MoviesDao moviesDao;

    @SuppressLint("StaticFieldLeak")
    static public HomeAdapter adapter;

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeRecView = view.findViewById(R.id.homeRecView);
        btnTrending = view.findViewById(R.id.btnTrending);
        btnLatest = view.findViewById(R.id.btnLatest);
        btnTopRated = view.findViewById(R.id.btnTopRated);
        btnComingUp = view.findViewById(R.id.btnComingUp);
        txtHomeContentMode = view.findViewById(R.id.txtHomeContentMode);

        //Swipe to refresh
        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_to_refresh);
        pullToRefresh.setOnRefreshListener(() -> {
            switch (contentMode){
                case IS_TRENDING:
                    reloadTrending();
                    break;
                case IS_LATEST:
                    reloadLatest();
                    break;
                case IS_TOP_RATED:
                    reloadTopRated();
                    break;
                case IS_COMING_UP:
                    reloadUpcoming();
                    break;
            }
            pullToRefresh.setRefreshing(false);
            Toast.makeText(getContext(), "Reloading", Toast.LENGTH_SHORT).show();
        });

        //Connect to database
        AppDatabase appDatabase = Room.databaseBuilder(getContext(), AppDatabase.class, "yourmoviecriticsdb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        moviesDao = appDatabase.getMoviesDao();

        utils = new ApiUtils();
        movies = new ArrayList<>();

        adapter = new HomeAdapter(getContext(), movies);
        homeRecView.setAdapter(adapter);
        homeRecView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(homeRecView);

        loadMode();
        btnTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentMode = IS_TRENDING;
                loadMode();
            }
        });
        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentMode = IS_LATEST;
                loadMode();
            }
        });
        btnTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentMode = IS_TOP_RATED;
                loadMode();
            }
        });
        btnComingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentMode = IS_COMING_UP;
                loadMode();
            }
        });

        //----Add ApiUtils Listener-------------
        utils.setApiUtilsCommentListener(new ApiUtilsCommentListener() {
            @Override
            public void onGetTrendingDone(ArrayList<Movie> movies) {
                onLoadTrendingDone(movies);
            }

            @Override
            public void onGetTrendingError(String err) {
            }
        });

        utils.setApiUtilsLatestListener(new ApiUtilsLatestListener() {
            @Override
            public void onGetLatestDone(ArrayList<Movie> movies) {
                Log.d("DEBUG1", movies.get(0).getTitle().toString());
                onLoadLatestDone(movies);

            }

            @Override
            public void onGetLatestError(String err) {
            }
        });

        utils.setApiUtilsTopRatedListener(new ApiUtilsTopRatedListener() {
            @Override
            public void onGetTopRatedDone(ArrayList<Movie> movies) {
                onLoadTopRatedDone(movies);
            }

            @Override
            public void onGetTopRatedError(String err) {
            }
        });

        utils.setApiUtilsUpcomingListener(new ApiUtilsUpcomingListener() {
            @Override
            public void onGetUpcomingDone(ArrayList<Movie> movies) {
                onLoadUpcomingDone(movies);
            }

            @Override
            public void onGetUpcomingError(String err) {
            }
        });
    }

    //Human_duck
    private void reloadTrending() {
        movies.clear();
        adapter.notifyDataSetChanged();
        if(isConnectWifi()) {
            moviesDao.deleteAll(0);
            utils.getTrending();
        }
        else loadTrendingFromRoom();
    }

    private void reloadLatest(){
        movies.clear();
        adapter.notifyDataSetChanged();
        if(isConnectWifi()) {
            moviesDao.deleteAll(1);
            utils.getLatest();
        }
        else loadLatestFromRoom();
    }

    private void reloadTopRated(){
        movies.clear();
        adapter.notifyDataSetChanged();
        if(isConnectWifi()) {
            moviesDao.deleteAll(2);
            utils.getTopRated();
        }
        else loadTopRatedFromRoom();
    }

    private void reloadUpcoming(){
        movies.clear();
        adapter.notifyDataSetChanged();
        if(isConnectWifi()) {
            moviesDao.deleteAll(3);
            utils.getUpcoming();
        }
        else loadUpcomingFromRoom();
    }

    private void loadTrendingFromRoom(){
        movies.clear();
        movies.addAll(moviesDao.getTrending());
        adapter.notifyDataSetChanged();
    }

    private void loadLatestFromRoom(){
        movies.clear();
        movies.addAll(moviesDao.getLatest());
        adapter.notifyDataSetChanged();
    }

    private void loadTopRatedFromRoom(){
        movies.clear();
        movies.addAll(moviesDao.getTopRated());
        adapter.notifyDataSetChanged();
    }

    private void loadUpcomingFromRoom(){
        movies.clear();
        movies.addAll(moviesDao.getUpcoming());
        adapter.notifyDataSetChanged();
    }

    private void onLoadTrendingDone(ArrayList<Movie> data) {
        movies.clear();
        movies.addAll(data);
        for (Movie movie : data) {
            movie.setType(0);
            movie.setPid(movie.getId()*10+movie.getType());
            moviesDao.insert(movie);
        }
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onLoadTrendingDone: success");
    }

    private void onLoadLatestDone(ArrayList<Movie> data){
        movies.clear();
        movies.addAll(data);
        for (Movie movie : data) {
            movie.setType(1);
            movie.setPid(movie.getId()*10+movie.getType());
            moviesDao.insert(movie);
        }
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onLoadLatestDone: success");
    }

    private void onLoadTopRatedDone(ArrayList<Movie> data){
        movies.clear();
        movies.addAll(data);
        for (Movie movie : data) {
            movie.setType(2);
            movie.setPid(movie.getId()*10+movie.getType());
            moviesDao.insert(movie);
        }
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onLoadTopRatedDone: success");
    }

    private void onLoadUpcomingDone(ArrayList<Movie> data){
        movies.clear();
        movies.addAll(data);
        for (Movie movie : data) {
            movie.setType(3);
            movie.setPid(movie.getId()*10+movie.getType());
            moviesDao.insert(movie);
        }
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onLoadUpcomingDone: success");
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.initFirebaseListener();
        Log.d(TAG, "onResume: " + "dfghjdfghjdfgh");
    }

    private boolean isConnectWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    private void loadMode() {
        switch (contentMode) {
            case IS_TRENDING:
                btnTrending.setOnActive(true);
                btnComingUp.setOnActive(false);
                btnTopRated.setOnActive(false);
                btnLatest.setOnActive(false);
                txtHomeContentMode.setText(R.string.trending);
                if (moviesDao.getTrending().size() > 0) {
                    loadTrendingFromRoom();
                } else reloadTrending();
                break;
            case IS_LATEST:
                btnTrending.setOnActive(false);
                btnComingUp.setOnActive(false);
                btnTopRated.setOnActive(false);
                btnLatest.setOnActive(true);
                txtHomeContentMode.setText(R.string.latest);
                if (moviesDao.getLatest().size() > 0) {
                    loadLatestFromRoom();
                } else reloadLatest();
                break;
            case IS_TOP_RATED:
                btnTrending.setOnActive(false);
                btnComingUp.setOnActive(false);
                btnTopRated.setOnActive(true);
                btnLatest.setOnActive(false);
                txtHomeContentMode.setText(R.string.top_rated);
                if (moviesDao.getTopRated().size() > 0) {
                    loadTopRatedFromRoom();
                } else reloadTopRated();
                break;
            case IS_COMING_UP:
                btnTrending.setOnActive(false);
                btnComingUp.setOnActive(true);
                btnTopRated.setOnActive(false);
                btnLatest.setOnActive(false);
                txtHomeContentMode.setText(R.string.coming_up);
                if (moviesDao.getUpcoming().size() > 0) {
                    loadUpcomingFromRoom();
                } else reloadUpcoming();
                break;
        }
    }
}
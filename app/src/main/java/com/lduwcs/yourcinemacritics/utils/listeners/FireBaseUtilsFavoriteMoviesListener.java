package com.lduwcs.yourcinemacritics.utils.listeners;

import android.view.View;

import com.lduwcs.yourcinemacritics.models.apiModels.Movie;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public interface FireBaseUtilsFavoriteMoviesListener {
    public void onGetFavoriteDone();
    public void onGetFavoriteError(String err);
}

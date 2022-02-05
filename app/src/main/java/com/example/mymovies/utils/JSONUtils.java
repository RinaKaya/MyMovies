package com.example.mymovies.utils;

import com.example.mymovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/** В этом класссе будем преобразовывать данные из JSON-объекта в фильм. */

public class JSONUtils {

    //во-первых, надо получить массив JSON-объектов по ключу results
    private static final String KEY_RESULTS = "results";

    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    //базовый путь до ресурса с картинками
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";

    //папки с нужными размером картинок (понадобится, чтобы составить полный путь до нужной картинки)
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    //этот метод получает массив с фильмами
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }

        try {
            //получаем массив JSON-объектов по ключу KEY_RESULTS
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);

            //теперь в цикле получаем фильмы
            for (int i = 0; i < jsonArray.length(); i++) {
                //получаем фильм
                JSONObject objectMovie = jsonArray.getJSONObject(i);

                //получаем данные о фильме
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = BASE_POSTER_URL + objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);

                //создаем объект Movie и передаем ему все полученные данные
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);

                //полученный фильм передаем в массив results
                result.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}

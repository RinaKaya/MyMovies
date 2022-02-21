package com.example.mymovies.utils;

import android.provider.MediaStore;

import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/** В этом класссе будем преобразовывать данные из JSON-объекта в фильм. */

public class JSONUtils {

    //во-первых, надо получить массив JSON-объектов по ключу results
    private static final String KEY_RESULTS = "results";

    //ключи для получения отзывов к фильму из JSON-объекта
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    //ключи для получения трейлера к фильму из JSON-объекта
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    //ключи для получения информации о фильме
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

    //метод, который возвращает массив отзывов
    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);

                //из jsonObjectReview получаем значения
                String author = jsonObjectReview.getString(KEY_AUTHOR);
                String content = jsonObjectReview.getString(KEY_CONTENT);

                //объект-отзыв
                Review review = new Review(author, content);

                //добавляем отзыв в массив result
                result.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result; //возвращаем массив отзывов
    }

    //метод, который возвращает массив трейлеров к фильму
    public static ArrayList<Trailer> getTrailersFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectTrailer = jsonArray.getJSONObject(i);

                //из jsonObjectTrailer получаем значения
                //Чтобы получить доступ к видео добавляем ссылку на ютуб BASE_YOUTUBE_URL,
                //где в качестве параметра мы передадим данный ключ.
                String key = BASE_YOUTUBE_URL + jsonObjectTrailer.getString(KEY_KEY_OF_VIDEO);
                String name = jsonObjectTrailer.getString(KEY_NAME);

                Trailer trailer = new Trailer(key, name);
                result.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result; //возвращаем массив отзывов
    }

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

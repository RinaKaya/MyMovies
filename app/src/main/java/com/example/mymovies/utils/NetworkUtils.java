package com.example.mymovies.utils;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/** В этом классе будет вся работа, связанная с сетью. */

public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie"; //базовый url

    //параметры
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";

    //значения параметров
    private static final String API_KEY = "bd067f070faea9ee1d1f7430a32fbee5";
    private static final String LANGUAGE_VALUE = "ru-RU"; //пока будем получать данные только на русском языке
    private static final String SORT_BY_POPULARITY = "popularity.desc"; //сортировка фильмов по популярности
    private static final String SORT_BY_TOP_RATED = "vote_average.desc"; //сортировка по средней оценке
    //номер страницы является числом и все время будет разный, поэтому сохранять ничего не будем

    //переменные, которые понадобятся для метода buildURL()
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    //метод, который будет формирует запрос
    private static URL buildURL(int sortBy, int page) {
        URL result = null;
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }

        Uri uri = Uri.parse(BASE_URL).buildUpon() //получаем строку BASE_URL в виде адреса, к которому можем прикреплять запросы
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result; //если все получилось, то вернется URL, а если нет, то получим null
    }

    //метод, который будет загружает данные из Интернета, то есть JSON-объект
    //для этого нам понадобится класс, расширяющий AsyncTask:
    //в качестве параметров будет принимать URL;
    //в процессе выполнения данные нам не нужны, поэтому Void;
    //возвращать будет JSONObject.
    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        JSONObject result = null; //то, что будем возвращать

        URL url = buildURL(sortBy, page);

        //получаем JSONObject
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null; //переменная, которую будем возвращать

            if (urls == null || urls.length == 0) {
                return result;
            }

            //если все нормально, то создаем соединение
            HttpURLConnection connection = null;

            //открываем соединение
            try {
                connection = (HttpURLConnection) urls[0].openConnection();

                InputStream inputStream = connection.getInputStream(); //создаем поток ввода

                //создаем ридер
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader); //чтобы читать строками

                //читаем данные
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    //сохраняем полученную строку
                    builder.append(line);
                    line = reader.readLine();
                }
                try {
                    result = new JSONObject(builder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return result;
        }
    }

}

package com.example.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

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
    //базовый url
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    //базовый url ресурса, где лежат трейлеры к фильмам
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";

    //базовый url ресурса, где хранятся отзывы к фильмам
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    //параметры
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    //значения параметров
    private static final String API_KEY = "bd067f070faea9ee1d1f7430a32fbee5";
    //private static final String LANGUAGE_VALUE = "ru-RU"; //пока будем получать данные только на русском языке
    private static final String SORT_BY_POPULARITY = "popularity.desc"; //сортировка фильмов по популярности
    private static final String SORT_BY_TOP_RATED = "vote_average.desc"; //сортировка по средней оценке
    private static final String MIN_VOTE_COUNT_VALUE = "1000";
    //номер страницы является числом и все время будет разный, поэтому сохранять ничего не будем


    //переменные, которые понадобятся для метода buildURL()
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    //метод, который генерирует полный URL к трейдеру фильма
    public static URL buildURLToVideos(int id, String lang) {
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //метод, который возвращает url к отзывам фильма
    public static URL buildURLToReviews(int id, String lang) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //метод, который будет формирует запрос
    public static URL buildURL(int sortBy, int page, String lang) {
        URL result = null;
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }

        Uri uri = Uri.parse(BASE_URL).buildUpon() //получаем строку BASE_URL в виде адреса, к которому можем прикреплять запросы
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result; //если все получилось, то вернется URL, а если нет, то получим null
    }

    //метод, который возвращает JSON-объект, из которого получим трейлер для фильма
    public static JSONObject getJSONForVideos(int id, String lang) {
        JSONObject result = null;

        //формируем url к ресурсу с трейлерами к фильмам
        URL url = buildURLToVideos(id, lang);

        try {
            //получаем JSONObject
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //метод, который возвращает JSON-объект, из которого получим отзывы для фильма
    public static JSONObject getJSONForReviews(int id, String lang) {
        JSONObject result = null;

        //формируем url к ресурсу с трейлерами к фильмам
        URL url = buildURLToReviews(id, lang);

        try {
            //получаем JSONObject
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //метод, который будет загружает данные из Интернета, то есть JSON-объект
    //для этого нам понадобится класс, расширяющий AsyncTask:
    //в качестве параметров будет принимать URL;
    //в процессе выполнения данные нам не нужны, поэтому Void;
    //возвращать будет JSONObject.
    public static JSONObject getJSONFromNetwork(int sortBy, int page, String lang) {
        JSONObject result = null; //то, что будем возвращать

        URL url = buildURL(sortBy, page, lang);

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

    //создаем загрузчик
    //в угловых скобках указываем, что класс должен возвращать JSONObject
    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        //передаем объект Bundle в конструкторе и присваиваем ему значение
        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        //для того, чтобы при инициализации загрузчика происходила загрузка, необходимо переопределить еще метод onStartLoading()
        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            //проверка для слушателя
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading(); //добавляем слушатель
            }

            //вызываем метод для продолжения загрузки
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            //так как объект Bundle может быть null, то добавляем проверку
            if (bundle == null) {
                return null;
            }

            //получаем url откуда мы хотим загрузить данные
            String urlAsString = bundle.getString("url");

            //получаем объект типа URL
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //теперь из URL надо загрузить данные
            JSONObject result = null; //переменная, которую будем возвращать

            if (url == null) {
                return result;
            }

            //если все нормально, то создаем соединение
            HttpURLConnection connection = null;

            try {
                //открываем соединение у полученного url
                connection = (HttpURLConnection) url.openConnection();

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

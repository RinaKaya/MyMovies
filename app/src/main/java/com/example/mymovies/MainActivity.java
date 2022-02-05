package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosters;

    //создаем адаптер
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //тестирование адаптера для компонента RecyclerView
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        //расположение элементов сеткой в компоненте RecyclerView
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));

        movieAdapter = new MovieAdapter();

        //получаем список фильмов
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

        //устанавливаем полученные фильмы у адаптера
        movieAdapter.setMovies(movies);

        //устанавливаем адаптер у RecyclerView
        recyclerViewPosters.setAdapter(movieAdapter);

        /*//1. тестирование работы метода buildURL() из класса NetworkUtils
        String url = NetworkUtils.buildURL(NetworkUtils.POPULARITY, 1).toString();
        Log.i("MyResult", url);*/

        /*//2. тестируем работу метода getJSONFromNetwork() из класса NetworkUtils
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.TOP_RATED, 3);
        if (jsonObject == null) {
            Toast.makeText(this, "Произошла ошибка!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Успешно!", Toast.LENGTH_SHORT).show();
        }*/

        /*//3. тестируем метод getMoviesFromJSON() из класса JSONUtils
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 5);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        StringBuilder builder = new StringBuilder();
        for (Movie movie : movies) {
            builder.append(movie.getTitle()).append("\n");
        }
        Log.i("MyResult", builder.toString());*/

    }
}
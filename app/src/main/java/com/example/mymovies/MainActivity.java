package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //ссылка на свитч
    private Switch switchSort;
    private TextView textViewTopRated;
    private TextView textViewPopularity;

    private RecyclerView recyclerViewPosters;

    //создаем адаптер
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewPopularity = findViewById(R.id.textViewPopularity);

        //тестирование адаптера для компонента RecyclerView
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        //расположение элементов сеткой в компоненте RecyclerView
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));

        movieAdapter = new MovieAdapter();

        //устанавливаем адаптер у RecyclerView
        recyclerViewPosters.setAdapter(movieAdapter);

        //чтобы фильмы сразу загрузились
        switchSort.setChecked(true);

        //добавляем слушатель для свитча
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheked) {
                setMethodOfSort(isCheked);
            }
        });
        switchSort.setChecked(false);

        //устанавливаем слушатель у адаптера
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Toast.makeText(MainActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        //устанавливаем адаптер у слушателя
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void OnReachEnd() {
                Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();
            }
        });

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

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);

        //устанавливаем бегунок на свиче
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);

        //устанавливаем бегунок на свиче
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated) {
        int methodOfSort;
        if (isTopRated) {
            //если свич включен, то рейтинговые фильмы
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.teal_200));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));

        } else {
            //если свитч выкл., то популярные фильмы
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.teal_200));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));

        }
        //получаем список фильмов
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

        //устанавливаем полученные фильмы у адаптера
        movieAdapter.setMovies(movies);
    }
}
package com.example.mymovies;

/** Активность для избранных фильмов. */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mymovies.adapters.MovieAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModal;
import com.example.mymovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavouriteMovies;

    //на странице избранных фильмов все будет также, как на главной,
    //поэтому используем тот же адаптер
    private MovieAdapter adapter;

    private MainViewModal viewModal;

    //чтобы добавить меню - надо переопределить метод onCreateOptionsMenu()
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //1. надо получить инфлэйтор
        MenuInflater inflater = getMenuInflater();
        //передаем меню, которое мы создали
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    //для того, чтобы добавить реакцию на нажатие пунктов меню, переопределяем метод onOptionsItemSelected()
    //в параметры этот метод принимает пункт меню, на который нажали
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //получаем id пункта меню, на который нажали
        int id = item.getItemId();
        //в зависимости от значения будем выполнять разные действия
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        recyclerViewFavouriteMovies = findViewById(R.id.recyclerViewFavouriteMovies);
        //разметка (сетка) для компонента RecyclerView
        recyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter();

        //устанавливаем адаптер у RecyclerView
        recyclerViewFavouriteMovies.setAdapter(adapter);

        viewModal = ViewModelProviders.of(this).get(MainViewModal.class);

        //получаем список избранных фильмов
        LiveData<List<FavouriteMovie>> favouriteMovies = viewModal.getFavouriteMovies();
        favouriteMovies.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                //при изменении данных мы в нашем адаптере устанавливаем фильмы
                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null) {
                    movies.addAll(favouriteMovies);
                    adapter.setMovies(movies);
                }
            }
        });

        //ПЕРЕХОД НА ДЕТАЛЬНУЮ СТРАНИЦУ из Избранного - не работает
        /*adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });*/

    }
}
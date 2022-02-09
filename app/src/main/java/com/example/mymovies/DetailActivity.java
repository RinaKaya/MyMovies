package com.example.mymovies;

/** Активность детальной страницы фильма. */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModal;
import com.example.mymovies.data.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewAddToFavourite;

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;
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
                Intent intentToMain = new Intent(this, MainActivity.class);
                startActivity(intentToMain);
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
        setContentView(R.layout.activity_detail);

        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);

        //1. получаем id фильма
        //получаем интент
        Intent intent = getIntent();
        //если интент не равен null и содержит ключ id
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            //иначе закрываем эту активность (вместо активности деталки будет вызвана активность, которая ее вызвала)
            finish();
        }
        //2. теперь надо получить фильм из БД
        viewModal = ViewModelProviders.of(this).get(MainViewModal.class);
        movie = viewModal.getMovieById(id);

        //3. далее устанавливаем значение у всех элементов
        //для ImageView установим значение с помощью Picasso
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        //преобразовываем рейтинг из типа Double в строку
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavourite();
    }

    public void onClickChangeFavourite(View view) {
        //ДОБАВЛЯЕМ ФИЛЬМ В ИЗБРАННОЕ
        // перед добавлением проверяем не находится ли там уже данный фильм,
        // если фильм уже в избранном, то удаляем его оттуда
        if (favouriteMovie == null) {
            //если фильма в БД нет, то нам надо его добавить
            viewModal.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            //если объект уже существует, то удаляем его
            viewModal.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite(); //устанавливаем нужное значение у favouriteMovie
    }

    private void setFavourite() {
        //получаем фильм
        favouriteMovie = viewModal.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            //если объект не добавлен, то установим серую звезду
            imageViewAddToFavourite.setImageResource(R.drawable.star_minus);
        } else {
            //иначе устанавливаем желтую звезду
            imageViewAddToFavourite.setImageResource(R.drawable.star_plus);
        }
    }

}
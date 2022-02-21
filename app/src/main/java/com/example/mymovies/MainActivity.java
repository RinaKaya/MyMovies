package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.adapters.MovieAdapter;
import com.example.mymovies.data.MainViewModal;
import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//чтобы показать, что MainActivity является слушателем загрузчика мы реализовываем интерфейс LoaderCallbacks
//в качестве параметров передаем данные, которые хотим получить из загрузчика, то есть JSONObject
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private MainViewModal viewModal;

    //ссылка на свитч
    private Switch switchSort;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private ProgressBar progressBarLoading;

    private RecyclerView recyclerViewPosters;

    //создаем адаптер
    private MovieAdapter movieAdapter;

    //уникальный идентификатор загрузчика
    private static final int LOADER_ID = 133; //указываем любое число
    //менеджер загрузок
    private LoaderManager loaderManager;

    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;

    private static String lang;

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

    //метод, который рассчитывает число колонок в зависимости от ширины экрана
    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //получаем характеристики экрана
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //получаем ширину экрана в аппаратно-независимых пикселях (dp)
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        //вычисляем количество колонок
        return width / 185 > 2 ? width / 185 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //получаем язык, который сейчас используется на устройстве
        lang = Locale.getDefault().getLanguage();

        //получаем экземпляр загрузчика, который отвечает за все загрузки, которые происходят в приложении
        loaderManager = LoaderManager.getInstance(this); //здесь используется паттерн синглтон, который изучали при создании БД

        viewModal = ViewModelProviders.of(this).get(MainViewModal.class);

        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        //тестирование адаптера для компонента RecyclerView
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        //расположение элементов сеткой в компоненте RecyclerView
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));

        movieAdapter = new MovieAdapter();

        //устанавливаем адаптер у RecyclerView
        recyclerViewPosters.setAdapter(movieAdapter);

        //чтобы фильмы сразу загрузились
        switchSort.setChecked(true);

        //добавляем слушатель для свитча
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheked) {
                page = 1; //если мы преключили метод сортировки
                setMethodOfSort(isCheked);
            }
        });
        switchSort.setChecked(false);

        //устанавливаем слушатель у адаптера
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                //Toast.makeText(MainActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();

                //получаем фильм, на который нажали
                Movie movie = movieAdapter.getMovies().get(position);
                //создаем интент для перехода в др. активность
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                //вставляем в интент информацию
                intent.putExtra("id", movie.getId());
                //запускаем активность
                startActivity(intent);
            }
        });
        //устанавливаем адаптер у слушателя
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) { //если загрузка не началась
                    //Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();

                    //если загрузка не началась, то мы должны ее начать
                    downloadData(methodOfSort, page);
                }
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = viewModal.getMovies();

        //добавляем наблюдателя
        //каждый раз, когда данные в БД будут меняться, то мы их будем устанавливать у адаптера
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                //при отсутствии Интернета берем все фильмы из БД и устанавливаем их в RecyclerView
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
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
        downloadData(methodOfSort, page);
    }

    //вынесли загрузку данных в отдельный метод
    //будем загружать данные в зависимости от способа сортировки и какая страница
    private void downloadData(int methodOfSort, int page) {
        //формируем url
        URL url = NetworkUtils.buildURL(methodOfSort, page, lang);

        //создаем объект Bundle
        Bundle bundle = new Bundle();

        //в этот bundle вставляем данные
        bundle.putString("url", url.toString());

        //теперь надо запустить загрузчика
        //метод restartLoader() проверит существует ли уже загрузчик, если нет, то он его создаст, вызвав метод initLoader()
        //а если загрузчик уже есть, то он его просто перезапустит
        //в параметры передаем: id загрузчика, затем bundle (который создали) и слушатель событий
        // (этот слушатель мы реализовали в MainActivity, поэтому передаем this)
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        //id - это уникальный идендификатор загрузчика
        //он может быть любым - его мы указываем сами, поэтому создадим для него переменную LOADER_ID

        //создаем загрузчик
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        //добавляем слушатель к загрузчику
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                //устанавливаем видимость прогресс-бара
                progressBarLoading.setVisibility(View.VISIBLE);

                isLoading = true;
            }
        });

        //возвращаем загрузчик
        return jsonLoader;

        //данные, которые мы получаем при завершении работы загрузчика, передаются в метод onLoadFinished()
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        //из JSONObject нам надо получить фильмы (мы это делаем в методе downloadData(), поэтому скопируем код оттуда)
        //то есть когда загрузчик завершит работу, он возьмет полученный объект JSONObject,
        //получит из него все фильмы и вставит их в БД

        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);

        //если мы получили новые данные и они не пустые
        if (movies != null && !movies.isEmpty()) {

            if (page == 1) {
                //тогда мы очистим предыдущие данные
                viewModal.deleteAllMovies();

                movieAdapter.clear(); //очищаем список компонента RecyclerView
            }

            //затем вставляем новые данные в цикле
            for (Movie movie : movies) {
                viewModal.insertMovie(movie);
            }

            //добавляем фильмы в адаптер
            movieAdapter.addMovies(movies);
            //когда данные загружены, мы увеличиваем page
            page++;
        }
        isLoading = false;

        //а когда загрузка завершена скрываем прогресс-бар
        progressBarLoading.setVisibility(View.INVISIBLE);

        //после того, как загрузка завершена необходимо, надо удалить этот загрузчик
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}
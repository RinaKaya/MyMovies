package com.example.mymovies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<Movie> movies;

    //создаем объект интерфейсного типа
    private OnPosterClickListener onPosterClickListener;

    private OnReachEndListener onReachEndListener;

    //пустой конструктор
    public MovieAdapter() {
        movies = new ArrayList<>();
    }

    interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    interface OnReachEndListener {
        //этот метод вызывается при достижении конца списка эл-тов
        void OnReachEnd();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        //если мы достигли конца списка и наш слушатель не равен null,
        if (position > movies.size() - 4 && onPosterClickListener != null) {
            onReachEndListener.OnReachEnd();
        }

        //в этом методе мы берем ImageView и устанавливаем у него изображение из фильма (из переменной posterPath)
        //т.к. в poster_path хранится не полный путь до картинки, то мы должны его составить
        //полный путь до картинки состоит из 3-х частей:
        // BASE_URL + папка с нужным размером картинок + путь до картинки из poster_path

        //получаем ImageView
        Movie movie = movies.get(position);
        ImageView imageView = holder.imageViewSmallPoster;

        //использование библиотеки Picasso для картинок
        //movie.getPosterPath() - это путь к изображению
        Picasso.get().load(movie.getPosterPath()).into(imageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        //здесь нам нужен доступ только к одному элементу
        private ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPosterClickListener != null) {
                        //getAdapterPosition() - передаем позицию адаптера
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    //добавим сеттер и геттер, чтобы мы могли добавить новый массив
    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    //когда мы будем пролистывать список фильмов, то понадобится добавлять новые фильмы в данный массив,
    //но не заменять весь старый массив новым, поэтому добавим метод
    public void addMovies(ArrayList<Movie> movies) {
        //берем наш массив и добавляем к нему новый
        this.movies.addAll(movies);

        //после того, как мы установили и добавили фильмы - надо оповестить об этом адаптер
        notifyDataSetChanged();
    }

}

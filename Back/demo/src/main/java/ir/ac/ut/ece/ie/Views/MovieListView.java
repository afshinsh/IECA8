package ir.ac.ut.ece.ie.Views;

import ir.ac.ut.ece.ie.Model.Movie;

import java.util.ArrayList;

public class MovieListView {
    public MovieListView(Movie movie) {
        id = movie.id;
        name = movie.name;
        director = movie.director;
        genres = movie.genres;
        rating = movie.rating;
        imdbRate = movie.imdbRate;
        releaseDate = movie.releaseDate;
        duration = movie.duration;
        image = movie.image;
        coverImage = movie.coverImage;
        concatGenres = "";
        if(movie.genres != null)
            for (String gen: movie.genres) {
                concatGenres += gen + " ";
            }
    }

    public int id;
    public String name;
    public String director;
    public String releaseDate;
    public long duration;
    public ArrayList<String> genres;
    public double rating;
    public double imdbRate;
    public String concatGenres;
    public String image;
    public String coverImage;
}

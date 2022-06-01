package ir.ac.ut.ece.ie.Views;

import ir.ac.ut.ece.ie.Model.Movie;
import ir.ac.ut.ece.ie.Storage.Storage;

import java.util.ArrayList;

public class GetAllMovieView {
    public GetAllMovieView(Movie movie){
        name = movie.name;
        summary = movie.summary;
        releaseDate = movie.releaseDate;
        director = movie.director;
        writers = movie.writers;
        genres = movie.genres;
        var castView = Storage.Database.GetMovieCast(movie.id);
        String castName = "";
        for(int i = 0; i < castView.size(); i++){
            castName += " " + castView.get(i).Name;
            if(i != castView.size() - 1)
                castName += ",";
        }
        castNames = castName;
        imdbRate = movie.imdbRate;
        rating = movie.rating;
        duration = movie.duration;
        ageLimit = movie.ageLimit;
    }
    public String name;
    public String summary;
    public String releaseDate;
    public String director;
    public ArrayList<String> writers;
    public ArrayList<String> genres;
    public String castNames;
    public double imdbRate;
    public double rating;
    public long duration;
    public int ageLimit;

}

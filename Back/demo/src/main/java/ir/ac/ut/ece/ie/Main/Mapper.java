package ir.ac.ut.ece.ie.Main;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.ac.ut.ece.ie.Model.Movie;
import ir.ac.ut.ece.ie.Storage.Storage;
import ir.ac.ut.ece.ie.Views.MovieListView;

import java.util.ArrayList;
import java.util.List;

public class Mapper {
    public static List<MovieListView> MapWatchList(ArrayList<Movie> watchList) throws JsonProcessingException {
        List<MovieListView> list = new ArrayList<>();
        for(Movie movie : watchList)
            list.add(new MovieListView(movie));
        return list;

    }

    public static List<MovieListView> MapGenreMovies(String genre) throws JsonProcessingException {
        List<MovieListView> list = new ArrayList<>();
        for(Movie movie : Storage.Database.Movies)
            if(movie.genres.contains(genre))
                list.add(new MovieListView(movie));
        return list;
    }
}

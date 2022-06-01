package ir.ac.ut.ece.ie.Model;

import java.util.ArrayList;

public class Movie implements Comparable<Movie> {
    public Movie(int id, String name, String summary, String releaseDate, String director, ArrayList<String> writers,
                 ArrayList<String> genres, ArrayList<Integer> cast, float imdbRate, int duration, int ageLimit){
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.releaseDate = releaseDate;
        this.director = director;
        this.writers = writers;
        this.genres = genres;
        this.cast = cast;
        this.imdbRate = imdbRate;
        this.duration = duration;
        this.ageLimit = ageLimit;
        this.rating = imdbRate;
        rates = new ArrayList<>();
    }

    public Movie(int id, String name, String summary, String releaseDate, String director
            , float imdbRate, int duration, int ageLimit, String image, String coverImage, float rating){
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.releaseDate = releaseDate;
        this.director = director;
        this.imdbRate = imdbRate;
        this.duration = duration;
        this.ageLimit = ageLimit;
        this.rating = rating;
        rates = new ArrayList<>();
        this.image = image;
        this.coverImage = coverImage;
    }
    public Movie(){
        id = -1;
    }
    public int id;
    public String name;
    public String summary;
    public String releaseDate;
    public String director;
    public ArrayList<String> writers;
    public ArrayList<String> genres;
    public ArrayList<Integer> cast;
    public float imdbRate;
    public float rating;
    public int duration;
    public int ageLimit;
    public Float tempScore;
    public String image;
    public String coverImage;
    public ArrayList<Rate> rates;


    private float getRating(){
        float sum = imdbRate;
        for (int i = 0; i < rates.size(); i++) {
            sum += rates.get(i).Score;
        }
        return sum / (rates.size() + 1) ;
    }
    private void updateRating(int before , int after){
        float sum = rating * (rates.size() + 1);
        sum = sum - before + after;
        rating = sum / (rates.size() + 1);
    }

    public void RateMovie(Rate rate){
        if(rates == null)
            rates = new ArrayList<>();
        for (Rate rt : rates)
            if(rt.UserEmail.equals(rate.UserEmail)){
                updateRating(rt.Score, rate.Score);
                rt = rate;
                return;
            }

        rates.add(rate);
        rating = getRating();
    }


    @Override
    public int compareTo(Movie o) {
        return tempScore.compareTo(o.tempScore);
    }
}

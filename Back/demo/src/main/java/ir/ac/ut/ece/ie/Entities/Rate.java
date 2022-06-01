package ir.ac.ut.ece.ie.Entities;

public class Rate {
    public Rate(String userEmail, int movieId, int score){
        userEmail = userEmail;
        movieId = movieId;
        score = score;
    }

    public String userEmail;
    public int movieId;
    public int score;
}

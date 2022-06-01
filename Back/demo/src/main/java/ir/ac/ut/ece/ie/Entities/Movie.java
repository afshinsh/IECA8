package ir.ac.ut.ece.ie.Entities;

public class Movie {
    public Movie(int id, String name, String summary, String releaseDate, String director,
                   float imdbRate, long duration, int ageLimit){

    }
    public Movie(){
        id = -1;
    }
    public int id;
    public String name;
    public String summary;
    public String releaseDate;
    public String director;
    public float imdbRate;
    public int duration;
    public int ageLimit;
    public String image;
    public String coverImage;
    public float rating;


    private float getRating(){
        /*float sum = imdbRate;
        for (int i = 0; i < rates.size(); i++) {
            sum += rates.get(i).Score;
        }
        return sum / (rates.size() + 1) ;*/
        return 1.4f;
    }
    private void updateRating(int before , int after){
/*        float sum = rating * (rates.size() + 1);
        sum = sum - before + after;
        rating = sum / (rates.size() + 1);*/
    }

    public void RateMovie(Rate rate){
        /*if(rates == null)
            rates = new ArrayList<>();
        for (Rate rt : rates)
            if(rt.UserEmail.equals(rate.UserEmail)){
                updateRating(rt.Score, rate.Score);
                rt = rate;
                return;
            }

        rates.add(rate);
        rating = getRating();*/
    }


}

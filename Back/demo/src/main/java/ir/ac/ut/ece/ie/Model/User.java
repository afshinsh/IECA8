package ir.ac.ut.ece.ie.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User {
    public User(String email, String password, String nickname, String name, String birthDate) throws ParseException {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        var strLength = birthDate.length();
        this.birthDate = new SimpleDateFormat("yyyy").parse(birthDate.substring(strLength - 4, strLength));
        this.watchList = new ArrayList<>();
    }
    public User(){}

    public int id;
    public String email;
    public String password;
    public String nickname;
    public String name;
    public Date birthDate;
    public ArrayList<Movie> watchList;

    public boolean canWatch(Movie movie) {
        long difference_In_Time
                = (new Date(System.currentTimeMillis())).getTime() - birthDate.getTime();
        long difference_In_Years
                = (difference_In_Time
                / (1000l * 60 * 60 * 24 * 365));
        if(difference_In_Years > movie.ageLimit)
            return true;
        return false;
    }

    public boolean addToWatchList(Movie movie) {
        if(watchList == null)
            watchList = new ArrayList<>();
        for (Movie mve : watchList)
            if(mve.id == movie.id)
                return false;
        watchList.add(movie);
        return true;
    }

    public void RemoveFromWatchList(Movie movie) throws Exception {
        for (Movie mve : watchList)
            if(mve.id == movie.id){
                watchList.remove(mve);
                return;
            }
        throw new Exception("MovieNotFound");


    }
}

package ir.ac.ut.ece.ie.Views;

import java.util.ArrayList;
import java.util.List;

public class SingleMovieView {
    public int Id;
    public String Name;
    public String Summary;
    public String ReleaseDate;
    public String Director;
    public ArrayList<String> Writers;
    public ArrayList<String> Genres;
    public List<CastView> Cast;
    public float ImdbRate;
    public float Rating;
    public long Duration;
    public int AgeLimit;
    public List<CommentView> Comments;
    public String Image;
    public String Cover;
}

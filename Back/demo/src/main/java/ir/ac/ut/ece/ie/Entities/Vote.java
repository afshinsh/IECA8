package ir.ac.ut.ece.ie.Entities;

public class Vote {
    public Vote(String userEmail, int commentId, int vote){
        userEmail = userEmail;
        commentId = commentId;
        vote = vote;
    }

    public String userEmail;
    public int commentId;
    public int vote;
}
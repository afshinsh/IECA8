package ir.ac.ut.ece.ie.repository;

import ir.ac.ut.ece.ie.Model.Vote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class VoteRepository  {
    private static final String TABLE_NAME = "vote";
    private static VoteRepository instance;


    public static VoteRepository getInstance() {
        if (instance == null) {
            try {
                instance = new VoteRepository();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error in IEMDBRepository.create query.");
            }
        }
        return instance;
    }

    private VoteRepository() throws SQLException {
        /*Connection con = ConnectionPool.getConnection();
        PreparedStatement createTableStatement = con.prepareStatement(
                String.format("CREATE TABLE IF NOT EXISTS %s;", TABLE_NAME)
        );
        createTableStatement.executeUpdate();
        createTableStatement.close();
        con.close();*/
    }

    public Vote CheckVoteStatus(int cmId, String email) throws SQLException {
        try{
            Connection con = ConnectionPool.getConnection();
            String query = "";
            query = "SELECT * FROM vote WHERE commentId=? and userEmail=?";
            PreparedStatement st = con.prepareStatement(query);
            st.setInt(1,cmId);
            st.setString(2,email);
            ResultSet rs = st.executeQuery();

            if(rs.next()){
                var newVote = new Vote(rs.getString("userEmail"), rs.getInt("commentId"), rs.getInt("vote"));
                st.close();
                con.close();
                return newVote;
            }

        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void DeleteVote(int cmId, String email) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "DELETE FROM vote WHERE commentId=? and userEmail=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,cmId);
        st.setString(2,email);
        st.executeUpdate();
        st.close();
        con.close();
    }


    public void AddVote(Vote vote) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "INSERT INTO vote(userEmail, commentId, vote)" +
                "VALUES(?,?,?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, vote.UserEmail);
        st.setInt(2, vote.CommentId);
        st.setInt(3, vote.Vote);
        st.executeUpdate();
        st.close();
        con.close();
    }
    public List<Vote> GetAllVotes() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM vote";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        List<Vote> votes = new ArrayList<Vote>();
        while (rs.next()){
            Vote vote = new Vote(rs.getString("userEmail"), rs.getInt("commentId"), rs.getInt("vote"));
            votes.add(vote);
        }
        st.close();
        con.close();
        return votes;
    }

    public void SetLikeAndDislike(int cmId, int like, int dislike) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "UPDATE comment\n" +
                "SET likeCount=?, dislikeCount=?\n" +
                "WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,like);
        st.setInt(2,dislike);
        st.setInt(3,cmId);
        st.executeUpdate();
        st.close();
        con.close();
    }


}

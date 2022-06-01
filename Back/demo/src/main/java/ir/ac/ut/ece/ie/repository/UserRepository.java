package ir.ac.ut.ece.ie.repository;

import ir.ac.ut.ece.ie.Model.Movie;
import ir.ac.ut.ece.ie.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class UserRepository  {
    private static UserRepository instance;


    public static UserRepository getInstance() {
        if (instance == null) {
            try {
                instance = new UserRepository();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error in IEMDBRepository.create query.");
            }
        }
        return instance;
    }

    private UserRepository() throws SQLException {

    }

    public void DeleteFromWatchList(int movieId, String email) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "DELETE FROM watchList WHERE movieId=? and userEmail=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,movieId);
        st.setString(2,email);
        st.executeUpdate();
        st.close();
        con.close();
    }


    public boolean AddToWatchList(int movieId, String email) throws SQLException {
            Connection con = ConnectionPool.getConnection();
            String query = "INSERT INTO watchList(userEmail, movieId)" +
                    "VALUES(?,?)";
            PreparedStatement st = con.prepareStatement(query);
            st.setString(1, email);
            st.setInt(2, movieId);
        try{
            st.executeUpdate();
            st.close();
            con.close();
            return true;
        }catch (Exception e){
            st.close();
            con.close();
            return false;
        }

    }
    public List<Movie> GetUserWatchList(String userEmail) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM movie m " +
                "inner join watchList w on m.id = w.movieId WHERE w.userEmail=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1,userEmail);
        ResultSet rs = st.executeQuery();
        List<Movie> movies = new ArrayList<Movie>();
        while (rs.next()){
            Movie movie = new Movie(rs.getInt("id"), rs.getString("name"), rs.getString("summary"),
                    rs.getString("releaseDate"), rs.getString("director"), rs.getFloat("imdbRate"),
                    rs.getInt("duration"), rs.getInt("ageLimit"), rs.getString("image")
                    ,rs.getString("coverImage"),
                    rs.getFloat("rating"));
            movies.add(movie);
        }
        st.close();

        for (var movie :
                movies) {
            query = "SELECT * FROM movieGenre WHERE movieId=?";
            st = con.prepareStatement(query);
            st.setInt(1,movie.id);
            rs = st.executeQuery();
            var genres = new ArrayList<String>();
            while(rs.next()) {
                genres.add(rs.getString("genre"));
            }
            movie.genres = genres;
            st.close();
        }
        con.close();
        return movies;
    }

    public User GetUserByEmail(String email) throws SQLException, ParseException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM user WHERE email=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1,email);
        ResultSet rs = st.executeQuery();
        User user = new User();
        if (rs.next()){
            user = new User(rs.getString("email"),rs.getString("password"),
                    rs.getString("nickname"),rs.getString("name"), rs.getString("birthDate"));
            st.close();
            con.close();
            return user;
        }
        else {
            st.close();
            con.close();
            return null;

        }
    }
    public boolean UserExists(String email) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM user WHERE email=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1,email);
        ResultSet rs = st.executeQuery();
        var result = rs.next();
        st.close();
        con.close();
        return result;
    }


    public void AddUser(String name, String username, String birthdate, String email, String password) throws SQLException {

        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "INSERT INTO user (email, password, name, nickname, birthDate)\n" +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1,email);
        st.setString(2,password);
        st.setString(3,name);
        st.setString(4,username);
        st.setString(5,birthdate);

        st.executeUpdate();
        st.close();
        con.close();

    }
}

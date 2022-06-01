package ir.ac.ut.ece.ie.repository;

import ir.ac.ut.ece.ie.Model.Comment;
import ir.ac.ut.ece.ie.Model.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MovieRepository  {
    private static final String TABLE_NAME = "Movie";
    private static MovieRepository instance;


    public static MovieRepository getInstance() {
        if (instance == null) {
            try {
                instance = new MovieRepository();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error in IEMDBRepository.create query.");
            }
        }
        return instance;
    }

    private MovieRepository() throws SQLException {
        /*Connection con = ConnectionPool.getConnection();
        PreparedStatement createTableStatement = con.prepareStatement(
                String.format("CREATE TABLE IF NOT EXISTS %s;", TABLE_NAME)
        );
        createTableStatement.executeUpdate();
        createTableStatement.close();
        con.close();*/
    }

    public Movie GetMovieById(int id) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM movie WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,id);
        ResultSet rs = st.executeQuery();
        Movie movie = new Movie();
        if (rs.next()){
            movie = new Movie(rs.getInt("id"), rs.getString("name"), rs.getString("summary"),
                    rs.getString("releaseDate"), rs.getString("director"), rs.getFloat("imdbRate"),
                    rs.getInt("duration"), rs.getInt("ageLimit"), rs.getString("image")
                    , rs.getString("coverImage"),
                    rs.getFloat("rating"));
            st.close();
        }
        else {
            st.close();
            return null;
        }
        query = "SELECT * FROM movieCast WHERE movieId=?";
        st = con.prepareStatement(query);
        st.setInt(1,id);
        rs = st.executeQuery();
        var cast = new ArrayList<Integer>();
        while(rs.next()) {
            cast.add(rs.getInt("actorId"));
        }
        movie.cast = cast;
        st.close();
        query = "SELECT * FROM movieGenre WHERE movieId=?";
        st = con.prepareStatement(query);
        st.setInt(1,id);
        rs = st.executeQuery();
        var genres = new ArrayList<String>();
        while(rs.next()) {
            genres.add(rs.getString("genre"));
        }
        movie.genres = genres;
        st.close();
        query = "SELECT * FROM movieWriter WHERE movieId=?";
        st = con.prepareStatement(query);
        st.setInt(1,id);
        rs = st.executeQuery();
        var writers = new ArrayList<String>();
        while(rs.next()) {
            writers.add(rs.getString("writer"));
        }
        movie.writers = writers;
        st.close();
        con.close();
        return movie;
    }

    public List<Movie> GetAllMovies() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM movie";
        PreparedStatement st = con.prepareStatement(query);
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

    public void AddCommentForMovie(int movieId, String text, String userEmail) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "INSERT INTO comment(submitDate, userEmail, movieId, text, likeCount, dislikeCount)" +
                "VALUES(?,?,?,?,?,?)";
        PreparedStatement st = con.prepareStatement(query);

        st.setString(1, new Date(System.currentTimeMillis()).toString());
        st.setString(2, userEmail);
        st.setInt(3, movieId);
        st.setString(4, text);
        st.setInt(5, 0);
        st.setInt(6, 0);
        st.executeUpdate();
        st.close();
        con.close();
    }


    public boolean MovieExists(int id) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM movie WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,id);
        ResultSet rs = st.executeQuery();
        var result = rs.next();
        st.close();
        con.close();
        return result;
    }

    public List<Comment> GetMovieComments(int movieId) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM comment " +
                "WHERE movieId=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,movieId);
        ResultSet rs = st.executeQuery();
        List<Comment> comments = new ArrayList<>();
        while (rs.next()){
            var comment = new Comment(rs.getInt("id"), rs.getString("userEmail"),
                    rs.getInt("movieId"), rs.getString("text"),
                    rs.getInt("likeCount"), rs.getInt("dislikeCount"));
            comments.add(comment);
        }
        st.close();
        con.close();
        return comments;
    }
}

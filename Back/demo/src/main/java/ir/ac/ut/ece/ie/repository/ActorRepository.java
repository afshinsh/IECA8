package ir.ac.ut.ece.ie.repository;

import ir.ac.ut.ece.ie.Model.Actor;
import ir.ac.ut.ece.ie.Model.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ActorRepository  {
    private static final String TABLE_NAME = "Movie";
    private static ActorRepository instance;


    public static ActorRepository getInstance() {
        if (instance == null) {
            try {
                instance = new ActorRepository();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error in IEMDBRepository.create query.");
            }
        }
        return instance;
    }

    private ActorRepository() throws SQLException {
        /*Connection con = ConnectionPool.getConnection();
        PreparedStatement createTableStatement = con.prepareStatement(
                String.format("CREATE TABLE IF NOT EXISTS %s;", TABLE_NAME)
        );
        createTableStatement.executeUpdate();
        createTableStatement.close();
        con.close();*/
    }

    public List<Movie> GetActorMovies(int actorId) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM movie m " +
                "inner join movieCast mc on m.id = mc.movieId WHERE mc.actorId=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,actorId);
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
        con.close();
        return movies;
    }

    public Actor GetActorById(int id) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        String query = "";
        query = "SELECT * FROM actor WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,id);
        ResultSet rs = st.executeQuery();
        Actor actor = new Actor();
        while (rs.next()){
            actor = new Actor(rs.getInt("id"), rs.getString("name"), rs.getString("birthDate"),
                    rs.getString("nationality"), rs.getString("image"));
        }
        st.close();
        con.close();
        return actor;
    }
}

package ir.ac.ut.ece.ie.repository;

import ir.ac.ut.ece.ie.Model.Actor;
import ir.ac.ut.ece.ie.Model.Comment;
import ir.ac.ut.ece.ie.Model.Movie;
import ir.ac.ut.ece.ie.Model.User;
import ir.ac.ut.ece.ie.Storage.Storage;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class ConnectionPool {
    private static BasicDataSource ds = new BasicDataSource();
    private final static String mysqlURL = "jdbc:mysql://mysql-standalone:3306";

    private final static String dbURL = "jdbc:mysql://mysql-standalone:3306/test";
    private final static String dbUserName = "sa";
    private final static String dbPassword = "password";

    public static void createPool() throws SQLException{
        try {

                Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        ds.setUsername(dbUserName);
        ds.setPassword(dbPassword);
        ds.setUrl(dbURL + "?useSSL=false");
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        setEncoding();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void setEncoding() throws SQLException{
        Connection connection = null;
        try {
            connection = getConnection();

        }catch (SQLException e){
            ds.setUrl(mysqlURL);
            var con = ds.getConnection();
            Statement statement = con.createStatement();
            statement.execute("create database if not exists test;");
            con.close();
            ds = new BasicDataSource();
            ds.setUsername(dbUserName);
            ds.setPassword(dbPassword);
            ds.setUrl(dbURL + "?useSSL=false");
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);
            connection = getConnection();
        }
        Statement statement = connection.createStatement();
        statement.execute("ALTER DATABASE test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        connection.close();
        statement.close();

    }

    public static void createDatabase() throws SQLException{
        Connection con = getConnection();
        PreparedStatement createDatabaseStatement = con.prepareStatement(
                String.format("create table movie(\n" +
                        "\tid\t\t\tinteger,\n" +
                        "\tname\t\tvarchar(100),\n" +
                        "\tsummary\t\ttext,\n" +
                        "\treleaseDate\tvarchar(100),\n" +
                        "\tdirector\tvarchar(100),\n" +
                        "\timdbRate\tfloat,\n" +
                        "\tduration\tinteger,\n" +
                        "\tageLimit\tinteger,\n" +
                        "\timage\t\tvarchar(200),\n" +
                        "\tcoverImage\tvarchar(200),\n" +
                        "\trating\t\tfloat,\n" +
                        "\tprimary key(id)\n" +
                        ");"


                )
        );
        createDatabaseStatement.executeUpdate();
        createDatabaseStatement.close();
        PreparedStatement createDatabaseStatement1 = con.prepareStatement(
                String.format("create table if not exists movieWriter(\n" +
                        "\tmovieId\t\tinteger,\n" +
                        "\twriter\t\tvarchar(100)\n" +
                        ");"


                )
        );
        createDatabaseStatement1.executeUpdate();
        createDatabaseStatement1.close();

        PreparedStatement createDatabaseStatement2 = con.prepareStatement(
                String.format("create table if not exists actor(\n" +
                        "\tid\t\t\t\tinteger,\n" +
                        "\tname\t\t\tvarchar(100),\n" +
                        "\tbirthDate\t\tvarchar(100),\n" +
                        "\tnationality\t\tvarchar(100),\n" +
                        "\timage\t\t\tvarchar(200),\n" +
                        "\tprimary key(id)\n" +
                        ");"


                )
        );
        createDatabaseStatement2.executeUpdate();
        createDatabaseStatement2.close();
        PreparedStatement createDatabaseStatement3 = con.prepareStatement(
                String.format("create table if not exists movieGenre(\n" +
                        "\tmovieId\t\tinteger,\n" +
                        "\tgenre\t\tvarchar(100)\n" +
                        ");"


                )
        );
        createDatabaseStatement3.executeUpdate();
        createDatabaseStatement3.close();
        PreparedStatement createDatabaseStatement4 = con.prepareStatement(
                String.format("create table if not exists movieCast(\n" +
                        "\tmovieId\t\tinteger,\n" +
                        "\tactorId\t\tinteger\n" +
                        ");"


                )
        );
        createDatabaseStatement4.executeUpdate();
        createDatabaseStatement4.close();
        PreparedStatement createDatabaseStatement5 = con.prepareStatement(
                String.format("create table if not exists user(\n" +
                        "\temail\t\t\tvarchar(100),\n" +
                        "\tpassword\t\tvarchar(100),\n" +
                        "\tname\t\t\tvarchar(100),\n" +
                        "\tnickname\t\tvarchar(100),\n" +
                        "\tbirthDate\t\tvarchar(100),\n" +
                        "\tprimary key(email)\n" +
                        ");"


                )
        );
        createDatabaseStatement5.executeUpdate();
        createDatabaseStatement5.close();
        PreparedStatement createDatabaseStatement6 = con.prepareStatement(
                String.format("create table if not exists rate(\n" +
                        "\tuserEmail\tvarchar(100),\n" +
                        "\tmovieId\t\tinteger,\n" +
                        "\tscore\t\tinteger,\n" +
                        "\tprimary key(userEmail, movieId)\n" +
                        ");"


                )
        );
        createDatabaseStatement6.executeUpdate();
        createDatabaseStatement6.close();
        PreparedStatement createDatabaseStatement7 = con.prepareStatement(
                String.format("create table if not exists comment(\n" +
                        "\tid\t\t\t\tinteger AUTO_INCREMENT,\n" +
                        "\tsubmitDate\t\tvarchar(100),\n" +
                        "\tuserEmail\t\tvarchar(100),\n" +
                        "\tmovieId\t\t\tinteger,\n" +
                        "\ttext\t\t\tTEXT,\n" +
                        "\tlikeCount\t\t\tinteger DEFAULT 0,\n" +
                        "\tdislikeCount\t\t\tinteger DEFAULT 0,\n" +
                        "\tprimary key(id)\n" +
                        ");\n"


                )
        );
        createDatabaseStatement7.executeUpdate();
        createDatabaseStatement7.close();
        PreparedStatement createDatabaseStatement8 = con.prepareStatement(
                String.format("create table if not exists vote(\n" +
                        "\tuserEmail\tvarchar(100),\n" +
                        "\tcommentId\tinteger,\n" +
                        "\tvote\t\tinteger,\n" +
                        "\tprimary key(userEmail, commentId)\n" +
                        ");"


                )
        );
        createDatabaseStatement8.executeUpdate();
        createDatabaseStatement8.close();
        PreparedStatement createDatabaseStatement9 = con.prepareStatement(
                String.format("create table if not exists watchList(\n" +
                        "\tuserEmail\tvarchar(100),\n" +
                        "\tmovieId\t\tinteger,\n" +
                        "\tprimary key(userEmail, movieId)\n" +
                        ");"


                )
        );
        createDatabaseStatement9.executeUpdate();
        createDatabaseStatement9.close();

        con.close();
    }
    public static void insertDataToDatabase() throws Exception{
        if(Storage.Database.SetInformations()) {
            Connection con = ds.getConnection();
            String query = "";
            for(User u : Storage.Database.Users) {
                query = "INSERT INTO user (email, password, name, nickname, birthDate)" +
                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement st = con.prepareStatement(query);
                st.setString(1, u.email);
                st.setString(2, u.password);
                st.setString(3, u.name);
                st.setString(4, u.nickname);
                st.setString(5, u.birthDate.toString());
                try{
                    st.executeUpdate();
                    st.close();
                }
                catch (Exception e){
                    System.out.println("    repeated User Id!");
                    st.close();
                }
            }
            System.out.println("Table User Created and Updated!");


            for(Actor a : Storage.Database.Actors) {
                    query = "INSERT INTO actor (id, name, birthDate, nationality, image)" +
                            "VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement st = con.prepareStatement(query);
                    st.setInt(1, a.id);
                    st.setString(2, a.name);
                    st.setString(3, a.birthDate);
                    st.setString(4, a.nationality);
                    st.setString(5, a.image);
                try{
                    st.executeUpdate();
                    st.close();
                }
                catch (Exception e){
                    System.out.println("    repeated Actor Id!");
                    st.close();
                }

            }
            System.out.println("Table Actor Created and Updated!");


            for(Movie m : Storage.Database.Movies) {
                query = "INSERT INTO movie (id, name, summary, releaseDate, director, imdbRate, duration, ageLimit, image,coverImage, rating)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
                PreparedStatement st = con.prepareStatement(query);
                st.setInt(1, m.id);
                st.setString(2, m.name);
                st.setString(3, m.summary);
                st.setString(4, m.releaseDate);
                st.setString(5, m.director);
                st.setFloat(6, m.imdbRate);
                st.setInt(7, m.duration);
                st.setInt(8, m.ageLimit);
                st.setString(9, m.image);
                st.setString(10, m.coverImage);
                try{
                    st.executeUpdate();
                    st.close();
                }
                catch (Exception e){
                    System.out.println("    repeated Movie Id!");
                    st.close();
                    continue;
                }                for (var s : m.cast) {
                    query = "INSERT INTO movieCast (movieId, actorId)" +
                            "VALUES (?, ?)";
                    st = con.prepareStatement(query);
                    st.setInt(1, m.id);
                    st.setInt(2, s);
                    st.executeUpdate();
                }
                for (var s : m.writers) {
                    query = "INSERT INTO movieWriter (movieId, writer)" +
                            "VALUES (?, ?)";
                    st = con.prepareStatement(query);
                    st.setInt(1, m.id);
                    st.setString(2, s);
                    st.executeUpdate();
                }
                for (var s : m.genres) {
                    query = "INSERT INTO movieGenre (movieId, genre)" +
                            "VALUES (?, ?)";
                    st = con.prepareStatement(query);
                    st.setInt(1, m.id);
                    st.setString(2, s);
                    st.executeUpdate();
                }

                st.close();
            }
            System.out.println("Table Movie Created and Updated!");

            for(Comment c : Storage.Database.Comments) {
                query = "INSERT INTO comment (id, submitDate, userEmail, movieId, text, likeCount, dislikeCount)" +
                        "VALUES (?, ?, ?, ?, ?, 0, 0)";
                PreparedStatement st = con.prepareStatement(query);
                st.setInt(1, c.id);
                if(c.submitDate != null)
                    st.setString(2, c.submitDate.toString());
                else
                    st.setString(2, "0001-01-01 00:00:00");

                st.setString(3, c.userEmail);
                st.setInt(4, c.movieId);
                st.setString(5, c.text);
                try{
                    st.executeUpdate();
                    st.close();
                }
                catch (Exception e){
                    System.out.println("    repeated comment Id!");
                    st.close();
                }
            }
            System.out.println("Table Comment Created and Updated!");
            con.close();
        }



    }
}


package ir.ac.ut.ece.ie.Storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import ir.ac.ut.ece.ie.Main.Mapper;
import ir.ac.ut.ece.ie.Model.*;
import ir.ac.ut.ece.ie.Model.Movie;
import ir.ac.ut.ece.ie.Views.CastView;
import ir.ac.ut.ece.ie.Views.CommentView;
import ir.ac.ut.ece.ie.Views.MovieListView;
import ir.ac.ut.ece.ie.Views.SingleMovieView;
import ir.ac.ut.ece.ie.repository.ActorRepository;
import ir.ac.ut.ece.ie.repository.MovieRepository;
import ir.ac.ut.ece.ie.repository.UserRepository;
import ir.ac.ut.ece.ie.repository.VoteRepository;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Storage {

    static public class Database {
        private static final MovieRepository movieRepository = MovieRepository.getInstance();
        private static final UserRepository userRepository = UserRepository.getInstance();
        private static final VoteRepository voteRepository = VoteRepository.getInstance();
        private static final ActorRepository actorRepository = ActorRepository.getInstance();
        public static List<Movie> Movies = new ArrayList<>();
        public static List<User> Users = new ArrayList<>();
        public static List<Actor> Actors = new ArrayList<>();
        public static List<Comment> Comments = new ArrayList<Comment>();
        public static List<Vote> Votes = new ArrayList<Vote>();
        public static int UserId = 1;
        public static boolean DataAddedd = false;
        public static boolean CreatePoolConnection = false;
        public static boolean DataFetchedInTables = false;
        public static boolean CreateDatabase = false;
        public static int CommentId = 1;

        public static User CurrentUser = null;

        public static boolean SetInformations()  {
            try{
                HttpResponse<JsonNode> movieResponse = Unirest.get("http://138.197.181.131:5000/api/v2/movies")
                        .asJson();
                HttpResponse<JsonNode> actorResponse = Unirest.get("http://138.197.181.131:5000/api/v2/actors")
                        .asJson();
                HttpResponse<JsonNode> userResponse = Unirest.get("http://138.197.181.131:5000/api/users")
                        .asJson();
                HttpResponse<JsonNode> commentResponse = Unirest.get("http://138.197.181.131:5000/api/comments")
                        .asJson();

                ObjectMapper objectMapper = new ObjectMapper();

                Movies = objectMapper.readValue(movieResponse.getBody().toString(), new TypeReference<List<Movie>>() {
                });
                Collections.sort(Movies, Database::CompareByImdbRate);
                SetRatingForMovies();
                Actors = objectMapper.readValue(actorResponse.getBody().toString(), new TypeReference<List<Actor>>() {
                });
                Users = objectMapper.readValue(userResponse.getBody().toString(), new TypeReference<List<User>>() {
                });
                AssignIdToUsers();
                Comments = objectMapper.readValue(commentResponse.getBody().toString(), new TypeReference<List<Comment>>() {
                });
                AssignIdToCommnet();
                Database.DataAddedd = true;
                return true;

            }catch (Exception e){
                return false;
            }
        }


        public static void AddActor(Actor actor) {
            for (Actor act : Actors) {
                if (act.id == actor.id) {
                    act = actor;
                    return;
                }
            }
            Actors.add(actor);
        }

        public static void AddMovie(Movie movie) throws Exception {
            var arr = movie.cast.toArray();
            for (int i = 0; i < movie.cast.size(); i++) {
                int actorId = Integer.valueOf(arr[i].toString());
                if (!ActorExists(actorId))
                    throw new Exception("ActorNotFound");
            }

            for (Movie mve : Movies) {
                if (mve.id == movie.id) {
                    mve = movie;
                    return;
                }
            }
            Movies.add(movie);
        }

        private static boolean ActorExists(int actorId) {
            for (Actor actor : Actors)
                if (actor.id == actorId)
                    return true;
            return false;
        }

        public static void AddUser(User user) {
            user.id = UserId++;
            Users.add(user);
        }

        public static Movie getMovieById(int id) {
            for (Movie mve : Movies)
                if (mve.id == id)
                    return mve;
            return null;

        }

        public static User LoginUser(String email, String pass) throws SQLException, ParseException {
            var user = userRepository.GetUserByEmail(email);
            if(user == null)
                return null;
            else if(user.password.equals(pass))
                return user;
            else
                return null;
        }

        public static User getUserByEmail(String email) throws SQLException, ParseException {
            return userRepository.GetUserByEmail(email);
        }

        public static User getUserById(int id) {
            for (User user : Users)
                if (user.id == id)
                    return user;
            return null;
        }

        public static void AddRateMovie(Rate rate) throws Exception {
            if (rate.Score > 10 || rate.Score < 1)
                throw new Exception("InvalidRateScore");
            if (getUserByEmail(rate.UserEmail) == null)
                throw new Exception("UserNotFound");
            Movie movie = getMovieById(rate.MovieId);
            if (movie == null)
                throw new Exception("MovieNotFound");
            movie.RateMovie(rate);
        }

        public static int GetNumOfRates(int id) {
            for (Movie mve : Movies)
                if (mve.id == id)
                    return mve.rates.size();
            return -1;
        }

        public static void AddWatchList(WatchList watchList) throws Exception {
            User user = userRepository.GetUserByEmail(watchList.UserEmail);
            Movie movie = movieRepository.GetMovieById(watchList.MovieId);
            NotFoundExceptions(user, movie);
            if (!user.canWatch(movie))
                throw new Exception("AgeLimitError");
            if (!userRepository.AddToWatchList(movie.id, user.email))
                throw new Exception("MovieAlreadyExists");

        }

        public static void RemoveFromWatchList(WatchList watchList) throws Exception {
            User user = userRepository.GetUserByEmail(watchList.UserEmail);
            Movie movie = movieRepository.GetMovieById(watchList.MovieId);
            NotFoundExceptions(user, movie);
            userRepository.DeleteFromWatchList(movie.id, user.email);
        }

        public static void NotFoundExceptions(User user, Movie movie) throws Exception {
            if (user == null)
                throw new Exception("UserNotFound");
            if (movie == null)
                throw new Exception("MovieNotFound");

        }

        public static List<MovieListView> GetUserWatchList() throws Exception {
            if (CurrentUser == null)
                new ArrayList<>();
            var watchList = userRepository.GetUserWatchList(CurrentUser.email);
            return Mapper.MapWatchList((ArrayList<Movie>) watchList);
        }

        public static void GetMoviesByGenre(String genre) throws JsonProcessingException {
            System.out.println("{\"data\":{\"MoviesListByGenre\": " + Mapper.MapGenreMovies(genre) + "}}");
        }

        public static List<Movie> GetMoviesListByGenre(String genre) {
            List<Movie> list = new ArrayList<>();
            for (Movie movie : Database.Movies)
                if (movie.genres.contains(genre))
                    list.add(movie);

            return list;
        }

        public static boolean MovieExists(int id) {
            for (Movie movie : Movies) {
                if (movie.id == id)
                    return true;
            }
            return false;
        }

        public static boolean UserExists(String email) throws SQLException {
            return userRepository.UserExists(email);
        }

        public static boolean CommentExists(int id) {
            for (Comment comment : Comments) {
                if (comment.id == id)
                    return true;
            }
            return false;
        }


        public static boolean AddComment(Comment comment) throws SQLException {
            if (!userRepository.UserExists(comment.userEmail)) {
                return false;
            }
            if (!movieRepository.MovieExists(comment.movieId)) {
                return false;
            }
           /* comment.id = CommentId++;
            Comments.add(comment);*/
            movieRepository.AddCommentForMovie(comment.movieId, comment.text, comment.userEmail);
            return true;
        }

        public static Vote GetVoteStatus(Vote voteInput) {
            for (Vote vote : Votes) {
                if (vote.CommentId == voteInput.CommentId && vote.UserEmail == voteInput.UserEmail)
                    return vote;
            }
            return null;
        }

        public static void AddVote(Vote vote) throws SQLException {
            //var previousVote = GetVoteStatus(vote);
            var previousVote = voteRepository.CheckVoteStatus(vote.CommentId, vote.UserEmail);
            if (previousVote != null) {
                //Votes.remove(previousVote);
                if (vote.Vote == previousVote.Vote)
                    return;
                voteRepository.DeleteVote(vote.CommentId, vote.UserEmail);
            }
            //Votes.add(vote);
            voteRepository.AddVote(vote);
            /*for (Comment cm : Comments) {
                if (cm.id == vote.CommentId) {
                    UpdateCommentVotes(cm);
                }
            }*/
            UpdateCommentVotes(vote.CommentId);

        }

        private static void UpdateCommentVotes(int cmId) throws SQLException {
            var like = 0;
            var dislike = 0;
            //GetAllVotes
            var votes = voteRepository.GetAllVotes();
            for (Vote vote : votes) {
                if (vote.CommentId == cmId) {
                    if (vote.Vote == 1)
                        like += 1;
                    else
                        dislike += 1;
                }
            }
            voteRepository.SetLikeAndDislike(cmId, like, dislike);
        }

        public static List<Movie> GetAllMovies() {
            return Movies;
        }

        public static SingleMovieView GetMovie(int id) throws Exception {
            Movie movie = movieRepository.GetMovieById(id);
/*
            for (Movie mve : Movies) {
                if (mve.id == id)
                    movie = mve;
            }
            if (movie.id == -1) {
                return null;
            }*/
            SingleMovieView view = new SingleMovieView();
            view.Id = movie.id;
            view.Name = movie.name;
            view.Summary = movie.summary;
            view.ReleaseDate = movie.releaseDate;
            view.Director = movie.director;
            view.Writers = movie.writers;
            view.Genres = movie.genres;
            view.ImdbRate = movie.imdbRate;
            view.Rating = movie.rating;
            view.Duration = movie.duration;
            view.AgeLimit = movie.ageLimit;
            view.Comments = GetMovieComments(id);
            //view.Cast = GetMovieCast(id);
            view.Cast = GetCastView(movie.cast);
            view.Image = movie.image;
            view.Cover = movie.coverImage;
            return view;
        }

        public static List<CastView> GetMovieCast(int id) {
            List<CastView> castViews = new ArrayList<CastView>();
            for (Movie mve : Movies) {
                if (mve.id == id) {
                    castViews = GetCastView(mve.cast);
                }
            }
            return castViews;
        }

        private static ArrayList<CastView> GetCastView(ArrayList<Integer> castIds) {
            ArrayList<CastView> result = new ArrayList<CastView>();
            var arr = castIds.toArray();
            for (int i = 0; i < castIds.size(); i++) {
                try {
                    //Actor actor = GetActorById(Integer.valueOf(arr[i].toString()));
                    Actor actor = actorRepository.GetActorById(Integer.valueOf(arr[i].toString()));
                    var strLength = actor.birthDate.length();
                    var year = actor.birthDate.substring(strLength - 4, strLength);
                    CastView cv = new CastView(actor.id, actor.name, 2022 - Integer.valueOf(year));
                    cv.Image = actor.image;
                    result.add(cv);
                } catch (Exception ex) {
                }

            }
            return result;
        }

        public static Actor GetActorById(Integer actorId) throws SQLException {
            return actorRepository.GetActorById(actorId);
        }


        private static List<CommentView> GetMovieComments(int id) throws SQLException, ParseException {
            List<CommentView> comments = new ArrayList<CommentView>();
            var DBcomments = movieRepository.GetMovieComments(id);
            for (Comment cm : DBcomments) {
                /*if (cm.movieId == id) {
                    var user = getUserByEmail(cm.userEmail);
                    if (user != null)*/
                var user = userRepository.GetUserByEmail(cm.userEmail);
                comments.add(new CommentView(cm, user.nickname));
            }
            return comments;
        }

        public static int GetCommentLike(int commentId) {

            for (Comment comment : Comments) {
                if (comment.id == commentId)
                    return comment.like;
            }
            return -1;
        }

        public static ArrayList<Movie> GetTotalMovieActedIn(int actorId) throws SQLException {
            ArrayList<Movie> movieList = (ArrayList<Movie>) actorRepository.GetActorMovies(actorId);
            return movieList;

        }

        public static void AssignIdToUsers() {
            int i = 0;
            for (User user : Users) {
                i++;
                user.id = i;
                user.watchList = new ArrayList<>();
            }
        }

        public static void AssignIdToCommnet() {
            int i = 0;
            for (Comment cm : Comments) {
                i++;
                cm.id = i;
            }
        }

        public static List<Movie> GetMovieByYear(int startDate, int endDate) {
            List<Movie> movieList = new ArrayList<>();
            for (Movie movie : Movies) {
                var year = movie.releaseDate.substring(0, 4);
                if (Integer.valueOf(year) <= endDate && Integer.valueOf(year) >= startDate)
                    movieList.add(movie);
            }
            Collections.reverse(movieList);
            return movieList;
        }

        public static void SetRatingForMovies() {
            for (Movie movie : Movies)
                movie.rating = 0;
        }

        public static List<Movie> GetMoviesByFilter(String searchTerm, String startDate, String endDate, String sortValue) {
            if (searchTerm == null && startDate == null && endDate == null && sortValue == null){
                Collections.sort(Movies, Database::CompareByImdbRate);
                Collections.reverse(Movies);
                return Movies;
            }
            if (searchTerm == null && startDate == null && endDate == null){
                if(sortValue != null && Integer.valueOf(sortValue) == 1)
                    Collections.sort(Movies, Database::CompareByReleaseDate);

                if(sortValue != null && Integer.valueOf(sortValue) == -1)
                    Collections.sort(Movies, Database::CompareByImdbRate);
                Collections.reverse(Movies);
                return Movies;
            }

            if (searchTerm == null)
                return GetMovieByYear(Integer.valueOf(startDate), Integer.valueOf(endDate));
            List<Movie> result = new ArrayList<>();
            for (Movie mve : Movies) {
                if (mve.genres.contains(searchTerm) || mve.name.contains(searchTerm))
                    result.add(mve);
            }
            if(sortValue != null && Integer.valueOf(sortValue) == 1)
                Collections.sort(result, Database::CompareByReleaseDate);
            else
                Collections.sort(result, Database::CompareByImdbRate);

            Collections.reverse(result);
            return result;
        }
        public static int CompareByReleaseDate(Movie m1, Movie m2){
            return Integer.compare(Integer.valueOf(m1.releaseDate.substring(0, 4)), Integer.valueOf(m2.releaseDate.substring(0,4)));
        }
        public static int CompareByImdbRate(Movie m1, Movie m2){
            return Double.compare(m1.imdbRate, m2.imdbRate);
        }

        public static int CompareById(Movie m1, Movie m2){
            return Integer.compare(m1.id, m2.id);
        }

        public static List<Movie> GetRecommendedWatchList() throws SQLException {
            if(Database.CurrentUser == null)
                return new ArrayList<>();
            ArrayList<Movie> watchList = (ArrayList<Movie>) userRepository.GetUserWatchList(CurrentUser.email);
            ArrayList<Movie> tempMovies = (ArrayList<Movie>) ((ArrayList<Movie>) movieRepository.GetAllMovies()).clone();
            for (var movie : tempMovies) {
                movie.tempScore = movie.imdbRate + movie.rating + 3 * GenerateScore(movie, watchList);
            }

            Collections.sort(tempMovies, Collections.reverseOrder());
            ArrayList<Movie> top3 = new ArrayList<>();
            for (int i = 0; i < 3; i++)
                top3.add(tempMovies.get(i));
            return top3;
        }

        private static int GenerateScore(Movie movie, ArrayList<Movie> watchList) {

            int totalSimilar = 0;
            for (var w_movie : watchList) {
                totalSimilar += NumberOfSameGenre(movie, w_movie);
            }
            return totalSimilar;
        }

        private static int NumberOfSameGenre(Movie movie, Movie w_movie) {
            int total = 0;
            for (var genre : movie.genres) {
                for (var w_genre : w_movie.genres) {
                    if(w_genre.equals(genre))
                        total++;
                }
            }
            return total;
        }

        public static List<Movie> GetMovies() throws SQLException {
            //return Movies;
            return movieRepository.GetAllMovies();
        }

        public static void SignUpUser(String name, String username, String birthDate, String email, String password) throws SQLException {
            userRepository.AddUser(name, username, birthDate, email, password);
        }
    }
}

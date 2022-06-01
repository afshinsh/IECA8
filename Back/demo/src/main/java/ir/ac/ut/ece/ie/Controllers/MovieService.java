package ir.ac.ut.ece.ie.Controllers;

import ir.ac.ut.ece.ie.Model.*;
import ir.ac.ut.ece.ie.Security.JwtUtils;
import ir.ac.ut.ece.ie.Storage.Storage;
import ir.ac.ut.ece.ie.Views.SingleMovieView;
import ir.ac.ut.ece.ie.repository.MovieRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MovieService {

    @RequestMapping(value = "/addComment", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse<Boolean> AddComment(
            @RequestParam(value = "movie_id") int movie_id,
            @RequestParam(value = "commentText") String commentText,
            @RequestHeader(value = "Authorization", required = false) String token){
        if(JwtUtils.verifyJWT(token) == null)
            return new ServiceResponse<>(null, false, "401", "UnAuthorized");
        Comment comment = new Comment(Storage.Database.CurrentUser.email, movie_id, commentText);
        try {
            var response = Storage.Database.AddComment(comment);

            return new ServiceResponse<>(true, true, "200", "Success");
        } catch (Exception e) {
            return new ServiceResponse<>(null, false, "401", e.getMessage());
        }
    }



    @RequestMapping(value = "/rateMovie", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse<Rate> RateMovie(
            @RequestParam(value = "movie_id") int movie_id,
            @RequestParam(value = "quantity") int rate,
            @RequestHeader(value = "Authorization", required = false) String token){
        if(JwtUtils.verifyJWT(token) == null)
            return new ServiceResponse<>(null, false, "401", "UnAuthorized");
        try {
            Storage.Database.AddRateMovie(new Rate(Storage.Database.CurrentUser.email, movie_id, rate));
            return new ServiceResponse<>(null, true, "200", "success");
        } catch (Exception e) {
            return new ServiceResponse<>(null, false, "401", e.getMessage());
        }
    }

    @RequestMapping(value = "/voteComment", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse<String> voteComment(
            @RequestParam(value = "movie_id") int movie_id,
            @RequestParam(value = "comment_id") int comment_id,
            @RequestParam(value = "voteValue") int voteValue,
            @RequestHeader(value = "Authorization", required = false) String token){
        if(JwtUtils.verifyJWT(token) == null)
            return new ServiceResponse<>(null, false, "401", "UnAuthorized");


        try {
            if(Storage.Database.CurrentUser == null)
                return new ServiceResponse<>(null, false, "401", "failed");
            Vote vote = new Vote(Storage.Database.CurrentUser.email, Integer.valueOf(comment_id), voteValue);
            Storage.Database.AddVote(vote);
            return new ServiceResponse<String>(String.valueOf(comment_id), true, "200", "success");
        } catch (Exception e) {
            return new ServiceResponse<>(null, false, "401", e.getMessage());
        }
    }

    @RequestMapping(value = "/GetMovieById/{movie_id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse<SingleMovieView> GetMovieById(
            @PathVariable(value = "movie_id") int movie_id){

        try {
            SingleMovieView result =  Storage.Database.GetMovie(movie_id);
            return new ServiceResponse<>(result, true, "200", "success");
        } catch (Exception e) {
            return new ServiceResponse<>(null, false, "401", e.getMessage());
        }
    }

    @RequestMapping(value = "/GetMoviesByFilter", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse<List<Movie>> GetMoviesByFilter(
            @RequestParam(value = "searchTerm") String searchTerm,
            @RequestParam(value = "startDate")String startDate,
            @RequestParam(value = "endDate")String endDate,
            @RequestParam(value = "sortValue")String sortValue){

        try {
            var result = Storage.Database.GetMoviesByFilter(searchTerm, startDate, endDate, sortValue);
            return new ServiceResponse<>(result, false, "200", "message");
        } catch (Exception e) {
            return new ServiceResponse<>(null, false, "401", e.getMessage()+ " " + e.toString());
        }
    }
    @RequestMapping(value = "/GetMovies", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Movie> GetMovies(){

        try {
            var result = Storage.Database.GetMovies();
            return result;
        } catch (Exception e) {

        }
        return null;
    }
    @RequestMapping(value = "/searchMovies", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Movie> searchMovies(
            @RequestParam(value = "searchTerm") String searchTerm,
            @RequestParam(value = "searchType")String searchType){

        try {
            var result = GetMovies();

            if(searchType.equals("1"))
                result = Storage.Database.GetMoviesByFilter(searchTerm,"","","");
            if(searchType.equals("2")){
                String startDate = "1800";
                String endDate = "2025";
                try{
                    startDate = searchTerm.substring(0,3);
                    endDate = searchTerm.substring(5,8);
                    result = Storage.Database.GetMoviesByFilter("",startDate,endDate,"");
                }catch (Exception e){
                    return result;
                }
            }
            if(searchType.equals("3"))
                result = Storage.Database.GetMoviesByFilter(searchTerm,"","","");

            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }
    @RequestMapping(value = "/Test", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String Test(){
        try {
            User user = Storage.Database.getUserByEmail("sara@ut.ac.ir");
            return user.email;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
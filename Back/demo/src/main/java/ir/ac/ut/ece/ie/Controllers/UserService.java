package ir.ac.ut.ece.ie.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ece.ie.Model.*;
import ir.ac.ut.ece.ie.Security.JwtUtils;
import ir.ac.ut.ece.ie.Storage.Storage;
import ir.ac.ut.ece.ie.Views.CallBackView;
import ir.ac.ut.ece.ie.Views.LoginView;
import ir.ac.ut.ece.ie.Views.MovieListView;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

@RestController(value = "api")
public class UserService {

    @RequestMapping(value = "/AddWatchList", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse AddWatchList(
            @RequestParam(value = "movie_id") int movie_id,
            @RequestHeader(value = "Authorization", required = false) String token){
        if(JwtUtils.verifyJWT(token) == null)
            return new ServiceResponse<>(null, false, "401", "UnAuthorized");
        try {
            Storage.Database.AddWatchList(new WatchList(Storage.Database.CurrentUser.email, movie_id));
            return new ServiceResponse(null, true, "200", "success");
        } catch (Exception e) {
            return new ServiceResponse(null, false, "401", e.getMessage());
        }
    }



    @RequestMapping(value = "/RemoveWatchList/{movie_id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse RemoveWatchList(
            @PathVariable(value = "movie_id") int movie_id,
            @RequestHeader(value = "Authorization", required = false) String token){
        if(JwtUtils.verifyJWT(token) == null)
            return new ServiceResponse<>(null, false, "401", "UnAuthorized");

        try {
            Storage.Database.RemoveFromWatchList(new WatchList(Storage.Database.CurrentUser.email, movie_id));
            List<MovieListView> movieList = Storage.Database.GetUserWatchList();

            return new ServiceResponse(movieList, true, "200", "success");
        }
        catch (Exception e){
            return new ServiceResponse(null, false, "401", e.getMessage());
        }
    }

    @RequestMapping(value = "/GetWatchList", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse GetWatchList(@RequestHeader(value = "Authorization", required = false) String token){
        if(JwtUtils.verifyJWT(token) == null)
            return new ServiceResponse<>(null, false, "401", "UnAuthorized");
        try {
            List<MovieListView> movieList = Storage.Database.GetUserWatchList();

            return new ServiceResponse(movieList, true, "200", "success");
        }
        catch (Exception e){
            return new ServiceResponse(null, false, "401", e.getMessage());
        }
    }

    @RequestMapping(value = "/GetRecommendedMovies", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResponse GetRecommendedMovies(@RequestHeader(value = "Authorization", required = false) String token){

        try {
            List<Movie> recommendList = Storage.Database.GetRecommendedWatchList();
            return new ServiceResponse(recommendList, true, "200", "success");
        }
        catch (Exception e){
            return new ServiceResponse(null, false, "401", e.getMessage());
        }
    }

    @RequestMapping(value = "/Login", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginView Login(
            @RequestParam(value = "email") String email, @RequestParam(value = "pass") String pass){
        try {
            User user = Storage.Database.LoginUser(email, pass);
            if(user != null){
                Storage.Database.CurrentUser = user;
                String token = JwtUtils.createJWT(user.email, 24);
                return new LoginView(true, "Login Successfully!", token);
            }
            else
                return new LoginView(false, "Login Failed! UserName or Password Invalid!", null);
        }
        catch (Exception e){
            return new LoginView(false, e.getMessage(), null);
        }
    }

    @RequestMapping(value = "/SignUp", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginView SignUp(
            @RequestParam(value = "name") String name, @RequestParam(value = "username") String username,
            @RequestParam(value = "birthDate") String birthDate, @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password){
        try {
            Storage.Database.SignUpUser(name, username, birthDate, email, password);
            Storage.Database.CurrentUser = Storage.Database.getUserByEmail(email);
            String token = JwtUtils.createJWT(email, 24);
            return new LoginView(true, "Login Successfully!", token);
        }
        catch (Exception e){
            return new LoginView(false, e.getMessage(), null);
        }
    }

    @RequestMapping(value = "/Logout", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void LogOut(@RequestHeader(value = "Authorization", required = false) String token){

        if(JwtUtils.verifyJWT(token) == null)
            return ;
        Storage.Database.CurrentUser = null;
    }

    @RequestMapping(value = "/Callback/{code}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CallBackView Callback(
            @PathVariable(value = "code") String code){
        try {
            String clientId = "02b0f53ba02b7dacc0b5";
            String clientSecret = "8eacb010129781fd5f3a54fd7126b93990986662";
            String accessTokenUrl = String.format(
                    "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s",
                    clientId, clientSecret, code
            );
            HttpClient client = HttpClient.newHttpClient();
            URI accessTokenUri = URI.create(accessTokenUrl);
            HttpRequest.Builder accessTokenBuilder = HttpRequest.newBuilder().uri(accessTokenUri);
            HttpRequest accessTokenRequest = accessTokenBuilder
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> accessTokenResult = client.send(accessTokenRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> resultBody = mapper.readValue(accessTokenResult.body(), HashMap.class);
            String accessToken = (String) resultBody.get("access_token");


            URI userDataUri = URI.create("https://api.github.com/user");
            HttpRequest.Builder userDataBuilder = HttpRequest.newBuilder().uri(userDataUri);
            HttpRequest req = userDataBuilder.GET()
                    .header("Authorization", String.format("token %s", accessToken))
                    .build();
            HttpResponse<String> userDataResult = client.send(req, HttpResponse.BodyHandlers.ofString());
            var userData = userDataResult.body();
            Object obj= JSONValue.parse(userData);
            JSONObject jsonObject = (JSONObject) obj;
            var username = (String) jsonObject.get("login");
            var name = (String) jsonObject.get("name");
            var email = (String) jsonObject.get("email");
            boolean userExists = Storage.Database.UserExists(email);

            if(!userExists){
                Storage.Database.SignUpUser(name, username, "2000-01-01", email, null);

            }
            Storage.Database.CurrentUser = Storage.Database.getUserByEmail(email);
            String token = JwtUtils.createJWT(email, 24);
            return new CallBackView(email, token);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        return null;
    }

}
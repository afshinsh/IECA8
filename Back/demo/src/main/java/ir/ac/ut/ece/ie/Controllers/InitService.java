package ir.ac.ut.ece.ie.Controllers;

import ir.ac.ut.ece.ie.Storage.Storage;
import ir.ac.ut.ece.ie.repository.ConnectionPool;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.text.ParseException;

@RestController
public class InitService {


    @RequestMapping(value = "/SetInfo", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setInfo(@RequestParam(value = "email", required = false) String email) throws SQLException, ParseException {

        if (!Storage.Database.DataAddedd)
            Storage.Database.SetInformations();



        try {
            if(!Storage.Database.CreatePoolConnection){
                ConnectionPool.createPool();
                Storage.Database.CreatePoolConnection = true;
            }
            if(!email.equals("empty"))
                Storage.Database.CurrentUser = Storage.Database.getUserByEmail(email);
            ConnectionPool.createDatabase();
            ConnectionPool.insertDataToDatabase();
            System.out.println("Database created successfully...");

        } catch (Exception e ) {
            System.out.println("Database Already Exists");
        }

    }

}

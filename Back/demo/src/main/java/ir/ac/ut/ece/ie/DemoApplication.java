package ir.ac.ut.ece.ie;

import ir.ac.ut.ece.ie.Storage.Storage;
import ir.ac.ut.ece.ie.repository.ConnectionPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {


    @GetMapping("/")
    public String Get(){
        return "Spring Boot Running!";
    }

    public static void main(String[] args) {
        if (!Storage.Database.DataAddedd)
            Storage.Database.SetInformations();

        try {
            if(!Storage.Database.CreatePoolConnection){
                ConnectionPool.createPool();
                Storage.Database.CreatePoolConnection = true;
            }

            ConnectionPool.createDatabase();
            ConnectionPool.insertDataToDatabase();
            System.out.println("Database created successfully...");

        } catch (Exception e ) {
            System.out.println(e.getMessage());
        }
        SpringApplication.run(DemoApplication.class, args);
    }


}

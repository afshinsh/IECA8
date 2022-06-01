package ir.ac.ut.ece.ie.Model;

public class Actor {
    public Actor(int id, String name, String birthday, String nationality){
        this.id = id;
        this.name = name;
        this.birthDate = birthday;
        this.nationality = nationality;
    }
    public Actor(){}
    public Actor(int id, String name, String birthday, String nationality, String image){
        this.id = id;
        this.name = name;
        this.birthDate = birthday;
        this.nationality = nationality;
        this.image = image;
    }
    public int id;
    public String name;
    public String birthDate;
    public String nationality;
    public String image;

}

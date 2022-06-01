package ir.ac.ut.ece.ie.Controllers;

public class ServiceResponse <T>{
    public ServiceResponse(T data, boolean success, String statusCode, String message){
        Data = data;
        Success = success;
        StatusCode = statusCode;
        Message = message;
    }
    public T Data;
    public boolean Success = true;
    public String StatusCode = "200";
    public String Message;
}

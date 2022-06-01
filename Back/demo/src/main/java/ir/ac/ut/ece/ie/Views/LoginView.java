package ir.ac.ut.ece.ie.Views;

public class LoginView {
    public LoginView(boolean _valid, String _message, String _token){
        valid = _valid;
        message = _message;
        token = _token;
    }
    public boolean valid;
    public String message;
    public String token;


    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

public class IncorrectActionException extends Exception {
    private String msg;

    public IncorrectActionException() {
        this.msg = "Not a valid action";
    }

    public IncorrectActionException(String msg) {
        super(msg);
    }

    public String getIncorrectActionMsg() {
        return this.msg;
    }
}

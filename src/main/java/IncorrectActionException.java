public class IncorrectActionException extends Exception {
    protected String msg;

    public IncorrectActionException() {
        this.msg = "Not a valid action";
    }

    public IncorrectActionException(String msg) {
        super(msg);
        this.msg = msg;
    }
}

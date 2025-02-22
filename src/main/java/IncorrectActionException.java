public class IncorrectActionException extends Exception {
    public IncorrectActionException() {
        super("Not a valid action");
    }

    public IncorrectActionException(String msg) {
        super(msg);
    }
}

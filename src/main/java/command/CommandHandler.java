package command;

public interface CommandHandler {
    boolean canHandle(String command);
    String handle(String command);
}

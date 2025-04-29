package command;

import java.util.Deque;

public class UndoHandler implements CommandHandler {
    private final Deque<Runnable> undoStack;

    public UndoHandler(Deque<Runnable> undoStack) {
        this.undoStack = undoStack;
    }

    @Override
    public boolean canHandle(String command) {
        return command.equals("undo");
    }

    @Override
    public String handle(String command) {
        if (undoStack.isEmpty()) return "No command to undo";
        undoStack.pop().run();
        return "Undo successful";
    }
}

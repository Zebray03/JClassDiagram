package command;

import diagram.ClassDiagram;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandLineTool {
    private final ClassDiagram diagram;
    private final Deque<Runnable> undoStack = new ArrayDeque<>();
    private final CommandHandler[] handlers;

    public CommandLineTool(ClassDiagram diagram) {
        this.diagram = diagram;
        this.handlers = new CommandHandler[]{
                new AddHandler(diagram, undoStack),
                new DeleteHandler(diagram, undoStack),
                new ModifyHandler(diagram, undoStack),
                new UndoHandler(undoStack),
                new QueryHandler(diagram),
                new SmellHandler(diagram)
        };
    }

    public String execute(String command) {
        try {
            for (CommandHandler handler : handlers) {
                if (handler.canHandle(command)) {
                    return handler.handle(command);
                }
            }
            return "Unknown command: " + command;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
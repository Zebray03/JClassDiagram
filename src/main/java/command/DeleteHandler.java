package command;

import command.utils.CommandUtils;
import diagram.ClassDiagram;
import diagram.model.Attribute;
import diagram.model.ClassInfo;
import diagram.model.Method;
import diagram.model.Relationship;

import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteHandler implements CommandHandler {
    private final ClassDiagram diagram;
    private final Deque<Runnable> undoStack;

    public DeleteHandler(ClassDiagram diagram, Deque<Runnable> undoStack) {
        this.diagram = diagram;
        this.undoStack = undoStack;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("delete");
    }

    @Override
    public String handle(String command) {
        if (command.matches("delete -([cie])\\s+.+")) {
            return deleteClass(command);
        } else if (command.matches("delete field\\s+.+")) {
            return deleteField(command);
        } else if (command.matches("delete function\\s+.+")) {
            return deleteMethod(command);
        }
        return "Invalid delete command";
    }

    private String deleteClass(String command) {
        Matcher m = Pattern.compile("delete -(?<type>[cie])\\s+(?<name>\\w+)").matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid delete command");

        String name = m.group("name");
        ClassInfo cls = CommandUtils.findClass(diagram, name);
        List<Relationship> delRelationshipList = diagram.getRelationships().stream()
                .filter(r -> r.getSource().equals(name) || r.getTarget().equals(name))
                .toList();

        diagram.getClasses().remove(cls);
        diagram.getRelationships().removeAll(delRelationshipList);

        undoStack.push(() -> {
            diagram.addClass(cls);
            diagram.getRelationships().addAll(delRelationshipList);
        });

        return "Deleted class: " + name;
    }

    private String deleteField(String command) {
        Matcher m = Pattern.compile("delete field (?<target>\\w+) -n (?<name>\\w+)").matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid field delete command");

        ClassInfo cls = CommandUtils.findClass(diagram, m.group("target"));
        Attribute attr = cls.getAttributes().stream()
                .filter(a -> a.getName().equals(m.group("name")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Field not found"));

        cls.getAttributes().remove(attr);
        undoStack.push(() -> cls.getAttributes().add(attr));
        return "Deleted field: " + attr.getName();
    }

    private String deleteMethod(String command) {
        Matcher m = Pattern.compile("delete function (?<target>\\w+) -n (?<name>\\w+)").matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid method delete command");

        ClassInfo cls = CommandUtils.findClass(diagram, m.group("target"));
        Method method = cls.getMethods().stream()
                .filter(mtd -> mtd.getName().equals(m.group("name")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Method not found"));

        cls.getMethods().remove(method);
        undoStack.push(() -> cls.getMethods().add(method));
        return "Deleted method: " + method.getName();
    }
}

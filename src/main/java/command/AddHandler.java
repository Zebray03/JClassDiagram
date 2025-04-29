package command;

import diagram.ClassDiagram;
import diagram.model.Attribute;
import diagram.model.ClassInfo;
import diagram.model.Method;

import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import command.utils.CommandUtils;


class AddHandler implements CommandHandler {
    private final ClassDiagram diagram;
    private final Deque<Runnable> undoStack;

    public AddHandler(ClassDiagram diagram, Deque<Runnable> undoStack) {
        this.diagram = diagram;
        this.undoStack = undoStack;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("add");
    }

    @Override
    public String handle(String command) {
        if (command.matches("add -(c|i|e)\\s+.+")) {
            return addClass(command);
        } else if (command.matches("add field\\s+.+")) {
            return addField(command);
        } else if (command.matches("add function\\s+.+")) {
            return addMethod(command);
        }
        return "Invalid add command";
    }

    private String addClass(String command) {
        Matcher m = Pattern.compile(
                        "add -(?<type>[cie])\\s+(?<name>\\w+)" +
                                "(\\s+--abstract|\\s+--values=([\\w,]+))?")
                .matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid class command");

        String type = m.group("type");
        String name = m.group("name");
        ClassInfo cls = new ClassInfo();
        cls.setName(name);

        switch (type) {
            case "c":
                cls.setAbstract(command.contains("--abstract"));
                break;
            case "i":
                cls.setInterface(true);
                break;
            case "e":
                cls.setEnum(true);
                if (command.contains("--values=")) {
                    String values = command.split("--values=")[1].split("\\s+")[0];
                    cls.setEnumConstants(List.of(values.split(",")));
                }
                break;
        }

        diagram.addClass(cls);
        undoStack.push(() -> {
            diagram.getClasses().remove(cls);
            diagram.getRelationships().removeIf(r ->
                    r.getSource().equals(name) || r.getTarget().equals(name)
            );
        });
        return "Added " + getTypeName(type) + ": " + name;
    }

    private String addField(String command) {
        Matcher m = Pattern.compile(
                        "add field (?<target>\\w+)\\s+" +
                                "-n (?<name>\\w+)\\s+" +
                                "-t (?<type>[\\w<>]+)\\s*" +
                                "(?:--access=(?<access>[+#~-])?\\s*)?(?:--static)?")
                .matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid field command");

        ClassInfo cls = CommandUtils.findClass(diagram, m.group("target"));
        Attribute attr = new Attribute();
        attr.setName(m.group("name"));
        attr.setType(m.group("type"));
        attr.setVisibility(CommandUtils.parseVisibility(m.group("access")));
        attr.setStatic(command.contains("--static"));

        cls.getAttributes().add(attr);
        undoStack.push(() -> cls.getAttributes().remove(attr));
        return "Added field: " + attr.getName();
    }

    private String addMethod(String command) {
        Matcher m = Pattern.compile(
                        "add function (?<target>\\w+)\\s+" +
                                "-n (?<name>\\w+)\\s+" +
                                "-t (?<ret>[\\w<>]+)\\s*" +
                                "(?:--params=(?<params>[\\w:,<>]+)\\s*)?(?:--access=([+#~-])?\\s*)?(?:--static)?(?:--abstract)?")
                .matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid method command");

        ClassInfo cls = CommandUtils.findClass(diagram, m.group("target"));
        Method method = new Method();
        method.setName(m.group("name"));
        method.setReturnType(m.group("ret"));
        method.setVisibility(CommandUtils.parseVisibility(m.group("access")));
        method.setStatic(command.contains("--static"));
        method.setAbstract(command.contains("--abstract"));

        if (m.group("params") != null) {
            method.setParameters(CommandUtils.parseParameters(m.group("params")));
        }

        cls.getMethods().add(method);
        undoStack.push(() -> cls.getMethods().remove(method));
        return "Added method: " + method.getName();
    }

    private String getTypeName(String type) {
        return switch (type) {
            case "c" -> "class";
            case "i" -> "interface";
            case "e" -> "enum";
            default -> "unknown";
        };
    }
}
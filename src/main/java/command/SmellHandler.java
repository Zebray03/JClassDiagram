package command;

import diagram.ClassDiagram;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmellHandler implements CommandHandler{
    private final ClassDiagram diagram;

    public SmellHandler(ClassDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("smell");
    }

    @Override
    public String handle(String command) {
        Matcher m = Pattern.compile("smell detail (?<name>\\w+)").matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid smell command");

        String elementName = m.group("name");
        return diagram.getCodeSmells().stream()
                .filter(smell -> isRelated(smell, elementName))
                .collect(Collectors.joining("\n")) + "\n";
    }

    private boolean isRelated(String smell, String elementName) {
        return smell.split(":")[1].trim().contains(elementName);
    }
}

package command;

import command.utils.CommandUtils;
import diagram.ClassDiagram;
import diagram.model.ClassInfo;
import diagram.model.Relationship;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryHandler implements CommandHandler{
    private final ClassDiagram diagram;

    public QueryHandler(ClassDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("query") || command.startsWith("relate");
    }

    @Override
    public String handle(String command) {
        if (command.startsWith("query")) {
            return handleQuery(command);
        } else if (command.startsWith("relate")) {
            return handleRelate(command);
        }
        return "Invalid query command";
    }

    private String handleQuery(String command) {
        Matcher m = Pattern.compile(
                        "query -(?<type>[cie])\\s+(?<name>\\w+)(?:\\s+--hide=(?<hide>\\w+))?")
                .matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid query command");

        ClassInfo cls = CommandUtils.findClass(diagram, m.group("name"));
        String hide = m.group("hide");
        return generateClassUML(cls, m.group("type"), hide);
    }

    private String generateClassUML(ClassInfo cls, String type, String hide) {
        StringBuilder sb = new StringBuilder();

        // 类类型标记
        sb.append(cls.isEnum() ? "enum " : cls.isInterface() ? "interface " :
                        cls.isAbstract() ? "abstract class " : "class ")
                .append(cls.getName()).append(cls.getGenericParameters()).append(" {\n");

        // 处理枚举常量
        if (cls.isEnum() && !"constant".equals(hide)) {
            cls.getEnumConstants().forEach(ec -> sb.append("    ").append(ec).append("\n"));
        }

        // 处理字段
        if (!"field".equals(hide) && !cls.isInterface()) {
            cls.getAttributes().stream()
                    .sorted(Comparator.comparingInt(a -> CommandUtils.getVisibilityOrder(a.getVisibility())))
                    .forEach(attr -> {
                        sb.append("    ").append(CommandUtils.getVisibilitySymbol(attr.getVisibility()))
                                .append(attr.isStatic() ? " {static} " : " ")
                                .append(attr.getName()).append(": ").append(attr.getType()).append("\n");
                    });
        }

        // 处理方法
        if (!"method".equals(hide)) {
            cls.getMethods().stream()
                    .sorted(Comparator.comparingInt(a -> CommandUtils.getVisibilityOrder(a.getVisibility())))
                    .forEach(method -> {
                        sb.append("    ").append(CommandUtils.getVisibilitySymbol(method.getVisibility()))
                                .append(" ");
                        if (method.isStatic()) sb.append("{static} ");
                        if (method.isAbstract()) sb.append("{abstract} ");
                        sb.append(method.getName()).append("(")
                                .append(method.getParameters().stream()
                                        .map(p -> p.getName() + ": " + p.getType())
                                        .collect(Collectors.joining(", ")))
                                .append("): ").append(method.getReturnType()).append("\n");
                    });
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String handleRelate(String command) {
        Matcher m = Pattern.compile("relate (?<a>\\w+) (?<b>\\w+)").matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid relate command");

        String a = m.group("a");
        String b = m.group("b");

        return diagram.getRelationships().stream()
                .filter(r -> (r.getSource().equals(a) && r.getTarget().equals(b)) ||
                        (r.getSource().equals(b) && r.getTarget().equals(a)))
                .map(this::formatRelationship)
                .collect(Collectors.joining("\n")) + "\n";
    }

    private String formatRelationship(Relationship rel) {
        String arrow = switch (rel.getType()) {
            case "EXTENDS" -> " <|-- ";
            case "IMPLEMENTS" -> " <|.. ";
            case "ASSOCIATION" -> " <-- ";
            case "DEPENDENCY" -> " <.. ";
            default -> " -- ";
        };
        return rel.getTarget() + arrow + rel.getSource();
    }
}

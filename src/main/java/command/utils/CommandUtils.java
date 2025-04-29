package command.utils;

import diagram.ClassDiagram;
import diagram.model.Attribute;
import diagram.model.ClassInfo;
import diagram.model.Method;
import diagram.model.Parameter;

import java.util.ArrayList;
import java.util.List;

public class CommandUtils {
    public static ClassInfo findClass(ClassDiagram diagram, String name) {
        return diagram.getClasses().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Class not found: " + name));
    }

    public static String parseVisibility(String symbol) {
        if (symbol == null) return "private";
        return switch (symbol) {
            case "+" -> "public";
            case "#" -> "protected";
            case "~" -> "package private";
            default -> "private";
        };
    }

    public static List<Parameter> parseParameters(String paramStr) {
        List<Parameter> params = new ArrayList<>();
        if (paramStr == null) return params;
        for (String pair : paramStr.split(",")) {
            String[] parts = pair.split(":");
            if (parts.length != 2) continue;
            params.add(new Parameter(parts[1].trim(), parts[0].trim()));
        }
        return params;
    }

    public static Attribute copyAttribute(Attribute src) {
        Attribute copy = new Attribute();
        copy.setName(src.getName());
        copy.setType(src.getType());
        copy.setVisibility(src.getVisibility());
        copy.setStatic(src.isStatic());
        return copy;
    }

    public static Method copyMethod(Method src) {
        Method copy = new Method();
        copy.setName(src.getName());
        copy.setReturnType(src.getReturnType());
        copy.setVisibility(src.getVisibility());
        copy.setStatic(src.isStatic());
        copy.setAbstract(src.isAbstract());
        copy.setParameters(new ArrayList<>(src.getParameters()));
        return copy;
    }

    public static void restoreAttribute(Attribute target, Attribute backup) {
        target.setName(backup.getName());
        target.setType(backup.getType());
        target.setVisibility(backup.getVisibility());
        target.setStatic(backup.isStatic());
    }

    public static void restoreMethod(Method target, Method backup) {
        target.setName(backup.getName());
        target.setReturnType(backup.getReturnType());
        target.setVisibility(backup.getVisibility());
        target.setStatic(backup.isStatic());
        target.setAbstract(backup.isAbstract());
        target.setParameters(new ArrayList<>(backup.getParameters()));
    }

    public static String getVisibilitySymbol(String visibility) {
        return switch (visibility.toLowerCase()) {
            case "public" -> "+";
            case "protected" -> "#";
            case "package private" -> "~";
            default -> "-";
        };
    }

    public static int getVisibilityOrder(String visibility) {
        return switch (visibility.toLowerCase()) {
            case "private" -> 0;
            case "protected" -> 1;
            case "package private" -> 2;
            default -> 3; // public
        };
    }
}

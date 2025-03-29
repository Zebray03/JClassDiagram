package diagram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDiagram {
    public List<ClassInfo> classes = new ArrayList<>();
    public List<Relationship> relationships = new ArrayList<>();

    public String generateUML() {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");

        for (ClassInfo cls : classes) {
            sb.append(cls.isInterface ? "interface " : "class ")
                    .append(cls.name)
                    .append(" {\n");

            List<Attribute> sortedAttributes = cls.attributes.stream()
                    .sorted(Comparator.comparingInt(a -> getVisibilityOrder(a.visibility)))
                    .toList();

            // ---------- Attribute ----------
            for (Attribute attr : sortedAttributes) {
                String visibilitySymbol = getVisibilitySymbol(attr.visibility);
                sb.append("    ")
                        .append(visibilitySymbol)
                        .append(" ");
                if (attr.isStatic) {
                    sb.append("{static} ");
                }
                sb.append(attr.name)
                        .append(": ")
                        .append(attr.type)
                        .append("\n");
            }

            // ---------- Method ----------
            List<Method> sortedMethods = cls.methods.stream()
                    .sorted(Comparator.comparingInt(m -> getVisibilityOrder(m.visibility)))
                    .toList();

            for (Method method : sortedMethods) {
                String visibilitySymbol = getVisibilitySymbol(method.visibility);
                String params = method.parameters.stream()
                        .map(p -> p.name + ": " + p.type)
                        .collect(Collectors.joining(", "));
                sb.append("    ")
                        .append(visibilitySymbol)
                        .append(" ");
                if (method.isStatic) {
                    sb.append("{static} "); // 静态方法标记
                }
                sb.append(method.name)
                        .append("(")
                        .append(params)
                        .append("): ")
                        .append(method.returnType)
                        .append("\n");
            }

            sb.append("}\n");
        }

        // ---------- Relationship ----------
        for (Relationship relationship : relationships) {
            switch (relationship.type) {
                case "EXTENDS":
                    sb.append(relationship.target)
                            .append(" <|-- ")
                            .append(relationship.source)
                            .append("\n");
                    break;
                case "IMPLEMENTS":
                    sb.append(relationship.target)
                            .append(" <|.. ")
                            .append(relationship.source)
                            .append("\n");
                    break;
            }
        }

        sb.append("@enduml");
        return sb.toString();
    }

    // private
    // protected
    // package private
    // public
    private int getVisibilityOrder(String visibility) {
        switch (visibility.toLowerCase()) {
            case "private":
                return 0;
            case "protected":
                return 1;
            case "package private":
                return 2;
            default:
                return 3;
        }
    }

    private String getVisibilitySymbol(String visibility) {
        switch (visibility.toLowerCase()) {
            case "private":
                return "-";
            case "protected":
                return "#";
            case "package private":
                return "~";
            default:
                return "+";
        }
    }

    public static class ClassInfo {
        public String name;
        public boolean isInterface;
        public List<Attribute> attributes = new ArrayList<>();
        public List<Method> methods = new ArrayList<>();
    }

    public static class Attribute {
        public String visibility;
        public String type;
        public String name;
        public boolean isStatic;
    }

    public static class Method {
        public String visibility;
        public String returnType;
        public String name;
        public List<Parameter> parameters = new ArrayList<>();
        public boolean isStatic;
    }

    public static class Parameter {
        public String type;
        public String name;

        public Parameter(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    public static class Relationship {
        public String source;
        public String target;
        public String type;
    }
}
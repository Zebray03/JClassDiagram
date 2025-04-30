package diagram;

import diagram.model.ClassInfo;
import diagram.model.Relationship;
import diagram.parser.utils.TypeUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UMLGenerator {
    public String generate(ClassDiagram diagram) {
        StringBuilder sb = new StringBuilder("@startuml\n");
        diagram.getClasses().forEach(cls -> generateClassUML(sb, cls));
        diagram.getRelationships().forEach(rel -> generateRelationshipUML(sb, rel));
        sb.append("@enduml");
        return sb.toString();
    }

    private void generateClassUML(StringBuilder sb, ClassInfo cls) {
        sb.append(cls.isEnum() ? "enum " : cls.isInterface() ? "interface " :
                        cls.isAbstract() ? "abstract class " : "class ")
                .append(cls.getName()).append(cls.getGenericParameters()).append(" {\n");
        generateEnumConstants(sb, cls);
        generateAttributes(sb, cls);
        generateMethods(sb, cls);
        sb.append("}\n");
    }

    private void generateEnumConstants(StringBuilder sb, ClassInfo cls) {
        if (cls.isEnum()) {
            cls.getEnumConstants().forEach(ec -> sb.append("    ").append(ec).append("\n"));
        }
    }

    private void generateAttributes(StringBuilder sb, ClassInfo cls) {
        cls.getAttributes().stream()
                .sorted(Comparator.comparingInt(a -> TypeUtils.getVisibilityOrder(a.getVisibility())))
                .forEach(attr -> {
                    sb.append("    ").append(TypeUtils.getVisibilitySymbol(attr.getVisibility()))
                            .append(attr.isStatic() ? " {static} " : " ")
                            .append(attr.getName()).append(": ").append(attr.getType()).append("\n");
                });
    }

    private void generateMethods(StringBuilder sb, ClassInfo cls) {
        cls.getMethods().stream()
                .filter(method -> !method.isConstructor())
                .sorted(Comparator.comparingInt(m -> TypeUtils.getVisibilityOrder(m.getVisibility())))
                .forEach(method -> {
                    sb.append("    ")
                            .append(TypeUtils.getVisibilitySymbol(method.getVisibility()))
                            .append(" ");

                    List<String> parts = new ArrayList<>();

                    if (!method.getGenericParameters().isEmpty()) {
                        parts.add(method.getGenericParameters());
                    }
                    if (method.isStatic()) {
                        parts.add("{static}");
                    }
                    if (method.isAbstract()) {
                        parts.add("{abstract}");
                    }
                    if (!parts.isEmpty()) {
                        sb.append(String.join(" ", parts)).append(" ");
                    }

                    sb.append(method.getName()).append("(")
                            .append(method.getParameters().stream()
                                    .map(p -> p.getName() + ": " + p.getType())
                                    .collect(Collectors.joining(", ")))
                            .append("): ").append(method.getReturnType()).append("\n");
                });
    }

    private void generateRelationshipUML(StringBuilder sb, Relationship rel) {
        switch (rel.getType()) {
            case "EXTENDS":
                sb.append(rel.getTarget()).append(" <|-- ").append(rel.getSource()).append("\n");
                break;
            case "IMPLEMENTS":
                sb.append(rel.getTarget()).append(" <|.. ").append(rel.getSource()).append("\n");
                break;
            case "DEPENDENCY":
                sb.append(rel.getTarget()).append(" <.. ").append(rel.getSource()).append("\n");
                break;
            case "ASSOCIATION":
                sb.append(rel.getTarget()).append(" <-- ").append(rel.getSource()).append("\n");
                break;
            default:
                break;
        }
    }
}

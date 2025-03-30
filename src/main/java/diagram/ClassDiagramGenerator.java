package diagram;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class ClassDiagramGenerator {

    public ClassDiagram parse(Path sourcePath) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(sourcePath);
        ClassDiagram diagram = new ClassDiagram();

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
            boolean isInterface = cls.isInterface();

            ClassDiagram.ClassInfo classInfo = new ClassDiagram.ClassInfo();
            classInfo.name = cls.getNameAsString();
            classInfo.isInterface = isInterface;
            diagram.classes.add(classInfo);

            // ---------- Attribute ----------
            cls.getFields().forEach(field -> {
                String visibility = field.getAccessSpecifier().toString().toLowerCase();
                String type = field.getElementType().asString();
                boolean isStatic = field.hasModifier(Modifier.Keyword.STATIC);

                if (visibility.equals("none")) {
                    visibility = isInterface ? "public" : "package private";
                }

                String finalVisibility = visibility;
                field.getVariables().forEach(variable -> {
                    ClassDiagram.Attribute attr = new ClassDiagram.Attribute();
                    attr.visibility = finalVisibility;
                    attr.type = type;
                    attr.name = variable.getNameAsString();
                    attr.isStatic = isStatic;
                    classInfo.attributes.add(attr);
                });
            });

            // ---------- Method ----------
            cls.getMethods().forEach(method -> {
                String visibility = method.getAccessSpecifier().toString().toLowerCase();

                if (visibility.equals("none")) {
                    visibility = isInterface ? "public" : "package private";
                }

                ClassDiagram.Method methodInfo = new ClassDiagram.Method();
                methodInfo.visibility = visibility;
                methodInfo.returnType = method.getType().asString();
                methodInfo.name = method.getNameAsString();
                methodInfo.isStatic = method.hasModifier(Modifier.Keyword.STATIC);
                methodInfo.parameters = method.getParameters().stream()
                        .map(p -> new ClassDiagram.Parameter(p.getType().asString(), p.getNameAsString()))
                        .collect(Collectors.toList());
                classInfo.methods.add(methodInfo);
            });

            // ---------- Relationship ----------
            cls.getExtendedTypes().forEach(parent -> {
                ClassDiagram.Relationship rel = new ClassDiagram.Relationship();
                rel.source = classInfo.name;
                rel.target = parent.getNameAsString();
                rel.type = "EXTENDS";
                diagram.relationships.add(rel);
            });
            cls.getImplementedTypes().forEach(impl -> {
                ClassDiagram.Relationship rel = new ClassDiagram.Relationship();
                rel.source = classInfo.name;
                rel.target = impl.getNameAsString();
                rel.type = "IMPLEMENTS";
                diagram.relationships.add(rel);
            });
        });

        return diagram;
    }
}
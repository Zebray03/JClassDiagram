package diagram;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import diagram.generator.UMLGenerator;
import diagram.model.ClassInfo;
import diagram.parser.ClassParser;
import diagram.parser.EnumParser;
import diagram.parser.RelationParser;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ClassDiagramGenerator {
    private final ClassParser classParser = new ClassParser();
    private final EnumParser enumParser = new EnumParser();
    private final RelationParser relationParser = new RelationParser();
    private final UMLGenerator umlGenerator = new UMLGenerator();

    public ClassDiagram parse(Path sourcePath) throws IOException {
        ClassDiagram diagram = new ClassDiagram();

        if (Files.isDirectory(sourcePath)) {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".java")) {
                        parseSingleFile(file, diagram);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            if (sourcePath.toString().endsWith(".java")) {
                parseSingleFile(sourcePath, diagram);
            }
        }
        return diagram;
    }

    private void parseSingleFile(Path filePath, ClassDiagram diagram) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(filePath);

        // 处理普通类/接口
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
            ClassInfo classInfo = new ClassInfo();
            classParser.parse(cls, classInfo, diagram);
            relationParser.parse(cls, classInfo, diagram);
            diagram.addClass(classInfo);
        });

        // 处理枚举类
        cu.findAll(EnumDeclaration.class).forEach(enumDecl -> {
            ClassInfo classInfo = new ClassInfo();
            enumParser.parse(enumDecl, classInfo, diagram);
            relationParser.parseForEnum(enumDecl, classInfo, diagram);
            diagram.addClass(classInfo);
        });
    }

    public String generate(ClassDiagram diagram) {
        return umlGenerator.generate(diagram);
    }
}
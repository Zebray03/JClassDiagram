package diagram.parser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import diagram.ClassDiagram;
import diagram.model.ClassInfo;
import diagram.model.Relationship;

public class RelationParser {
    public void parseClassRelations(ClassOrInterfaceDeclaration cls, ClassInfo classInfo, ClassDiagram diagram) {
        cls.getExtendedTypes().forEach(parent -> {
            Relationship rel = new Relationship();
            rel.setSource(classInfo.getName());
            rel.setTarget(parent.getNameAsString());
            rel.setType("EXTENDS");
            diagram.addRelationship(rel);
        });

        cls.getImplementedTypes().forEach(impl -> {
            Relationship rel = new Relationship();
            rel.setSource(classInfo.getName());
            rel.setTarget(impl.getNameAsString());
            rel.setType("IMPLEMENTS");
            diagram.addRelationship(rel);
        });
    }

    public void parseEnumRelations(EnumDeclaration enumDecl, ClassInfo classInfo, ClassDiagram diagram) {
        enumDecl.getImplementedTypes().forEach(impl -> {
            Relationship rel = new Relationship();
            rel.setSource(classInfo.getName());
            rel.setTarget(impl.getNameAsString());
            rel.setType("IMPLEMENTS");
            diagram.addRelationship(rel);
        });
    }
}
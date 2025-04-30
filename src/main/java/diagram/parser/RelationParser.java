package diagram.parser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import diagram.ClassDiagram;
import diagram.model.ClassInfo;
import diagram.model.Relationship;

public class RelationParser {
    public void parse(ClassOrInterfaceDeclaration cls, ClassInfo classInfo, ClassDiagram diagram) {
        // 处理扩展类型（父类）
        cls.getExtendedTypes().forEach(parent -> {
            Relationship rel = new Relationship();
            rel.setSource(classInfo.getName());
            rel.setTarget(parent.getNameAsString());
            rel.setType("EXTENDS");
            diagram.addRelationship(rel);

            // 找到父类并将当前类添加到父类的子类列表中
            ClassInfo parentClass = findClassByName(parent.getNameAsString(), diagram);
            if (parentClass != null) {
                parentClass.getChildren().add(classInfo); // 维护父类的子类列表
            }
        });

        // 处理实现接口（关键修复）
        cls.getImplementedTypes().forEach(impl -> {
            String interfaceName = impl.getNameAsString();

            // 1. 添加实现关系到ClassDiagram
            Relationship rel = new Relationship();
            rel.setSource(classInfo.getName());
            rel.setTarget(interfaceName);
            rel.setType("IMPLEMENTS");
            diagram.addRelationship(rel);

            // 2. 将接口名称记录到ClassInfo
            classInfo.getImplementedTypes().add(interfaceName); // 直接在此处写入

            // 3. 维护接口实现关系（可选）
            ClassInfo interfaceClass = findClassByName(interfaceName, diagram);
            if (interfaceClass != null) {
                interfaceClass.getChildren().add(classInfo);
            }
        });
    }

    private ClassInfo findClassByName(String className, ClassDiagram diagram) {
        return diagram.getClasses().stream()
                .filter(c -> c.getName().equals(className))
                .findFirst()
                .orElse(null);  // 如果找不到，返回null
    }

    public void parseForEnum(EnumDeclaration enumDecl, ClassInfo classInfo, ClassDiagram diagram) {
        enumDecl.getImplementedTypes().forEach(impl -> {
            Relationship rel = new Relationship();
            rel.setSource(classInfo.getName());
            rel.setTarget(impl.getNameAsString());
            rel.setType("IMPLEMENTS");
            diagram.addRelationship(rel);
        });
    }

}
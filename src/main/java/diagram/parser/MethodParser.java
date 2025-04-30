package diagram.parser;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import diagram.model.ClassInfo;
import diagram.model.Relationship;
import diagram.ClassDiagram;
import diagram.parser.utils.GenericUtils;

import java.util.List;

public class MethodParser {
    public void parse(CallableDeclaration<?> method, ClassInfo classInfo, ClassDiagram diagram) {
        String sourceClass = classInfo.getName();

        // 1. 解析返回类型（仅处理方法，构造函数没有返回类型）
        if (method instanceof MethodDeclaration) {
            MethodDeclaration methodDecl = (MethodDeclaration) method;
            String returnType = methodDecl.getType().asString();
            extractAndAddDependencies(returnType, sourceClass, diagram);
        }

        // 2. 解析参数类型（方法和构造函数均需处理）
        method.getParameters().forEach(param -> {
            String paramType = param.getType().asString();
            extractAndAddDependencies(paramType, sourceClass, diagram);
        });

        // 3. 解析局部变量类型（仅处理方法的Body）
        if (method instanceof MethodDeclaration) {
            MethodDeclaration methodDecl = (MethodDeclaration) method;
            methodDecl.getBody().ifPresent(body -> {
                body.findAll(VariableDeclarationExpr.class).forEach(expr -> {
                    expr.getVariables().forEach(var -> {
                        String varType = var.getType().asString();
                        extractAndAddDependencies(varType, sourceClass, diagram);
                    });
                });
            });
        }
    }

    private void extractAndAddDependencies(String type, String sourceClass, ClassDiagram diagram) {
        List<String> customTypes = GenericUtils.extractCustomTypes(type);
        customTypes.forEach(targetClass -> {
            if (isValidDependency(sourceClass, targetClass, diagram)) {
                Relationship rel = new Relationship();
                rel.setSource(sourceClass);
                rel.setTarget(targetClass);
                rel.setType("DEPENDENCY");
                diagram.addRelationship(rel);
            }
        });
    }

    private boolean isValidDependency(String source, String target, ClassDiagram diagram) {
        return !GenericUtils.isBuiltInType(target)
                && !source.equals(target)
                && !hasAssociation(source, target, diagram)
                && !hasExistingDependency(source, target, diagram); // 检查依赖是否已存在
    }

    // 检查是否已经存在相同的依赖关系
    private boolean hasExistingDependency(String source, String target, ClassDiagram diagram) {
        return diagram.getRelationships().stream()
                .anyMatch(rel -> rel.getSource().equals(source)
                        && rel.getTarget().equals(target)
                        && rel.getType().equals("DEPENDENCY"));
    }

    private boolean hasAssociation(String source, String target, ClassDiagram diagram) {
        return diagram.getRelationships().stream()
                .anyMatch(rel -> rel.getSource().equals(source)
                        && rel.getTarget().equals(target)
                        && rel.getType().equals("ASSOCIATION"));
    }
}

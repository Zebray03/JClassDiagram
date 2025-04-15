package diagram.parser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.Node;
import diagram.ClassDiagram;
import diagram.model.*;
import diagram.utils.Type.TypeExtractor;
import diagram.utils.Type.TypeUtils;

import java.util.List;
import java.util.stream.Collectors;

public class EnumParser {
    private final MethodParser methodParser = new MethodParser();

    public void parse(EnumDeclaration enumDecl, ClassInfo classInfo, ClassDiagram diagram) {
        classInfo.setName(enumDecl.getNameAsString());
        classInfo.setEnum(true);
        processEnumConstants(enumDecl, classInfo);
        processFields(enumDecl, classInfo, diagram);
        processMethods(enumDecl, classInfo, diagram);
    }

    private void processEnumConstants(EnumDeclaration enumDecl, ClassInfo classInfo) {
        enumDecl.getEntries().forEach(entry -> {
            String name = entry.getNameAsString();
            String args = entry.getArguments().stream()
                    .map(Node::toString)
                    .collect(Collectors.joining(", "));
            classInfo.getEnumConstants().add(args.isEmpty() ? name : name + "(" + args + ")");
        });
    }

    private void processFields(EnumDeclaration enumDecl, ClassInfo classInfo, ClassDiagram diagram) {
        enumDecl.getFields().forEach(field -> {
            String type = TypeUtils.fixGenericTypeFormat(field.getCommonType().asString());
            List<String> customTypes = TypeExtractor.extractCustomTypes(type);

            // 去重逻辑，检查是否已经存在相同关系
            customTypes.forEach(targetClass -> {
                if (!classInfo.getName().equals(targetClass) && !hasExistingRelationship(classInfo.getName(), targetClass, diagram)) {
                    Relationship rel = new Relationship();
                    rel.setSource(classInfo.getName());
                    rel.setTarget(targetClass);
                    rel.setType("ASSOCIATION");
                    diagram.addRelationship(rel);
                }
            });

            String visibility = field.getAccessSpecifier().toString().toLowerCase();
            boolean isStatic = field.hasModifier(Modifier.Keyword.STATIC);

            if ("none".equals(visibility)) {
                visibility = "private"; // 枚举字段默认private
            }

            String finalVisibility = visibility;
            field.getVariables().forEach(variable -> {
                Attribute attr = new Attribute();
                attr.setVisibility(finalVisibility);
                attr.setType(type);
                attr.setName(variable.getNameAsString());
                attr.setStatic(isStatic);
                classInfo.getAttributes().add(attr);
            });
        });
    }

    private void processMethods(EnumDeclaration enumDecl, ClassInfo classInfo, ClassDiagram diagram) {
        enumDecl.getMethods().forEach(method -> {
            // JavaParser 中枚举的 getMethods() 不包含构造函数，无需跳过
            // 处理可见性（枚举方法默认public）
            String visibility = method.getAccessSpecifier().toString().toLowerCase();
            if (visibility.equals("none")) {
                visibility = "public"; // 枚举方法默认public
            }

            Method methodInfo = new Method();
            methodInfo.setVisibility(visibility);

            // 修复返回类型的泛型格式
            String returnType = TypeUtils.fixGenericTypeFormat(method.getType().asString());
            methodInfo.setReturnType(returnType);

            methodInfo.setName(method.getNameAsString());
            methodInfo.setStatic(method.hasModifier(Modifier.Keyword.STATIC));
            methodInfo.setAbstract(method.isAbstract());

            // 处理泛型参数（方法级泛型）
            if (!method.getTypeParameters().isEmpty()) {
                String genericParams = method.getTypeParameters().stream()
                        .map(tp -> {
                            String name = tp.getNameAsString();
                            String bounds = tp.getTypeBound().stream()
                                    .map(bound -> TypeUtils.fixGenericTypeFormat(bound.asString()))
                                    .collect(Collectors.joining(" & "));
                            return bounds.isEmpty() ? name : name + " extends " + bounds;
                        })
                        .collect(Collectors.joining(", ", "<", ">"));
                methodInfo.setGenericParameters(genericParams);
            }

            // 处理参数
            method.getParameters().forEach(param -> {
                String paramType = TypeUtils.fixGenericTypeFormat(param.getType().asString());
                Parameter paramModel = new Parameter(paramType, param.getNameAsString());
                methodInfo.getParameters().add(paramModel);
            });

            classInfo.getMethods().add(methodInfo);

            methodParser.parseMethodDependencies(method, classInfo, diagram);
        });
    }

    // 检查是否已经存在相同的关系
    private boolean hasExistingRelationship(String source, String target, ClassDiagram diagram) {
        return diagram.getRelationships().stream()
                .anyMatch(rel -> rel.getSource().equals(source) && rel.getTarget().equals(target));
    }
}

package diagram.parser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.Node;
import diagram.model.Attribute;
import diagram.model.ClassInfo;
import diagram.model.Method;
import diagram.model.Parameter;
import diagram.utils.TypeUtils;

import java.util.stream.Collectors;

public class EnumParser {
    public void parse(EnumDeclaration enumDecl, ClassInfo classInfo) {
        classInfo.setName(enumDecl.getNameAsString());
        classInfo.setEnum(true);
        processEnumConstants(enumDecl, classInfo);
        processFields(enumDecl, classInfo);
        processMethods(enumDecl, classInfo);
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

    private void processFields(EnumDeclaration enumDecl, ClassInfo classInfo) {
        enumDecl.getFields().forEach(field -> {
            String visibility = field.getAccessSpecifier().toString().toLowerCase();
            String type = TypeUtils.fixGenericTypeFormat(field.getCommonType().asString());
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

    private void processMethods(EnumDeclaration enumDecl, ClassInfo classInfo) {
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
        });
    }
}
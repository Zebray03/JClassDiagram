package diagram.parser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import diagram.model.Attribute;
import diagram.model.ClassInfo;
import diagram.model.Method;
import diagram.model.Parameter;
import diagram.utils.TypeUtils;

import java.util.stream.Collectors;

public class ClassParser {
    public void parse(ClassOrInterfaceDeclaration cls, ClassInfo classInfo) {
        classInfo.setName(cls.getNameAsString());
        classInfo.setInterface(cls.isInterface());
        classInfo.setAbstract(cls.isAbstract());
        processTypeParameters(cls, classInfo);
        processFields(cls, classInfo);
        processMethods(cls, classInfo);
    }

    private void processTypeParameters(ClassOrInterfaceDeclaration cls, ClassInfo classInfo) {
        if (!cls.getTypeParameters().isEmpty()) {
            String generics = cls.getTypeParameters().stream()
                    .map(tp -> {
                        String name = tp.getNameAsString();
                        String bounds = tp.getTypeBound().stream()
                                .map(b -> TypeUtils.fixGenericTypeFormat(b.asString()))
                                .collect(Collectors.joining(" & "));
                        return bounds.isEmpty() ? name : name + " extends " + bounds;
                    })
                    .collect(Collectors.joining(", ", "<", ">"));
            classInfo.setGenericParameters(generics);
        }
    }

    private void processFields(ClassOrInterfaceDeclaration cls, ClassInfo classInfo) {
        cls.getFields().forEach(field -> {
            String visibility = field.getAccessSpecifier().toString().toLowerCase();
            String type = TypeUtils.fixGenericTypeFormat(field.getCommonType().asString());
            boolean isStatic = field.hasModifier(Modifier.Keyword.STATIC);

            if ("none".equals(visibility)) {
                visibility = classInfo.isInterface() ? "public" : "package private";
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

    private void processMethods(ClassOrInterfaceDeclaration cls, ClassInfo classInfo) {
        cls.getMethods().forEach(method -> {
            String visibility = method.getAccessSpecifier().toString().toLowerCase();
            if (visibility.equals("none")) {
                if (classInfo.isInterface() || classInfo.isEnum()) {
                    visibility = "public";
                } else {
                    visibility = "package private";
                }
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
                                    .map(bound -> TypeUtils.fixGenericTypeFormat(bound.asString())) // 修复泛型边界格式
                                    .collect(Collectors.joining(" & "));
                            return bounds.isEmpty() ? name : name + " extends " + bounds;
                        })
                        .collect(Collectors.joining(", ", "<", ">"));
                methodInfo.setGenericParameters(genericParams);
            }

            // 处理方法参数（修复参数类型格式）
            method.getParameters().forEach(param -> {
                String paramType = TypeUtils.fixGenericTypeFormat(param.getType().asString());
                Parameter paramModel = new Parameter(paramType, param.getNameAsString());
                methodInfo.getParameters().add(paramModel);
            });

            classInfo.getMethods().add(methodInfo);
        });
    }
}
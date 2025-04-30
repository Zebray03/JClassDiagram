package diagram.parser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import diagram.ClassDiagram;
import diagram.model.*;
import diagram.utils.Type.GenericUtils;
import diagram.utils.Type.TypeUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ClassParser {
    private final MethodParser methodParser = new MethodParser();

    public void parse(ClassOrInterfaceDeclaration cls, ClassInfo classInfo, ClassDiagram diagram) {
        classInfo.setName(cls.getNameAsString());
        classInfo.setInterface(cls.isInterface());
        classInfo.setAbstract(cls.isAbstract());
        parseTypeParameters(cls, classInfo);
        parseFields(cls, classInfo, diagram);
        parseMethods(cls, classInfo, diagram);
    }

    private void parseTypeParameters(ClassOrInterfaceDeclaration cls, ClassInfo classInfo) {
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

    private void parseFields(ClassOrInterfaceDeclaration cls, ClassInfo classInfo, ClassDiagram diagram) {
        cls.getFields().forEach(field -> {
            String type = TypeUtils.fixGenericTypeFormat(field.getCommonType().asString());
            List<String> customTypes = GenericUtils.extractCustomTypes(type);

            // 遍历并为每个泛型类型生成关系
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

    private void parseMethods(ClassOrInterfaceDeclaration cls, ClassInfo classInfo, ClassDiagram diagram) {
        cls.getConstructors().forEach(constructor -> {
            Method methodInfo = new Method();
            methodInfo.setConstructor(true);

            methodInfo.setName(classInfo.getName());

            String visibility = constructor.getAccessSpecifier().toString().toLowerCase();
            if ("none".equals(visibility)) {
                visibility = classInfo.isInterface() ? "public" : "package private";
            }
            methodInfo.setVisibility(visibility);

            // 处理泛型参数
            if (!constructor.getTypeParameters().isEmpty()) {
                String genericParams = constructor.getTypeParameters().stream()
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

            // 处理构造方法参数
            constructor.getParameters().forEach(param -> {
                String paramType = TypeUtils.fixGenericTypeFormat(param.getType().asString());
                Parameter paramModel = new Parameter(paramType, param.getNameAsString());
                methodInfo.getParameters().add(paramModel);
            });

            // 构造方法不能是static/abstract
            methodInfo.setStatic(false);
            methodInfo.setAbstract(false);

            classInfo.getMethods().add(methodInfo);

            methodParser.parse(constructor, classInfo, diagram);
        });

        classInfo.setHasConstructor(!classInfo.getMethods().isEmpty());

        cls.getMethods().forEach(method -> {
            Method methodInfo = new Method();

            methodInfo.setConstructor(false);

            String visibility = method.getAccessSpecifier().toString().toLowerCase();

            if (visibility.equals("none")) {
                if (classInfo.isInterface() || classInfo.isEnum()) {
                    visibility = "public";
                } else {
                    visibility = "package private";
                }
            }

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

            methodParser.parse(method, classInfo, diagram);
        });
    }

    private boolean hasExistingRelationship(String source, String target, ClassDiagram diagram) {
        return diagram.getRelationships().stream()
                .anyMatch(rel -> rel.getSource().equals(source) && rel.getTarget().equals(target));
    }
}


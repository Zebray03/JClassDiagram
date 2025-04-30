package diagram.detector;

import diagram.ClassDiagram;
import diagram.model.ClassInfo;

import java.util.ArrayList;
import java.util.List;

public class SingletonPatternDetector implements PatternDetector {
    @Override
    public List<String> detect(ClassDiagram diagram) {
        List<String> results = new ArrayList<>();
        for (ClassInfo cls : diagram.getClasses()) {
            if (isSingleton(cls, diagram)) {
                results.add("Possible Design Patterns: Singleton Pattern - " + cls.getName());
            }
        }
        return results;
    }

    private boolean isSingleton(ClassInfo cls, ClassDiagram diagram) {
        // 检查是否有子类
        if (!cls.getChildren().isEmpty()) return false;

        // 检查构造函数
        boolean hasPrivateConstructor = cls.getMethods().stream()
                .anyMatch(m -> m.isConstructor() && "private".equals(m.getVisibility()));
        boolean hasPublicConstructor = cls.getMethods().stream()
                .anyMatch(m -> m.isConstructor() && "public".equals(m.getVisibility()));
        if (hasPublicConstructor) return false;

        // 检查静态实例字段
        boolean hasStaticInstance = cls.getAttributes().stream()
                .anyMatch(attr -> attr.isStatic() &&
                        attr.getType().equals(cls.getName()) &&
                        "private".equals(attr.getVisibility()));

        // 检查获取实例方法
        boolean hasGetInstance = cls.getMethods().stream()
                .anyMatch(m -> m.isStatic() &&
                        "public".equals(m.getVisibility()) &&
                        m.getReturnType().equals(cls.getName()));

        return hasPrivateConstructor && hasStaticInstance && hasGetInstance;
    }
}

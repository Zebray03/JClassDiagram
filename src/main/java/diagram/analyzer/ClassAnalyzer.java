package diagram.analyzer;

import diagram.ClassDiagram;
import diagram.model.ClassInfo;

import java.util.ArrayList;
import java.util.List;

public class ClassAnalyzer implements Analyzer {
    boolean active = true;

    @Override
    public List<String> analyze(ClassDiagram diagram) {
        List<String> result = new ArrayList<>();

        for (ClassInfo classInfo : diagram.getClasses()) {
            if (!classInfo.isInterface() && !classInfo.isEnum()) {
                result.addAll(analyzeClass(classInfo));
            }
        }

        return result;
    }

    private List<String> analyzeClass(ClassInfo classInfo) {
        List<String> result = new ArrayList<>();

        // 检查 God Class
        if (classInfo.getAttributes().size() >= 20 || classInfo.getMethods().size() >= 20) {
            result.add("God Class: " + classInfo.getName());
        }

        // 检查 Lazy Class
        if (classInfo.getAttributes().isEmpty() || classInfo.getMethods().size() <= 1) {
            result.add("Lazy Class: " + classInfo.getName());
        }

        // 检查 Data Class
        if (!classInfo.isGodClass() && !classInfo.isLazyClass()) {
            boolean isDataClass = classInfo.getMethods().stream()
                    .filter(method -> !method.isConstructor())  // 排除构造方法
                    .allMatch(method -> method.getName().startsWith("get") || method.getName().startsWith("set"));
            if (isDataClass) {
                result.add("Data Class: " + classInfo.getName());
            }
        }

        return result;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean _active) {
        active = _active;
    }
}

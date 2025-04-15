package diagram;

import diagram.model.ClassInfo;
import diagram.model.Relationship;

import java.util.ArrayList;
import java.util.List;

public class ClassDiagram {
    private final List<ClassInfo> classes = new ArrayList<>();
    private final List<Relationship> relationships = new ArrayList<>();

    public void addClass(ClassInfo cls) {
        classes.add(cls);
    }

    public void addRelationship(Relationship rel) {
        relationships.add(rel);
    }

    public List<ClassInfo> getClasses() {
        return new ArrayList<>(classes); // 返回不可变副本以保证封装性
    }

    public List<Relationship> getRelationships() {
        return new ArrayList<>(relationships);
    }

    public String generateUML() {
        // 委托给生成器，传递当前数据
        return new ClassDiagramGenerator().generate(this);
    }

    /**
     * 你应当在迭代二中实现这个方法
     *
     * @return 返回代码中的“坏味道”
     */
    public List<String> getCodeSmells() {
        List<String> codeSmells = new ArrayList<>();

        // 检查每个类的 God Class, Lazy Class, Data Class
        for (ClassInfo classInfo : this.getClasses()) {
            // 检查 God Class
            if (classInfo.getAttributes().size() >= 20 || classInfo.getMethods().size() >= 20) {
                codeSmells.add("God Class: " + classInfo.getName());
            }

            // 检查 Lazy Class
            if (classInfo.getAttributes().isEmpty() || classInfo.getMethods().size() <= 1) {
                codeSmells.add("Lazy Class: " + classInfo.getName());
            }

            // 检查 Data Class
            if (!classInfo.isGodClass() && !classInfo.isLazyClass()) {
                boolean isDataClass = classInfo.getMethods().stream()
                        .filter(method -> !method.isConstructor())  // 排除构造方法
                        .allMatch(method -> method.getName().startsWith("get") || method.getName().startsWith("set"));
                if (isDataClass) {
                    codeSmells.add("Data Class: " + classInfo.getName());
                }
            }
        }

        return codeSmells;
    }

    /**
     * 你应当在迭代三中实现这个方法
     *
     * @param configFile 配置文件路径
     */
    public void loadConfig(String configFile) {
        return;
    }

}
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
        return null;
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
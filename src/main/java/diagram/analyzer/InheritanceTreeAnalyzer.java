package diagram.analyzer;

import diagram.ClassDiagram;
import diagram.model.ClassInfo;
import diagram.utils.InheritanceTree.InheritanceTreeUtils;

import java.util.ArrayList;
import java.util.List;

public class InheritanceTreeAnalyzer implements Analyzer {
    boolean active = true;

    @Override
    public List<String> analyze(ClassDiagram diagram) {
        List<String> result = new ArrayList<>();

        for (ClassInfo classInfo : diagram.getClasses()) {
            result.addAll(analyzeInheritanceTree(classInfo));
        }

        return result;
    }

    private List<String> analyzeInheritanceTree(ClassInfo classInfo) {
        List<String> result = new ArrayList<>();

        // 在处理继承树时，首先将 classInfo 本身作为继承链的起始节点
        List<String> inheritanceChains = new ArrayList<>();
        List<String> chains = InheritanceTreeUtils.buildInheritanceChains(classInfo);
        inheritanceChains.addAll(chains);

        // 输出每条继承链并判断其深度
        for (String chain : inheritanceChains) {
            if (chain.split(" <\\|-- ").length >= 6) {
                result.add("Inheritance Abuse: " + chain);
            }
        }

        // 获取所有子类并判断是否有 "过宽的继承"（子类数量 >= 10）
        List<ClassInfo> children = classInfo.getChildren();
        if (children.size() >= 10) {
            result.add("Too Many Children: " + classInfo.getName());
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

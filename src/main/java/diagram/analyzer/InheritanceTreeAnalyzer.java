package diagram.analyzer;

import diagram.ClassDiagram;
import diagram.model.ClassInfo;

import java.util.ArrayList;
import java.util.List;

public class InheritanceTreeAnalyzer implements Analyzer {
    boolean active;

    public InheritanceTreeAnalyzer(boolean _active) {
        active = _active;
    }

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

        // 在处理继承树时，首先将classInfo本身作为继承链的起始节点
        List<String> inheritanceChains = new ArrayList<>();
        List<String> chains = buildInheritanceChains(classInfo);
        inheritanceChains.addAll(chains);

        // 过深判断
        for (String chain : inheritanceChains) {
            if (chain.split(" <\\|-- ").length >= 6) {
                result.add("Inheritance Abuse: " + chain);
            }
        }

        // 过宽判断
        List<ClassInfo> children = classInfo.getChildren();
        if (children.size() >= 10) {
            result.add("Too Many Children: " + classInfo.getName());
        }

        return result;
    }

    // 构建每个类的所有继承链
    public List<String> buildInheritanceChains(ClassInfo classInfo) {
        List<String> chains = new ArrayList<>();
        buildInheritanceChainsRecursive(classInfo, new ArrayList<>(), chains);
        return chains;
    }

    private void buildInheritanceChainsRecursive(ClassInfo classInfo, List<String> currentChain, List<String> chains) {
        currentChain.add(classInfo.getName());

        // 如果当前类有子类，继续递归构建继承链
        List<ClassInfo> children = classInfo.getChildren();
        if (!children.isEmpty()) {
            for (ClassInfo child : children) {
                // 递归构建子类的继承链
                List<String> newChain = new ArrayList<>(currentChain);
                buildInheritanceChainsRecursive(child, newChain, chains);
            }
        } else {
            // 如果没有子类，保存当前链
            chains.add(String.join(" <|-- ", currentChain));
        }
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

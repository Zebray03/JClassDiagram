package diagram;

import diagram.detector.PatternDetector;
import diagram.detector.SingletonPatternDetector;
import diagram.detector.StrategyPatternDetector;
import diagram.model.ClassInfo;
import diagram.model.Relationship;
import diagram.utils.CircularDependencies.CircularDependenciesUtils;
import diagram.utils.InheritanceTree.InheritanceTreeUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        return classes;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public String generateUML() {
        return new ClassDiagramGenerator().generate(this);
    }

    /**
     * 你应当在迭代二中实现这个方法
     *
     * @return 返回代码中的“坏味道”
     */
    public List<String> getCodeSmells() {
        List<String> codeSmells = new ArrayList<>();

        for (ClassInfo classInfo : this.getClasses()) {
            if (!classInfo.isInterface() && !classInfo.isEnum()){
                // 类分析
                analyzeClass(classInfo, codeSmells);
            }

            // 继承树分析
            analyzeInheritanceTree(classInfo, codeSmells);
        }

        detectCircularDependencies(codeSmells);

        List<PatternDetector> detectors = List.of(
                new SingletonPatternDetector(),
                new StrategyPatternDetector()
        );

        codeSmells.addAll(detectors.stream()
                .flatMap(d -> d.detect(this).stream())
                .toList());

        return codeSmells;
    }

    private void analyzeClass(ClassInfo classInfo, List<String> codeSmells) {
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

    private void analyzeInheritanceTree(ClassInfo classInfo, List<String> codeSmells) {
        // 在处理继承树时，首先将 classInfo 本身作为继承链的起始节点
        List<String> inheritanceChains = new ArrayList<>();
        List<String> chains = InheritanceTreeUtils.buildInheritanceChains(classInfo);
        inheritanceChains.addAll(chains);

        // 输出每条继承链并判断其深度
        for (String chain : inheritanceChains) {
            if (chain.split(" <\\|-- ").length >= 6) {
                codeSmells.add("Inheritance Abuse: " + chain);
            }
        }

        // 获取所有子类并判断是否有 "过宽的继承"（子类数量 >= 10）
        List<ClassInfo> children = classInfo.getChildren();
        if (children.size() >= 10) {
            codeSmells.add("Too Many Children: " + classInfo.getName());
        }
    }

    /**
     * 循环依赖分析
     */
    private void detectCircularDependencies(List<String> codeSmells) {
        Map<String, List<String>> graph = new HashMap<>();
        // 初始化图
        for (ClassInfo classInfo : getClasses()) {
            graph.put(classInfo.getName(), new ArrayList<>());
        }

        // 填充依赖关系（仅包含依赖和关联关系）
        for (Relationship rel : relationships) {
            if (rel.getType().equals("DEPENDENCY") || rel.getType().equals("ASSOCIATION")) {
                graph.get(rel.getSource()).add(rel.getTarget());
            }
        }

        // 检查环
        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();
        List<String> currentPath = new ArrayList<>();

        for (String className : graph.keySet()) {
            if (!visited.contains(className)) {
                if (CircularDependenciesUtils.detectCycleDFS(
                        className,
                        graph,
                        visited,
                        inStack,
                        currentPath,
                        codeSmells
                )) {
                    // 检测到环后立即返回，确保只输出一个环（根据题目要求）
                    return;
                }
            }
        }
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
package diagram;

import diagram.analyzer.*;
import diagram.analyzer.design_pattern_analyzer.DesignPatternAnalyzer;
import diagram.model.ClassInfo;
import diagram.model.Relationship;

import java.util.*;

public class ClassDiagram {
    private final List<ClassInfo> classes;

    private final List<Relationship> relationships;

    private final Analyzer[] analyzers;

    public ClassDiagram() {
        classes = new ArrayList<>();
        relationships = new ArrayList<>();
        analyzers = new Analyzer[]{
                new ClassAnalyzer(),
                new InheritanceTreeAnalyzer(),
                new CircularDependencyAnalyzer(),
                new DesignPatternAnalyzer()
        };
    }

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

    public List<Analyzer> getAnalyzers() {
        return List.of(analyzers);
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

        for (Analyzer analyzer : this.getAnalyzers()) {
            if (analyzer.isActive()) {
                codeSmells.addAll(analyzer.analyze(this));
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
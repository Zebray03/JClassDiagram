package diagram;

import diagram.analyzer.*;
import diagram.analyzer.design_pattern_analyzer.DesignPatternAnalyzer;
import diagram.config.AnalyzerConfigParser;
import diagram.config.XMLAnalyzerConfigParser;
import diagram.model.ClassInfo;
import diagram.model.Relationship;

import java.util.*;
import java.util.stream.Collectors;

public class ClassDiagram {
    private final List<ClassInfo> classes;

    private final List<Relationship> relationships;

    private final Map<String, Analyzer> analyzers = new LinkedHashMap<>();

    private boolean configLoaded = false;

    public ClassDiagram() {
        classes = new ArrayList<>();
        relationships = new ArrayList<>();
        boolean defaultState = true;
        analyzers.put("ClassAnalyzer", new ClassAnalyzer(defaultState));
        analyzers.put("InheritanceTreeAnalyzer", new InheritanceTreeAnalyzer(defaultState));
        analyzers.put("CircularDependencyAnalyzer", new CircularDependencyAnalyzer(defaultState));
        analyzers.put("DesignPatternAnalyzer", new DesignPatternAnalyzer(defaultState));
    }

    /**
     * 你应当在迭代二中实现这个方法
     *
     * @return 返回代码中的“坏味道”
     */
    public List<String> getCodeSmells() {
        if (!configLoaded) {
            analyzers.values().forEach(a -> a.setActive(true));
        }

        return analyzers.values().stream()
                .filter(Analyzer::isActive)
                .flatMap(a -> a.analyze(this).stream())
                .collect(Collectors.toList());
    }

    /**
     * 你应当在迭代三中实现这个方法
     *
     * @param configFile 配置文件路径
     */
    public void loadConfig(String configFile) {
        AnalyzerConfigParser parser = getConfigParser(configFile);
        Set<String> activeAnalyzers = parser.parse(configFile);

        analyzers.forEach((name, analyzer) ->
                analyzer.setActive(activeAnalyzers.contains(name)));

        configLoaded = true;
    }

    private AnalyzerConfigParser getConfigParser(String configFile) {
        if (configFile.endsWith(".xml")) {
            return new XMLAnalyzerConfigParser();
        }
        // 可扩展xml以外格式
        throw new UnsupportedOperationException("Unsupported config format");
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

    public String generateUML() {
        return new ClassDiagramGenerator().generate(this);
    }

}
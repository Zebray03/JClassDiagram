package diagram.analyzer.design_pattern_analyzer;

import diagram.ClassDiagram;

import java.util.List;

public interface PatternAnalyzer {
    List<String> analyze(ClassDiagram diagram);
}

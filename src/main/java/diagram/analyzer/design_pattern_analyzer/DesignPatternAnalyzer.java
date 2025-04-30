package diagram.analyzer.design_pattern_analyzer;

import diagram.ClassDiagram;
import diagram.analyzer.Analyzer;

import java.util.ArrayList;
import java.util.List;

public class DesignPatternAnalyzer implements Analyzer {
    boolean active;

    PatternAnalyzer[] patternAnalyzers;

    public DesignPatternAnalyzer() {
        active = true;
        patternAnalyzers = new PatternAnalyzer[]{
                new SingletonPatternAnalyzer(),
                new StrategyPatternAnalyzer()
        };
    }

    @Override
    public List<String> analyze(ClassDiagram diagram) {
        List<String> result = new ArrayList<>();

        for (PatternAnalyzer analyzer : patternAnalyzers) {
            result.addAll(analyzer.analyze(diagram));
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

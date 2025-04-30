package diagram.analyzer;

import diagram.ClassDiagram;

import java.util.List;

public class DesignPatternAnalyzer implements Analyzer {
    boolean active = true;

    @Override
    public List<String> analyze(ClassDiagram diagram) {
        return null;
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

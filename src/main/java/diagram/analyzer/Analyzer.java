package diagram.analyzer;

import diagram.ClassDiagram;

import java.util.List;

public interface Analyzer {
    List<String> analyze(ClassDiagram diagram);

    boolean isActive();

    void setActive(boolean _active);
}

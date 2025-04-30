package diagram.detector;

import diagram.ClassDiagram;

import java.util.List;

public interface PatternDetector {
    List<String> detect(ClassDiagram diagram);
}

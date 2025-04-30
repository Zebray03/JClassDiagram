package diagram.detector;

import diagram.ClassDiagram;
import diagram.model.ClassInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StrategyPatternDetector implements PatternDetector{
    @Override
    public List<String> detect(ClassDiagram diagram) {
        List<String> results = new ArrayList<>();
        detectStrategyInterface(diagram).forEach(strategy -> {
            List<ClassInfo> concreteStrategies = getConcreteStrategies(strategy, diagram);
            if (concreteStrategies.size() >= 2 && hasContextClass(strategy, diagram)) {
                results.add("Possible Design Patterns: Strategy Pattern - " + strategy.getName());
            }
        });
        return results;
    }

    private List<ClassInfo> detectStrategyInterface(ClassDiagram diagram) {
        return diagram.getClasses().stream()
                .filter(c -> (c.isInterface() || c.isAbstract()) &&
                        (c.getName().endsWith("Strategy") ||
                                c.getName().endsWith("Policy") ||
                                c.getName().endsWith("Behavior")) &&
                        !c.getMethods().isEmpty())
                .collect(Collectors.toList());
    }

    private List<ClassInfo> getConcreteStrategies(ClassInfo strategy, ClassDiagram diagram) {
        return diagram.getClasses().stream()
                .filter(c -> !c.isInterface() && !c.isAbstract() &&
                        c.getImplementedTypes().contains(strategy.getName()))
                .collect(Collectors.toList());
    }

    private boolean hasContextClass(ClassInfo strategy, ClassDiagram diagram) {
        return diagram.getRelationships().stream()
                .anyMatch(r -> r.getType().equals("ASSOCIATION") &&
                        r.getTarget().equals(strategy.getName()));
    }
}

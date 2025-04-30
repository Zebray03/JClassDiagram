package diagram.analyzer;

import diagram.ClassDiagram;
import diagram.model.ClassInfo;
import diagram.model.Relationship;

import java.util.*;

public class CircularDependencyAnalyzer implements Analyzer {
    boolean active = true;

    @Override
    public List<String> analyze(ClassDiagram diagram) {
        return detectCircularDependencies(diagram);
    }

    private List<String> detectCircularDependencies(ClassDiagram diagram) {
        List<String> result = new ArrayList<>();

        Map<String, List<String>> graph = new HashMap<>();
        // 初始化图
        for (ClassInfo classInfo : diagram.getClasses()) {
            graph.put(classInfo.getName(), new ArrayList<>());
        }

        // 填充依赖关系（仅包含依赖和关联关系）
        for (Relationship rel : diagram.getRelationships()) {
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
                if (detectCycleDFS(
                        className,
                        graph,
                        visited,
                        inStack,
                        currentPath,
                        result
                )) {
                    // 检测到环后立即返回，确保只输出一个环
                    break;
                }
            }
        }
        return result;
    }

    public boolean detectCycleDFS(
            String current,
            Map<String, List<String>> graph,
            Set<String> visited,
            Set<String> inStack,
            List<String> currentPath,
            List<String> codeSmells
    ) {
        if (inStack.contains(current)) {
            int startIdx = currentPath.indexOf(current);
            // 截取从环起点到当前节点的路径
            List<String> cycle = new ArrayList<>(currentPath.subList(startIdx, currentPath.size()));
            cycle.add(current); // 闭合环

            // 反转路径列表以匹配依赖方向（例如 [X, Y, Z] → [Z, Y, X]）
            Collections.reverse(cycle);

            // 使用正确的依赖符号拼接路径
            String cyclePath = String.join(" <.. ", cycle);
            codeSmells.add("Circular Dependency: " + cyclePath);
            return true;
        }

        if (visited.contains(current)) {
            return false;
        }

        visited.add(current);
        inStack.add(current);
        currentPath.add(current);

        for (String neighbor : graph.getOrDefault(current, new ArrayList<>())) {
            if (detectCycleDFS(neighbor, graph, visited, inStack, currentPath, codeSmells)) {
                return true;
            }
        }

        inStack.remove(current);
        currentPath.remove(currentPath.size() - 1);
        return false;
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

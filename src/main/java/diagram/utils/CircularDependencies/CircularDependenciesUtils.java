package diagram.utils.CircularDependencies;

import java.util.*;

public class CircularDependenciesUtils {
    public static boolean detectCycleDFS(
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
}
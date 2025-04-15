package diagram.utils.InheritanceTree;

import diagram.model.ClassInfo;

import java.util.ArrayList;
import java.util.List;

public class InheritanceTreeUtils {
    // 构建每个类的所有继承链
    public static List<String> buildInheritanceChains(ClassInfo classInfo) {
        List<String> chains = new ArrayList<>();
        buildInheritanceChainsRecursive(classInfo, new ArrayList<>(), chains);
        return chains;
    }

    private static void buildInheritanceChainsRecursive(ClassInfo classInfo, List<String> currentChain, List<String> chains) {
        currentChain.add(classInfo.getName());

        // 如果当前类有子类，继续递归构建继承链
        List<ClassInfo> children = classInfo.getChildren();
        if (!children.isEmpty()) {
            for (ClassInfo child : children) {
                // 递归构建子类的继承链
                List<String> newChain = new ArrayList<>(currentChain);
                buildInheritanceChainsRecursive(child, newChain, chains);
            }
        } else {
            // 如果没有子类，说明到了继承链的末尾，保存当前链
            chains.add(String.join(" <|-- ", currentChain));
        }
    }
}

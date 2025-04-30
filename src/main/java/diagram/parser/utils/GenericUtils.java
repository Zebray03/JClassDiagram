package diagram.parser.utils;

import java.util.ArrayList;
import java.util.List;

public class GenericUtils {
    private static final String BUILT_IN_TYPES = "String|int|boolean|List|Map|Set|Optional|ArrayList|HashMap";

    public static List<String> extractCustomTypes(String type) {
        List<String> types = new ArrayList<>();
        decomposeType(type, types);
        return types.stream()
                .filter(t -> !isBuiltInType(t))
                .distinct()
                .toList();
    }

    private static void decomposeType(String type, List<String> result) {
        if (type == null || type.isEmpty()) return;

        // 1. 去除数组符号
        String normalizedType = type.replaceAll("\\[]", "");

        // 2. 提取原始类型（如 Map<...> 的 Map）
        String rawType = normalizedType.replaceAll("<.*", "").trim();
        if (!isBuiltInType(rawType)) {
            result.add(rawType);
        }

        // 3. 递归处理泛型参数（手动解析嵌套结构）
        List<String> genericParts = extractGenericParts(normalizedType);
        for (String part : genericParts) {
            decomposeType(part, result);
        }
    }

    // 手动解析泛型内容，支持嵌套结构
    private static List<String> extractGenericParts(String type) {
        List<String> parts = new ArrayList<>();
        int start = type.indexOf('<');
        if (start == -1) return parts; // 无泛型内容

        int end = findMatchingClosingBracket(type, start);
        if (end == -1) return parts; // 格式错误，忽略

        String innerContent = type.substring(start + 1, end);
        return splitGenericParameters(innerContent);
    }

    // 找到匹配的闭合尖括号位置
    private static int findMatchingClosingBracket(String str, int start) {
        int depth = 1;
        for (int i = start + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '<') depth++;
            else if (c == '>') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1; // 未找到匹配
    }

    // 安全分割泛型参数（跟踪嵌套深度）
    private static List<String> splitGenericParameters(String content) {
        List<String> params = new ArrayList<>();
        int depth = 0;
        StringBuilder buffer = new StringBuilder();

        for (char c : content.toCharArray()) {
            if (c == '<') depth++;
            else if (c == '>') depth--;

            if (c == ',' && depth == 0) {
                params.add(buffer.toString().trim());
                buffer.setLength(0);
            } else {
                buffer.append(c);
            }
        }
        if (!buffer.isEmpty()) {
            params.add(buffer.toString().trim());
        }
        return params;
    }

    public static boolean isBuiltInType(String type) {
        return type.matches(BUILT_IN_TYPES);
    }
}

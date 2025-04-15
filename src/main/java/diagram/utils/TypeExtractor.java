package diagram.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeExtractor {
    private static final Pattern GENERIC_PATTERN = Pattern.compile("<([^<>]+)>");  // 泛型匹配
    private static final String BUILT_IN_TYPES = "String|int|boolean|List|Map|Set|Optional|ArrayList|HashMap";

    public static List<String> extractCustomTypes(String type) {
        List<String> types = new ArrayList<>();
        decomposeType(type, types);
        return types.stream()
                .filter(t -> !isBuiltInType(t)) // 过滤内建类型
                .distinct()
                .toList();
    }

    // 提取类型并递归处理泛型
    private static void decomposeType(String type, List<String> result) {
        if (type == null || type.isEmpty()) return;

        // 去除数组符号（如 B[] → B）
        String normalizedType = type.replaceAll("\\[]", "");

        // 提取原始类型（如 List<B> → List，忽略后继续递归）
        String rawType = normalizedType.replaceAll("<.*", "").trim();

        // 递归处理泛型内容
        Matcher matcher = GENERIC_PATTERN.matcher(normalizedType);
        if (matcher.find()) {
            // 获取泛型部分，例如 <A, B> 或 <Book>，以逗号分隔的类型
            String innerTypes = matcher.group(1);
            // 提取每个泛型类型（处理 <A, B> 这种多类型参数）
            for (String innerType : innerTypes.split(",")) {
                decomposeType(innerType.trim(), result);  // 提取每个泛型类型
            }
        }

        // 添加非基础类型
        if (!isBuiltInType(rawType)) {
            result.add(rawType); // 只添加非基础类型（如 A、B）
        }
    }

    public static boolean isBuiltInType(String type) {
        return type.matches(BUILT_IN_TYPES); // 判断是否为内建类型
    }
}

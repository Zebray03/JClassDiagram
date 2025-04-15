package diagram.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeExtractor {
    private static final Pattern GENERIC_PATTERN = Pattern.compile("<([^<>]+)>");
    private static final String BUILT_IN_TYPES = "String|int|boolean|List|Map|Set|Optional";

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

        // 提取最外层类型（如 List<Map<Classroom, Score>> → List, Map, Classroom, Score）
        String rawType = type.split("<")[0].trim();
        if (!isBuiltInType(rawType)) {
            result.add(rawType);
        }

        // 递归处理泛型内容
        Matcher matcher = GENERIC_PATTERN.matcher(type);
        if (matcher.find()) {
            String innerTypes = matcher.group(1);
            for (String innerType : innerTypes.split(",")) {
                decomposeType(innerType.trim(), result);
            }
        }
    }

    public static boolean isBuiltInType(String type) {
        return type.matches(BUILT_IN_TYPES);
    }
}
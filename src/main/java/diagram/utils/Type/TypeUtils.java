package diagram.utils.Type;

public class TypeUtils {
    public static String fixGenericTypeFormat(String type) {
        return type.replaceAll(",(?=\\S)", ", ");
    }

    public static String getVisibilitySymbol(String visibility) {
        switch (visibility.toLowerCase()) {
            case "private": return "-";
            case "protected": return "#";
            case "package private": return "~";
            default: return "+";
        }
    }

    public static int getVisibilityOrder(String visibility) {
        switch (visibility.toLowerCase()) {
            case "private": return 0;
            case "protected": return 1;
            case "package private": return 2;
            default: return 3; // public
        }
    }
}
package xyz.hamster.tools.utils;

@Deprecated
public class Utils {
    public static String capitalizeString(String str) {
        return str == null
                ? null
                : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String replaceIfEndsWith(String input, String target, String replacement) {
        if (input.endsWith(target)) {
            int lastIndex = input.lastIndexOf(target);
            if (lastIndex > 0) {
                return input.substring(0, lastIndex) + replacement;
            } else {
                return replacement;
            }
        }
        return input;
    }

    public static boolean stringHasValue(String str) {
        return str != null && !str.isBlank();
    }
}

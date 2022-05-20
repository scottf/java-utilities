package scottf;

public class ParseNumber {
    public static int parseInt(String val) {
        long l = parseLong(val);
        if (l > (long)Integer.MAX_VALUE || l < (long)Integer.MIN_VALUE) {
            throw new NumberFormatException(
                "Input string outside results in a number outside of the range for an int: \"" + val + "\"");
        }
        return (int)l;
    }

    public static long parseLong(String val) {
        String vl = val
            .trim()
            .toLowerCase()
            .replaceAll("_", "")
            .replaceAll(",", "")
            .replaceAll("\\.", "");

        long factor = 1;
        int fl = 1;
        if (vl.endsWith("k")) {
            factor = 1000;
        }
        else if (vl.endsWith("ki")) {
            factor = 1024;
            fl = 2;
        }
        else if (vl.endsWith("m")) {
            factor = 1_000_000;
        }
        else if (vl.endsWith("mi")) {
            factor = 1024 * 1024;
            fl = 2;
        }
        else if (vl.endsWith("g")) {
            factor = 1_000_000_000;
        }
        else if (vl.endsWith("gi")) {
            factor = 1024 * 1024 * 1024;
            fl = 2;
        }
        if (factor > 1) {
            vl = vl.substring(0, vl.length() - fl);
        }
        return Long.parseLong(vl) * factor;
    }
}

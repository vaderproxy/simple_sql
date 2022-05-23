package base;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class Hex {

    public String StrToHex(String str) {
        String res = "";
        for (int i = 0; i < str.length(); i++) {
            int code = (int) str.charAt(i);
            res += Integer.toHexString(code);
        }

        return res;
    }

    public String formatHex(String str) {
        String res = this.StrToHex(str);
        res = "0x" + res;
        return res;
    }

    public static String formatHexShort(String str) {
        Hex hex = new Hex();
        String res = hex.StrToHex(str);
        res = "0x" + res;
        return res;
    }

    public static String unhex(String str) {
        if (str.length() % 2 > 0) {
            return "";
        }

        byte[] res = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            String chr = str.substring(i, i + 2);
            try {
                res[i / 2] = (byte) Integer.parseInt(chr, 16);
            } catch (Exception ex) {
                return "";
            }
        }

        try {
            String rStr = new String(res, "UTF8");
            return rStr;
        } catch (Exception ex) {
            return "";
        }

    }

    public static String urlHex(String str) {
        String res = "";
        for (int i = 0; i < str.length(); i++) {
            int code = (int) str.charAt(i);
            res += "%" + Integer.toHexString(code);
        }

        res = res.replace('+', ' ');
        return res.toUpperCase();
    }

}

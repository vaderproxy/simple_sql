package struct;

import base.Helper;
import base.IniFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    public static Map<String, String> trans = new HashMap();

    public static void load(String ini) throws Exception {
        IniFile ifile = new IniFile(ini);
        trans = ifile.get_section("translate");
    }

    public static String get(String name) {
        return trans.get(name);
    }

    public static String get(String name, String param) {
        String r = trans.get(name);
        r = Helper.replace(r, "%s", param);
        return r;
    }

    public static String get(String name, int param) {
        String r = trans.get(name);
        String p = "" + param;
        r = Helper.replace(r, "%s", p);
        return r;
    }

}

package core;

import base.Hex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struct.*;

public class SqlParse {

    public SqlTest createSql(UrlLinks url, int param) {
        SqlTest res = new SqlTest();
        String paramlist = "";

        switch (url.type) {
            case 1:
                for (int i = 0; i < url.paramsCount; i++) {
                    UrlParam keyvalue = (UrlParam) url.params.get(i);
                    String key = keyvalue.param;
                    String value = keyvalue.value;
                    if (param == i) {
                        res.paramName = key;
                        if (!Pattern.matches("^\\d+$", value)) {
                            res.type = 1;
                        }
                        res.param = value;
                        value = "%Inject_Here%";
                    }
                    if (i > 0) {
                        paramlist += "&";
                    }
                    paramlist += key + "=" + value;
                }
                res.url = url.base + "?" + paramlist;
                break;
            case 2:
                Pattern p = Pattern.compile("(\\d+)/");
                Matcher m = p.matcher(url.appendix);

                for (int i = 0; i < url.paramsCount; i++) {
                    m.find();
                    String value = m.group(1);
                    if (i == param) {
                        res.param = value;
                        value = "%Inject_Here%";
                    }
                    paramlist += "/" + value;
                }
                res.url = url.base + paramlist;
                break;

        }

        return res;
    }

    public SqlTest createCookieSql(CookieUrl curl, String param) {
        SqlTest res = new SqlTest();
        res.url = curl.url;
        String paramlist = "";
        for (Entry<String, String> entry : curl.cookie.cookies.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String hexvalue = Hex.urlHex(value);
            if (param.equals(key)) {
                if (!Pattern.matches("^\\d+$", value)) {
                    res.type = 1;
                }
                res.param = value;
                hexvalue = "%Inject_Here%";
            }
            if (paramlist.length() > 0) {
                paramlist += ";";
            }
            paramlist += key + "=" + hexvalue;
        }

        String[] iList = paramlist.split(";");
        String cookieText = paramlist;
        for (int i = 0; i < iList.length; i++) {
            String tmp = iList[i];
            if (tmp.indexOf("%Inject_Here%") >= 0) {
                cookieText = tmp;
                break;
            }
        }

        res.cookiestr = cookieText;
        return res;
    }

    public void clearLasrSql(ArrayList<SqlTest> list, ArrayList<SqlTest> last) {
        for (int i = 0; i < last.size(); i++) {
            SqlTest sTest = last.get(i);
            for (int j = 0; j < list.size(); j++) {
                SqlTest sTest2 = list.get(j);
                if (sTest.equals(sTest2)) {
                    list.remove(j);
                    j--;
                    continue;
                }
            }
        }
    }

    public ArrayList<SqlTest> parseUrlList(UrlLinks url) {
        ArrayList<SqlTest> res = new ArrayList();

        if (url.type == 0) {
            return res;
        }

        for (int j = 0; j < url.paramsCount; j++) {
            try {
                res.add(this.createSql(url, j));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        for (int i = 0; i < res.size() - 1; i++) {
            SqlTest sTest = res.get(i);
            for (int j = i + 1; j < res.size(); j++) {
                SqlTest sTest2 = res.get(j);
                if (sTest.equals(sTest2)) {
                    res.remove(j);
                    j--;
                    continue;
                }
            }
        }

        return res;
    }

    public ArrayList<SqlTest> parseUrlList(ArrayList<UrlLinks> urls) {
        ArrayList<SqlTest> res = new ArrayList();
        for (int i = 0; i < urls.size(); i++) {
            UrlLinks url = urls.get(i);
            if (url.type == 0) {
                continue;
            }
            for (int j = 0; j < url.paramsCount; j++) {
                try {
                    res.add(this.createSql(url, j));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

        }

        for (int i = 0; i < res.size() - 1; i++) {
            SqlTest sTest = res.get(i);
            for (int j = i + 1; j < res.size(); j++) {
                SqlTest sTest2 = res.get(j);
                if (sTest.equals(sTest2)) {
                    res.remove(j);
                    j--;
                    continue;
                }
            }
        }

        return res;
    }

    public ArrayList<SqlTest> parseCookieList(ArrayList<CookieUrl> urls) {
        ArrayList res = new ArrayList();
        for (int i = 0; i < urls.size(); i++) {
            CookieUrl cookie = urls.get(i);
            for (Map.Entry<String, String> entry : cookie.cookie.cookies.entrySet()) {
                String key = entry.getKey();
                String test = key.toLowerCase();
                if (key.equals("PHPSESSID")) {
                    continue;
                }

                if (test.equals("expires")) {
                    continue;
                }

                if (test.equals("domain")) {
                    continue;
                }

                if (test.equals("path")) {
                    continue;
                }

                if (test.equals("max-age")) {
                    continue;
                }

                res.add(this.createCookieSql(cookie, key));
            }
        }

        return res;
    }

}

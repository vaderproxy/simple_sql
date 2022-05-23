package core;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struct.*;

public class LinkParse {

    public String baseurl;

    /*
     Функция ссылку вида www.site.ru/page1/page2/ 
     сделает http://www.site.ru/page1/page2
     */
    public String trimLink(String site) {
        site = site.toLowerCase();
        if (!Pattern.matches("^https?://.*?", site)) {
            site = "http://" + site;
        }
        site = site.trim();
        site = site.replaceAll("//(www\\.)?", "//");
        site = site.replaceAll("\\?amp;", "");
        if (site.indexOf("#") != -1) {
            site = site.replaceAll("(.*?)#.*?", "$1");
        }
        if (Pattern.matches("(.*?)/$", site)) {
            site = site.replaceAll("(.*?)/$", "$1");
        }

        return site;
    }

    /*
     Убирает все http, возвращает только домен без слешей.
     */
    public String getSiteBase(String site) {
        site = site.toLowerCase();
        site = site.replaceAll("https?://(www\\.)?", "");
        int ind = site.indexOf('/');
        if (ind > -1) {
            site = site.substring(0, ind);
        }
        return site;
    }

    /*
     Функция ссылку вида site.ru/page1/page2/index.php 
     сделает http://site.ru/page1/page2 */
    public String getLinkBase(String link) {
        String http = "";
        Pattern p = Pattern.compile("^(https?://).*?");
        Matcher m = p.matcher(link);
        if (m.find()) {
            http = m.group(1);
        }
        link = link.replaceAll("^https?://(www\\.)?(.*?)$", "$2");
        int ind = link.lastIndexOf("/");
        if (ind != -1) {
            link = http + link.substring(0, ind);
        } else {
            link = http + link;
        }
        return link;
    }

    public boolean checkUrl(String url, String base) {
        base = this.getSiteBase(base);
        //System.out.println(url);
/*
         if (url.indexOf(".jpg") > 0) {
         System.out.println();
         }
         */

        try {
            if (Pattern.matches("^mailto.*?", url)) {
                return false;
            }
            if (Pattern.matches("javascript:.*?", url)) {
                return false;
            }
            if (Pattern.matches(".*?=.*?" + base + ".*?", url.toLowerCase())) {
                return false;
            }

            if (Pattern.matches("\\.(png|gif|jpg|jpeg|css|js)\\?.+", url.toLowerCase())) {
                return false;
            }

            if (url.indexOf(base) == -1) {
                return false;
            }

            if (url.indexOf('<') >= 0) {
                return false;
            }

            if (url.indexOf('#') == 0) {
                return false;
            }

            /*
             if (!Pattern.matches("^https?://.*?", url)) {
             return true;
             }
             */
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public UrlLinks createLink(String url, String base) {
        url = url.replaceAll("^//", "http://");
        UrlLinks link = new UrlLinks();
        base = this.getLinkBase(base);
        if (!Pattern.matches("^https?://.*?", url)) {
            if (Pattern.matches("^/.*?", url)) {
                url = this.trimLink(this.getSiteBase(base)) + url;
            } else {
                url = base + "/" + url;
            }
        };
        url = this.trimLink(url);
        link.url = url;
        int type = 0;
        String baseurl = url;
        String appendix = "";
        if (url.indexOf("?") != -1) {
            type = 1;
            baseurl = url.substring(0, url.indexOf("?"));
            appendix = url.substring(url.indexOf('?') + 1);
        }

        Pattern p = Pattern.compile("^(.*?)((/\\d+)+)$");
        Matcher m = p.matcher(url);

        if (m.find()) {
            type = 2;
            baseurl = m.group(1);
            appendix = m.group(2);
            appendix = appendix.replaceAll("^/(.*?)$", "$1");
            if (!Pattern.matches("^(.*?)/$", appendix)) {
                appendix += '/';
            }
            link.paramsCount = appendix.replaceAll("[^/]", "").length();
        }

        link.base = baseurl;
        link.type = type;
        link.appendix = appendix;

        if (type == 1) {
            link.params = this.getUrlParams(link);
            link.paramsCount = link.params.size();
        }
        return link;
    }

    public ArrayList getUrlParams(UrlLinks ln) {
        String params = ln.appendix;
        int type = ln.type;
        ArrayList arr = new ArrayList();
        switch (type) {
            case 1:
                String[] prms = params.split("&");
                for (int i = 0; i < prms.length; i++) {
                    String[] param = prms[i].split("=");
                    if (param.length != 2) {
                        continue;
                    }
                    UrlParam urlparam = new UrlParam();
                    urlparam.param = param[0];
                    urlparam.value = param[1];
                    arr.add(urlparam);
                }
                break;
            case 2:
                break;

        }

        for (int i = 0; i < arr.size(); i++) {
            UrlParam temp;
            UrlParam param1 = (UrlParam) arr.get(i);
            for (int j = i + 1; j < arr.size(); j++) {
                UrlParam param2 = (UrlParam) arr.get(j);
                if (param1.param.compareTo(param2.param) > 0) {
                    temp = param1;
                    param1 = param2;
                    param2 = temp;
                    arr.set(i, param1);
                    arr.set(j, param2);
                }
            };
        }

        return arr;

    }

    public boolean isUrlExists(UrlLinks ln, ArrayList allurls) {
        int size = allurls.size();
        for (int i = 0; i < size; i++) {
            UrlLinks link = (UrlLinks) allurls.get(i);
            if (link.type != ln.type) {
                continue;
            }
            if (!ln.base.equals(link.base)) {
                continue;
            }

            if (link.url.equals(ln.url)) {
                return true;
            }

            switch (ln.type) {
                case 1:
                    //if (link.getTMPUrl().equals(ln.getTMPUrl())) {
                    //    return true;
                    //}

                    if (ln.paramsCount != link.paramsCount) {
                        continue;
                    }
                    boolean ravno = true;
                    for (int j = 0; j < link.params.size(); j++) {
                        UrlParam lnparam = (UrlParam) ln.params.get(j);
                        UrlParam linkparam = (UrlParam) link.params.get(j);
                        if (lnparam.param.equals(linkparam.param)) {
                            continue;
                        }
                        ravno = false;
                        break;
                    }
                    if (ravno) {
                        return ravno;
                    }
                    break;
                case 2:
                    if (ln.paramsCount == link.paramsCount) {
                        return true;
                    }
                    break;

            }

        }

        return false;
    }

}

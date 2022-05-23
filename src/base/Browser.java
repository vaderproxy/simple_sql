package base;

import base.*;
import errors.BrowserException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struct.UrlLinks;

public class Browser {

    public int code = 0;
    public Cookies cookies;
    public String specifyCookie = null;
    public String location = "";
    public boolean hasRedirect = false;
    public int maxRedirs = 3;
    public int follow = 3;
    private int followLoc = 3;
    public static Map<Long, Long> tstart;
    public static Map<Long, Boolean> sql_found;
    private String noInternet = "No internet!";

    public int maxStrLen = 10000000;
    public int maxBufLen = 10000000;

    public String url = "";
    public String html = "";

    public Browser() {
        cookies = new Cookies();
    }

    public String getHTMLPage(String site) {
        try {
            return this.getHTML(site);
        } catch (Exception ex) {
            return "";
        }
    }

    public String getSiteBase(String site) {
        site = site.toLowerCase();
        site = site.replaceAll("https?://(www\\.)?", "");
        int ind = site.indexOf('/');
        if (ind > -1) {
            site = site.substring(0, ind);
        }
        return site;
    }

    public String getHTML(String site) throws BrowserException {
        if (WebSQL.finish) {
            throw new BrowserException("finish:" + site);
        }
        this.followLoc = follow;
        this.hasRedirect = false;
        return this.getPage(site);

    }

    public void getHTTPHTML(String site) {
        this.followLoc = follow;
        this.hasRedirect = false;
        html = this.getPage(site);
    }

    public String getHTML() throws BrowserException {
        return this.getHTML(this.url);
    }

    public String getPage(String site) {
        try {
            URL url = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);//???
            //connection.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:57.0) Gecko/20100101 Firefox/57.0");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Content-Language", "en-US");
            if (this.specifyCookie == null) {
                conn.setRequestProperty("Cookie", cookies.cookieSTR());
            } else {
                conn.setRequestProperty("Cookie", this.specifyCookie);
            }
            //conn.connect();
            String html = "";
            StringBuffer rhtml = new StringBuffer();

            String loc = null;
            String base1 = this.getSiteBase(site);
            for (int i = 1;; i++) {
                String headerName = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);
                if ((headerName != null)) {
                    if (headerName.equals("Set-Cookie")) {
                        cookies.setCookies(headerValue);
                    };

                    if (headerName.equals("Location")) {
                        String loctemp = headerValue;
                        if (!Pattern.matches("https?://.*?", loctemp)) {
                            if (loctemp.indexOf('/') != 0) {
                                loctemp = '/' + loctemp;
                            }
                            loctemp = "http://" + base1 + loctemp;
                        }
                        String base2 = this.getSiteBase(loctemp);
                        if ((base1.equals(base2))) {
                            loc = loctemp;
                        }
                    };
                }

                if (headerName == null && headerValue == null) {
                    break;
                }
            }

            code = conn.getResponseCode();
            location = loc;

            if (followLoc == -1) {
                return "";
            }

            if (location != null) {
                if (followLoc != 0) {
                    this.hasRedirect = true;
                    if (followLoc > 0) {
                        followLoc--;
                    } else {
                        followLoc++;
                    }
                    return this.getPage(location);
                }
            }

            if (followLoc < 0) {
                return "";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                if (decodedString.length() > this.maxStrLen) {
                    break;
                }
                rhtml.append(decodedString);
                if (rhtml.length() > this.maxBufLen) {
                    break;
                }
            }
            in.close();

            html = rhtml.toString();

            if (code >= 400) {
                throw new BrowserException("Code >=400!");
            }

            return html;

        } catch (Exception ex) {

            return "";
        }

    }

    public String excludePost(String site, String post) throws BrowserException {
        try {
            URL url = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            //connection.setInstanceFollowRedirects(false);
            if (post.isEmpty()) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Length", Integer.toString(post.getBytes().length));
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
            }
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:57.0) Gecko/20100101 Firefox/57.0");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Content-Language", "en-US");
            if (this.specifyCookie == null) {
                conn.setRequestProperty("Cookie", cookies.cookieSTR());
            } else {
                conn.setRequestProperty("Cookie", this.specifyCookie);
            }

            if (post.length() > 0) {
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(
                        conn.getOutputStream());
                wr.writeBytes(post);
                wr.flush();
                wr.close();
            };

            //conn.connect();
            String html = "";
            StringBuffer rhtml = new StringBuffer();

            String loc = null;
            String base1 = this.getSiteBase(site);
            for (int i = 1;; i++) {
                String headerName = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);
                if ((headerName != null)) {
                    if (headerName.equals("Set-Cookie")) {
                        cookies.setCookies(headerValue);
                    };

                    if (headerName.equals("Location")) {
                        String loctemp = headerValue;
                        if (!Pattern.matches("https?://.*?", loctemp)) {
                            if (loctemp.indexOf('/') != 0) {
                                loctemp = '/' + loctemp;
                            }
                            loctemp = "http://" + base1 + loctemp;
                        }
                        String base2 = this.getSiteBase(loctemp);
                        if ((base1.equals(base2))) {
                            loc = loctemp;
                        }
                    };
                }

                if (headerName == null && headerValue == null) {
                    break;
                }
            }

            code = conn.getResponseCode();
            location = loc;

            if (followLoc == -1) {
                return "";
            }

            if (location != null) {
                if (followLoc != 0) {
                    this.hasRedirect = true;
                    if (followLoc > 0) {
                        followLoc--;
                    } else {
                        followLoc++;
                    }
                    return this.getPage(location);
                }
            }

            if (followLoc < 0) {
                return "";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                if (decodedString.length() > this.maxStrLen) {
                    break;
                }
                rhtml.append(decodedString);
                if (rhtml.length() > this.maxBufLen) {
                    break;
                }
            }
            in.close();

            html = rhtml.toString();

            if (code >= 400) {
                throw new BrowserException("Code >=400!");
            }

            return html;

        } catch (Exception ex) {

            return "";
        }
    }

}

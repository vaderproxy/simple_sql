package base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import struct.Config;

public class miniBrowser {

    public Cookies cookies;
    public String loc_need = "";
    public String last_reloc = "";

    public miniBrowser() {
        cookies = new Cookies();
    }

    public String getWPBase(String site) {
        site = site.toLowerCase();
        site = site.replaceAll("https?://(www\\.)?", "");
        int ind = site.lastIndexOf('/');
        if (ind > -1) {
            site = site.substring(0, ind);
        }
        return site;
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

    public String getHostName(String site) {
        String host = getSiteBase(site);
        int ind = host.indexOf('.');
        if (ind > -1) {
            host = host.substring(0, ind);
        }
        return host;
    }

   
    public String excutePost(String targetURL, String urlParameters, int follow) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(60000);
            if (urlParameters.length() > 0) {
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
            } else {
                connection.setRequestMethod("GET");
            }

            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Cookie",
                    cookies.cookieSTR());
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 5.1; rv:32.0) Gecko/20100101 Firefox/32.0");
            connection.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            if (urlParameters.length() > 0) {
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            };

            String loc = null;
            for (int i = 1;; i++) {
                String headerName = connection.getHeaderFieldKey(i);
                String headerValue = connection.getHeaderField(i);
                if ((headerName != null)) {
                    if (headerName.equals("Set-Cookie")) {
                        cookies.setCookies(headerValue);
                    };

                    if (headerName.equals("Location")) {
                        String loctemp = headerValue;
                        String base1 = this.getSiteBase(targetURL);
                        if (!Pattern.matches("https?://.*?", loctemp)) {
                            if (loctemp.indexOf('/') != 0) {
                                loctemp = '/' + loctemp;
                            }
                            loctemp = "http://" + base1 + loctemp;
                        }
                        String base2 = this.getSiteBase(loctemp);
                        if ((base1.equals(base2)) && (loctemp.indexOf(this.loc_need) > -1)) {
                            loc = loctemp;
                        }
                    };
                }

                if (headerName == null && headerValue == null) {
                    break;
                }
            }

            if (follow == -1) {
                return "";
            }

            if ((follow != 0) && (loc != null)) {
                if (follow < 0) {
                    follow++;
                } else {
                    follow--;
                }

                this.last_reloc = loc;
                return this.excutePost(loc, urlParameters, follow);
            }

            if (follow < 0) {
                return "";
            }
            //Get Response	
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\r\n");
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {
            //miniBrowser.wlog(e);
            //e.printStackTrace();
            return "";

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String excutePost(String targetURL, String urlParameters) {
        return excutePost(targetURL, urlParameters, 3);
    }

    public String getPage(String targetURL) {
        return this.excutePost(targetURL, "");
    }
}

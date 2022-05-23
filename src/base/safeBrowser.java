package base;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class safeBrowser {

    public int code = 0;
    public Cookies cookies;
    public String specifyCookie = null;
    public String location = "";
    public boolean hasRedirect = false;
    public int maxRedirs = 3;
    public int follow = 3;
    private int followLoc = 3;

    public String url = "";
    public String html = "";
    public String referer = null;

    public int BUFFER_SIZE = 65536;

    public safeBrowser() {
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

    public String getHTML(String site) {
        this.followLoc = follow;
        this.hasRedirect = false;
        return this.getPage(site);
    }

    public void getHTTPHTML(String site) {
        this.followLoc = follow;
        this.hasRedirect = false;
        html = this.getPage(site);
    }

    public String getHTML() {
        return this.getHTML(this.url);
    }

    public String getPage(String site) {
        try {

            URL url = new URL(site);
            HttpURLConnection conn = null;

            conn = (HttpURLConnection) url.openConnection();

            conn.setInstanceFollowRedirects(false);
            //connection.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:32.0) Gecko/20100101 Firefox/32.0");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Content-Language", "en-US");
            if (this.specifyCookie == null) {
                conn.setRequestProperty("Cookie", cookies.cookieSTR());
            } else {
                conn.setRequestProperty("Cookie", this.specifyCookie);
            }

            if (referer != null) {
                conn.setRequestProperty("Referer", referer);
            }

            //conn.connect();
            code = conn.getResponseCode();
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
                rhtml.append(decodedString);
            }
            in.close();

            html = rhtml.toString();

            if (code >= 400) {
                throw new IOException("Code >=400!");
            }

            return html;

        } catch (Exception ex) {

            return "";
        }

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
            if (this.specifyCookie == null) {
                connection.setRequestProperty("Cookie", cookies.cookieSTR());
            } else {
                connection.setRequestProperty("Cookie", this.specifyCookie);
            }
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 5.1; rv:32.0) Gecko/20100101 Firefox/32.0");
            connection.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            connection.setRequestProperty("Content-Language", "en-US");

            if (referer != null) {
                connection.setRequestProperty("Referer", referer);
            }

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
                        loc = loctemp;

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

                //this.last_reloc = loc;
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

    public void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection conn = null;

        conn = (HttpURLConnection) url.openConnection();

        conn.setInstanceFollowRedirects(false);
        //connection.setInstanceFollowRedirects(false);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        //conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:32.0) Gecko/20100101 Firefox/32.0");
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Content-Language", "en-US");

        if (this.specifyCookie == null) {
            conn.setRequestProperty("Cookie", cookies.cookieSTR());
        } else {
            conn.setRequestProperty("Cookie", this.specifyCookie);
        }

        if (referer != null) {
            conn.setRequestProperty("Referer", referer);
        }

        int code = conn.getResponseCode();

        // always check HTTP response code first
        if (code == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = conn.getHeaderField("Content-Disposition");
            String contentType = conn.getContentType();
            int contentLength = conn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = conn.getInputStream();

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveDir);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + code);
        }
        conn.disconnect();
    }

    public boolean download(String url, String file) {
        try {
            this.downloadFile(url, file);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}

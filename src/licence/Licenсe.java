package licence;

import base.Helper;
import base.safeBrowser;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Licenсe {

    public String licServer;
    public String licName;
    public String action;
    /**/
    public String file;
    /**/
    private double rand = 1.0;
    private String alphabet = "qwertyuiopasdfghjklzxcvbnm/\\+|=:1234567890_-QWERTYUIOPASDFGHJKLZXCVBNM. ";
    private String newalphabet = "";
    private HashMap<String, String> encMap = new HashMap();
    private HashMap<String, String> decMap = new HashMap();

    /*browser*/
    public boolean isLaggy = false;
    public WebDriver driver = null;
    public String phantomJsPath = null;
    public safeBrowser br = null;

    private boolean killLinuxNag() {
        try {
            String proc1 = Helper.readFromProcess("xdotool search --name win0");
            String[] procList = proc1.split("\r\n");
            for (int i = 0; i < procList.length; i++) {
                String proc = procList[i];
                if (proc.length() < 3) {
                    continue;
                }
                String proc2 = Helper.readFromProcess("xwininfo -id " + proc);
                if (proc2.indexOf("600x167") > 0) {
                    Helper.readFromProcess("xdotool windowunmap " + proc);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static interface User32 extends StdCallLibrary {

        final User32 instance = (User32) Native.loadLibrary("user32", User32.class);

        Integer FindWindowA(String winClass, String title);

        boolean ShowWindow(Integer hWnd, int nCmdShow);

        boolean SetForegroundWindow(Integer hWnd);
    }

    public void killNag() {
        String dir = System.getProperty("user.dir");
        char sep = dir.indexOf("/") >= 0 ? '/' : '\\';
        if (sep == '/') {
            this.killLinuxNag();
        } else {
            Integer hwnd = User32.instance.FindWindowA("SunAwtWindow", null);
            User32.instance.ShowWindow(hwnd, 0);
        }
    }

    private int nextRand(int max) {
        double sin = Math.abs(Math.sin(this.rand));
        int rnd = (int) Math.round(Math.floor(sin * max));
        this.rand = Math.round(sin * 10000);
        return rnd;
    }

    private void generateAlphabet() {
        this.newalphabet = this.alphabet;
        char[] alp1 = this.alphabet.toCharArray();
        char[] alp2 = this.newalphabet.toCharArray();
        for (int i = 0; i < this.alphabet.length(); i++) {
            char c = alp2[i];
            int rnd = this.nextRand(this.newalphabet.length());
            if (i != rnd) {
                alp2[i] = alp2[rnd];
                alp2[rnd] = c;
            }
        }
        for (int i = 0; i < this.alphabet.length(); i++) {
            encMap.put(alp1[i] + "", alp2[i] + "");
            decMap.put(alp2[i] + "", alp1[i] + "");
        }
    }

    private String encode(String str) {
        String res = "";
        for (int i = 0; i < str.length(); i++) {
            char char1 = str.charAt(i);
            String char2 = encMap.get(char1 + "");
            res += char2;
        }
        return res;
    }

    private String decode(String str) {
        String res = "";
        for (int i = 0; i < str.length(); i++) {
            char char1 = str.charAt(i);
            String char2 = decMap.get(char1 + "");
            res += char2;
        }
        return res;
    }

    public void log(String txt) {
        System.out.println(txt);
    }

    public static void strToFile(String file, String str, boolean append) {
        try {
            File newTextFile = new File(file);
            FileWriter fw = new FileWriter(newTextFile, append);
            fw.write(str);
            fw.close();

        } catch (Exception iox) {
            //        iox.printStackTrace();
        }
    }

    public String getBrowser(String url, String data) {
        if (isLaggy) {
            if (this.driver == null) {
                DesiredCapabilities caps = new DesiredCapabilities();
                ArrayList<String> cliArgsCap = new ArrayList<String>();
                cliArgsCap.add("--disk-cache=yes");
                cliArgsCap.add("--max-disk-cache-size=524288");
                cliArgsCap.add("--ignore-ssl-errors=true");
                cliArgsCap.add("--web-security=false");
                cliArgsCap.add("--webdriver-loglevel=NONE");

                //System.out.println(proxy.proxyToStr(pp));
                caps.setJavascriptEnabled(true); // not really needed: JS enabled by default
                caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, this.phantomJsPath);
                caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
                Random rnd = new Random();
                caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", "Mozilla/5.0000000000000000000000 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 Safari/537.36");
                Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
                driver = new PhantomJSDriver(caps);
                driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
                driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
            }

            driver.get(url + "?" + data);
            String res = driver.getPageSource().trim();
            Pattern p = Pattern.compile("dy>(....*?)</");
            Matcher m = p.matcher(res);
            if (m.find()) {
                res = m.group(1).trim();
            }
            return res;
        } else {
            if (this.br == null) {
                br = new safeBrowser();
                br.specifyCookie = "user=me";
            }
            String res = br.excutePost(url, data);
            res = res.trim();
            return res;
        }
    }

    public void licQuery() {
        this.generateAlphabet();
        String url = this.licServer + "/lic/base";
        String postData = "user=" + this.licName + "&act=" + this.action;
        Random rnd = new Random();
        int r0 = rnd.nextInt(100000) + 100000;
        postData += "&r=" + r0;
        postData = Helper.replace(postData, " ", "%20");

        //safeBrowser br = new safeBrowser();
        //br.specifyCookie = "user=me";
        String res = this.getBrowser(url, postData);
        //res = res.trim();

        if (res.equals("LOGIN_NOT_FOUND")) {
            log("Такой пользователь не найден.");
            System.exit(0);
        }

        if (res.equals("LIC_TIME_EXPIRED")) {
            log("Лицензия закончилась.");
            System.exit(0);
        }

        if (res.equals("LIC_BLOCKED")) {
            log("Лицензия заблочена.");
            System.exit(0);
        }

        String kTxt = "Ошибка ключа.";
        if (res.indexOf("OK|") == 0) {
            String data = /*this.decode*/ (res.substring(3));
            int otvet = (r0 * 3) % 1123 + (r0 * 5 + 1) % 10937 + (r0 * 7 + 2) % 42331;
            if (!data.equals("" + otvet)) {
                log(kTxt);
                System.exit(0);
            }

        } else {
            log(kTxt);
            System.exit(0);
        }

        if (this.isLaggy) {
            driver.quit();
        }
    }
}

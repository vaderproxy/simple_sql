package bro;

import base.IniFile;
import form.BROWindow;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;
import licence.Licenсe;
import struct.Config;
import struct.Lang;

public class Bro {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Licenсe lic = new Licenсe();
        lic.licName = "h2";
        lic.action = "os: " + System.getProperty("os.name");
        lic.killNag();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        int datenum = Integer.parseInt(date);
        if (datenum >= 20260115) {
            //System.exit(0);
        }

        System.setProperty("file.encoding", "UTF-8");

        String curDir = System.getProperty("user.dir");
        String sep = "\\";
        if (curDir.indexOf("/") != -1) {
            sep = "/";
        }
        IniFile ifile = null;
        try {
            String iname = "main.ini";
            if (args.length > 0) {
                iname = args[0];
            }
            ifile = new IniFile(curDir + sep + iname);
            Config.language = ifile.getString("main", "lang", "ru");
            String lang_file = curDir + sep + "lang_" + Config.language + ".ini";
            Lang.load(lang_file);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        //lic.licQuery();
        BROWindow form = new BROWindow();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - form.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - form.getHeight()) / 2);
        form.setLocation(x, y);
        form.show();
    }

}

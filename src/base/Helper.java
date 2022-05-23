package base;

import com.google.common.base.Charsets;
import form.BROWindow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.TableModel;
import org.apache.commons.codec.binary.Base64;

public class Helper {

    public static void log(String text) {
        BROWindow.areaComment.append(text + "\r\n");
    }

    public static void log(int text) {
        BROWindow.areaComment.append(text + "\r\n");
    }

    public static void log_inline(String text) {
        BROWindow.areaComment.append(text);
    }

    public static void log_inline(int text) {
        BROWindow.areaComment.append(text + "");
    }

    public static void delay(int d) {
        try {
            Thread.sleep(d);
        } catch (InterruptedException ex) {

        }
    }

    public static int getInt(String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }

    }

    public static String replace(String in, String ths, String that) {
        StringBuilder sb = new StringBuilder(in);
        int idx = sb.indexOf(ths);
        while (idx > -1) {
            sb.replace(idx, idx + ths.length(), that);
            idx = sb.indexOf(ths);
        }

        return sb.toString();

    }

    public static boolean exportToCSV(String pathToExportTo) {
        try {
            TableModel model = BROWindow.dataTable.getModel();
            FileWriter csv = new FileWriter(new File(pathToExportTo));

            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i));
                if (i != model.getColumnCount() - 1) {
                    csv.write(";");
                }
            }

            csv.write("\r\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    csv.write(model.getValueAt(i, j).toString());
                    if (j != model.getColumnCount() - 1) {
                        csv.write(";");
                    }
                }
                csv.write("\r\n");
            }

            csv.close();
            return true;
        } catch (Exception e) {
            ///e.printStackTrace();
        }
        return false;
    }

    public static String readFromProcessWin(String proc) {
        try {
            Process core = Runtime.getRuntime().exec(proc); //pb.start();
            InputStream stream = core.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_16LE));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            core.waitFor();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                stringBuilder.append(line);
                stringBuilder.append("\r\n");
            }
            stream.close();
            return stringBuilder.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String readFromProcess(String proc) {
        try {
            Process core = Runtime.getRuntime().exec(proc); //pb.start();
            InputStream stream = core.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            core.waitFor();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                stringBuilder.append(line);
                stringBuilder.append("\r\n");
            }
            stream.close();
            return stringBuilder.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static HashMap<String, String> parse_query_string(String post) {
        HashMap<String, String> post_map = new HashMap();
        String[] param_list = post.split("&");
        for (int i1 = 0; i1 < param_list.length; i1++) {
            String pv = param_list[i1];
            String[] pv_arr = pv.split("=");
            if (pv_arr.length != 2) {
                continue;
            }
            try {
                if (pv_arr[1].equals("%Inject_Here%")) {
                    post_map.put(pv_arr[0], pv_arr[1]);
                } else {
                    post_map.put(pv_arr[0], java.net.URLDecoder.decode(pv_arr[1], StandardCharsets.UTF_8.name()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // not going to happen - value came from JDK's own StandardCharsets
            }
        }
        return post_map;
    }

    public static String generate_post_query(HashMap<String, String> post_map) {
        String result = "";
        for (Map.Entry<String, String> entry : post_map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String enc = "%Inject_Here%";
            try {
                if (!value.equals(enc)) {
                    enc = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                }
            } catch (Exception ex) {
                //Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (!result.isEmpty()) {
                result += "&";
            }

            result += key + "=" + enc;
        }

        return result;
    }

}

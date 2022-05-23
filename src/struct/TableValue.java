package struct;

import base.*;
import java.util.ArrayList;

public class TableValue {

    public String table;
    public ArrayList<KeyValue> keyvalues = new ArrayList();
    public Integer[] posit;
    /**/
    private UrlKeyValidation ukv = null;

    /*
     public TableValue() {
     int s2 = 0;
     if (!Config.multiUrlMode) {
     if (ReadSql.keys_opt != null) {
     s2 = ReadSql.keys_opt.length;
     }
     posit = new Integer[ReadSql.keys.length + s2];
     for (int i = 0; i < ReadSql.keys.length + s2; i++) {
     posit[i] = -1;
     }
     }
     }
     */

    public TableValue() {
    }

    public TableValue(UrlKeyValidation ukv) {
        int s2 = 0;

        if (ukv.keys_opt != null) {
            s2 = ukv.keys_opt.length;
        }
        posit = new Integer[ukv.keys.length + s2];
        for (int i = 0; i < ukv.keys.length + s2; i++) {
            posit[i] = -1;
        }
        this.ukv = ukv;
    }

    /*
     public void getPosit() {
     for (int i = 0; i < keyvalues.size(); i++) {
     KeyValue kv = keyvalues.get(i);
     int len = this.ukv == null ? ReadSql.keys_all.length : ukv.keys_all.length;
     for (int j = 0; j < len; j++) {
     String key = this.ukv == null ? ReadSql.keys_all[j] : ukv.keys_all[j];
     if (key.equals(kv.key)) {
     posit[i] = j;
     break;
     }

     }
     }
     }
     */

    public void sort() {
        for (int i = 0; i < keyvalues.size(); i++) {
            if (posit[i] == i) {
                continue;
            }

            if (posit[i] == -1) {
                break;
            }

            for (int j = i + 1; j < keyvalues.size(); j++) {
                if (posit[j] == -1) {
                    break;
                }

                if (i == posit[j]) {
                    KeyValue kv = keyvalues.get(j);
                    keyvalues.set(j, keyvalues.get(i));
                    keyvalues.set(i, kv);
                    posit[j] = posit[i];
                    posit[i] = i;
                    break;
                }
            }

        }
    }
}

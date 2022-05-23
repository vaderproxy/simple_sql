package struct;

public class SqlTest {

    public String url;
    public String param = "";
    public String paramName = "";
    public int type = 0;
    public int columns = 0;
    public String cookiestr = "";
    public String poststr = "";

    @Override
    public String toString() {
        return paramName + "=" + param;
    }

}

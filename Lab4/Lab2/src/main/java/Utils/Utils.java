package Utils;

public class Utils {
    private static String muleUrl;
    public static String getMuleUrl(){
        if (muleUrl == null){
            //TODO: add read file
            muleUrl = "http://localhost:8987/";
        }
        return muleUrl;
    }
}

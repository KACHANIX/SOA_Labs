package Utils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
//
    public static String getDBString(){
        return "jdbc:postgresql://192.168.10.99:5432/studs";
    }

    public static String getDBUser(){
        return "s242274";
    }
    public static String getDBUserPassword(){
        return "bld868";
    }

    public static ArrayList<String> fields = new ArrayList<String>(Arrays.asList("id", "name", "turnover", "x", "y", "street", "type", "date"));
    public static ArrayList<String> sortBys = new ArrayList<String>(Arrays.asList("ASC", "DESC"));

//    public static String getDBString() {
//        return "jdbc:postgresql://localhost:5432/ifmo";
//    }
//
//    public static String getDBUser() {
//        return "postgres";
//    }
//
//    public static String getDBUserPassword() {
//        return "1";
//    }

    public static Map<String, String> splitQuery(String query) throws Exception {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static Map<String, String> splitQueryWithoutDecoding(String query) throws Exception {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), pair.substring(idx + 1));
        }
        return query_pairs;
    }

    public static boolean checkNecessaryPostParams(Map<String, String> paramsMap) {
        if (paramsMap.get("name") == null || paramsMap.get("name").isEmpty()) {
            return false;
        }
        if (!tryParseLong(paramsMap.get("y"))) {
            return false;
        }

        if (!tryParseDouble(paramsMap.get("turnover"))) {
            return false;
        }

        return !(Double.parseDouble(paramsMap.get("turnover")) <= 0);
    }

    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean tryParseDouble(String value) {
        try {
            Double dbl = Double.parseDouble(value);
            if (dbl == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean tryParseShort(String value) {
        try {
            Short.parseShort(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

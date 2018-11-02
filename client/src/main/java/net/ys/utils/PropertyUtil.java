package net.ys.utils;

import java.util.*;

/**
 * User: NMY
 * Date: 17-5-11
 */
public class PropertyUtil {

    static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(PropertyUtil.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError("load properties error!");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static List<String> gets(String prefixKey) {
        List<String> values = new ArrayList<String>();
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        String key;
        for (Map.Entry<Object, Object> entry : entries) {
            key = String.valueOf(entry.getKey());
            if (key.startsWith(prefixKey)) {
                values.add(String.valueOf(entry.getValue()));
            }
        }
        return values;
    }
}

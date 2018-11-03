package net.ys.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

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
        Enumeration<String> names = (Enumeration<String>) properties.propertyNames();
        String name;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (name.startsWith(prefixKey)) {
                values.add(properties.getProperty(name));
            }
        }
        return values;
    }
}


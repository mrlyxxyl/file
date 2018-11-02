package net.ys.utils;

import java.io.*;

/**
 * User: LiWenC
 * Date: 16-9-8
 */
public class TimeUtil {

    static String path;

    static {
        path = TimeUtil.class.getClassLoader().getResource("time.properties").getPath();
    }

    public static long queryLastScanTime() {
        try {
            FileInputStream inputStream = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String time = reader.readLine();
            reader.close();
            return Long.parseLong(time);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return 0L;
    }

    public static void flushLastScanTime(long time) {
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(String.valueOf(time));
            writer.close();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }
}

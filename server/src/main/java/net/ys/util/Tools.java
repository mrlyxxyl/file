package net.ys.util;

import net.ys.constant.X;

import java.security.MessageDigest;
import java.text.DecimalFormat;

/**
 * User: LiWenC
 * Date: 16-9-8
 */
public class Tools {

    public static String KEY_PREFIX = "88_";

    /**
     * MD5加密
     *
     * @param key
     * @return
     */
    public static String genMD5(String key) {
        try {
            if (key == null || "".equals(key.trim())) {
                return "";
            }
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest((KEY_PREFIX + key).getBytes(X.ENCODING.U));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; i++) {
                sb.append(Character.forDigit((bs[i] >>> 4) & 0x0F, 16)).append(Character.forDigit(bs[i] & 0x0F, 16));
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 字节
     * @return
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String result;
        if (fileSize <= 0) {
            result = "0B";
        } else if (fileSize < 1024) {
            result = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            result = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            result = df.format((double) fileSize / 1048576) + "MB";
        } else {
            result = df.format((double) fileSize / 1073741824) + "GB";
        }
        return result;
    }
}

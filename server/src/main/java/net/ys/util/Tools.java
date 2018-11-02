package net.ys.util;

import java.text.DecimalFormat;

/**
 * User: LiWenC
 * Date: 16-9-8
 */
public class Tools {

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

package net.ys.utils;

import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

/**
 * User: LiWenC
 * Date: 16-9-8
 */
public class Tools {

    private static Random rand;
    public static String KEY_PREFIX = "88_";

    static {
        rand = new Random();
    }

    /**
     * 判断多个字符串是否为空
     *
     * @param strings
     * @return
     */
    public static boolean isNotEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (String str : strings) {
            if (str == null || "".equals(str.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取随机数字
     *
     * @return
     */
    public static int randomInt() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++) {
            if (i == 0 && array[i] == 0) {
                array[i] = 1;
            }
            result = result * 10 + array[i];
        }
        return result;
    }

    /**
     * 获取指定长度随机字符串
     *
     * @param len
     * @return
     */
    public static String randomString(int len) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, len);
    }

    /**
     * 获取随机数字
     *
     * @return
     */
    public static int randomInt(int num) {
        return rand.nextInt(num);
    }

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
            byte[] bs = md5.digest((KEY_PREFIX + key).getBytes("UTF-8"));
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
     * 生成上传图片的名称
     *
     * @return
     */
    public static String genFileName() {
        return System.currentTimeMillis() + "_" + rand.nextInt(100);
    }


    /**
     * 获取文件路径
     *
     * @return
     */
    public static int randomPath() {
        return rand.nextInt(100) + 100;
    }

    /**
     * 生成问号字符串
     *
     * @param size
     * @return
     */
    public static String genMark(int size) {
        StringBuffer sb = new StringBuffer("?");
        for (int i = 1; i < size; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }
}

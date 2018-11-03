package net.ys.service;

import java.io.InputStream;

/**
 * User: NMY
 * Date: 18-11-3
 */
public interface FileOperate {

    boolean full(InputStream inputStream, String path, String fileName);

    boolean split(InputStream inputStream, String path, String fileName, long fileLen, long startPoint);

    boolean info(String filePath, String fileName, long fileSize, String clientIpAddress);

    InputStream download(String root, String key);
}

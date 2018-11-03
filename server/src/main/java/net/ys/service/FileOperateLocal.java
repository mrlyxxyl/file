package net.ys.service;

import net.ys.util.LogUtil;
import net.ys.util.PropertyUtil;
import org.apache.commons.io.FileUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;

//@Service(value = "fileOperate")
public class FileOperateLocal implements FileOperate {

    @Resource
    private FileService fileService;

    String uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = PropertyUtil.get("upload_path");
    }

    @Override
    public boolean full(InputStream inputStream, String path, String fileName) {
        try {
            String targetPath = uploadPath + path;
            File desPath = new File(targetPath);
            if (!desPath.exists()) {
                desPath.mkdirs();
            }
            FileUtils.copyInputStreamToFile(inputStream, new File(targetPath + "/" + fileName));
            return true;
        } catch (IOException e) {
            LogUtil.error(e);
        }
        return false;
    }

    @Override
    public boolean split(InputStream is, String path, String fileName, long fileLen, long startPoint) {
        try {
            String targetPath = uploadPath + path;
            File desPath = new File(targetPath);
            if (!desPath.exists()) {
                desPath.mkdirs();
            }
            String targetFile = targetPath + fileName;
            RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
            if (!new File(targetFile).exists()) {
                randomAccessFile.setLength(fileLen);
            }
            randomAccessFile.seek(startPoint);
            int len;
            byte[] bytes = new byte[2048];
            while ((len = is.read(bytes)) > 0) {
                randomAccessFile.write(bytes, 0, len);
            }
            randomAccessFile.close();

            return true;
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return false;
    }

    @Override
    public boolean info(String filePath, String fileName, long fileSize, String clientIpAddress) {
        return fileService.addFiles(uploadPath, filePath, fileName, fileSize, clientIpAddress);
    }

    @Override
    public InputStream download(String root, String key, File tempFile) {
        try {
            return new FileInputStream(root + key);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}

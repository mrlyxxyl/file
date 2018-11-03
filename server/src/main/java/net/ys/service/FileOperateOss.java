package net.ys.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import net.ys.util.LogUtil;
import net.ys.util.PropertyUtil;
import net.ys.util.Tools;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

//@Service(value = "fileOperate")
public class FileOperateOss implements FileOperate {

    @Resource
    private FileService fileService;

    String ossEndpoint;

    String ossAccessKeyId;

    String ossAccessKeySecret;

    String ossBucketName;

    OSSClient ossClient;

    @PostConstruct
    protected void init() {

        ossEndpoint = PropertyUtil.get("oss.endpoint");

        ossAccessKeyId = PropertyUtil.get("oss.accessKeyId");

        ossAccessKeySecret = PropertyUtil.get("oss.accessKeySecret");

        ossBucketName = PropertyUtil.get("oss.bucketName");

        ossClient = new OSSClient(ossEndpoint, ossAccessKeyId, ossAccessKeySecret);
    }

    @PreDestroy
    protected void close() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    @Override
    public boolean full(InputStream inputStream, String path, String fileName) {
        try {
            ossClient.putObject(ossBucketName, Tools.genMD5(path + "/" + fileName), inputStream);
            return true;
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return false;
    }

    @Override
    public boolean split(InputStream inputStream, String path, String fileName, long fileLen, long startPoint) {
        return false;
    }

    @Override
    public boolean info(String filePath, String fileName, long fileSize, String clientIpAddress) {
        return fileService.addFiles(ossBucketName, filePath, fileName, fileSize, clientIpAddress);
    }

    @Override
    public InputStream download(String container, String key, File tempFile) {
        try {
            ossClient.getObject(new GetObjectRequest(container, Tools.genMD5(key)), tempFile);
            return new FileInputStream(tempFile);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return null;
    }
}

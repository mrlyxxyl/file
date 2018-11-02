package net.ys.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.net.URLEncoder;

public class FileUploadUtil {

    static CloseableHttpClient httpClient;

    static {
        httpClient = HttpClients.createDefault();
    }

    /**
     * 上传整个文件
     */
    public static boolean uploadFull(String uploadUrl, File file, String filePathStr) {
        boolean flag = false;
        CloseableHttpResponse response = null;
        HttpPost httppost = null;
        try {
            httppost = new HttpPost(uploadUrl);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();
            httppost.setConfig(requestConfig);
            FileBody bin = new FileBody(file);
            StringBody filePath = new StringBody(URLEncoder.encode(filePathStr, "UTF-8"), ContentType.TEXT_PLAIN);
            StringBody fileName = new StringBody(URLEncoder.encode(file.getName(), "UTF-8"), ContentType.TEXT_PLAIN);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", bin);
            builder.addPart("filePath", filePath);
            builder.addPart("fileName", fileName);

            HttpEntity reqEntity = builder.build();
            httppost.setEntity(reqEntity);
            response = httpClient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(entity);
                    flag = "true".equals(result);
                }
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httppost != null) {
                    httppost.releaseConnection();
                }
            } catch (Exception e) {
            }
        }
        return flag;
    }

    /**
     * 切分文件上传
     */
    public static boolean uploadSplit(String uploadUrl, String fileNameStr, File file, String filePathStr) {
        boolean flag = false;
        CloseableHttpResponse response = null;
        HttpPost httppost = null;
        try {
            httppost = new HttpPost(uploadUrl);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();
            httppost.setConfig(requestConfig);
            FileBody bin = new FileBody(file);
            StringBody filePath = new StringBody(URLEncoder.encode(filePathStr, "UTF-8"), ContentType.TEXT_PLAIN);
            StringBody fileName = new StringBody(URLEncoder.encode(fileNameStr, "UTF-8"), ContentType.TEXT_PLAIN);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", bin);
            builder.addPart("filePath", filePath);
            builder.addPart("fileName", fileName);
            HttpEntity reqEntity = builder.build();
            httppost.setEntity(reqEntity);
            response = httpClient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(entity);
                    flag = "true".equals(result);
                }
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httppost != null) {
                    httppost.releaseConnection();
                }
            } catch (Exception e) {
            }
        }
        return flag;
    }

    /**
     * 上传文件信息
     */
    public static boolean uploadFileInfo(String uploadUrl, String filePathStr, String fileNameStr, long fileLength, String clientIpAddressStr) {
        boolean flag = false;
        CloseableHttpResponse response = null;
        HttpPost httppost = null;
        try {
            httppost = new HttpPost(uploadUrl);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();
            httppost.setConfig(requestConfig);
            StringBody filePath = new StringBody(URLEncoder.encode(filePathStr, "UTF-8"), ContentType.TEXT_PLAIN);
            StringBody fileName = new StringBody(URLEncoder.encode(fileNameStr, "UTF-8"), ContentType.TEXT_PLAIN);
            StringBody fileSize = new StringBody(fileLength + "", ContentType.TEXT_PLAIN);
            StringBody clientIpAddress = new StringBody(clientIpAddressStr, ContentType.TEXT_PLAIN);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("filePath", filePath);
            builder.addPart("fileName", fileName);
            builder.addPart("fileSize", fileSize);
            builder.addPart("clientIpAddress", clientIpAddress);
            HttpEntity reqEntity = builder.build();
            httppost.setEntity(reqEntity);
            response = httpClient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(entity);
                    flag = "true".equals(result);
                }
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httppost != null) {
                    httppost.releaseConnection();
                }
            } catch (Exception e) {
            }
        }
        return flag;
    }
}
package net.ys.service;

import net.ys.util.LogUtil;
import net.ys.util.PropertyUtil;
import net.ys.util.Tools;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//@Service(value = "fileOperate")
public class FileOperateSwift implements FileOperate {

    @Resource
    private FileService fileService;

    String swiftUrl;

    String swiftUser;

    String swiftPassword;

    String swiftContainer;

    final static String SEPARATOR = "/";

    CloseableHttpClient httpClient;

    @PostConstruct
    protected void init() {
        httpClient = HttpClients.createDefault();

        swiftUrl = PropertyUtil.get("swift.url");

        swiftUser = PropertyUtil.get("swift.user");

        swiftPassword = PropertyUtil.get("swift.password");

        swiftContainer = PropertyUtil.get("swift.container");

    }

    @PreDestroy
    protected void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                LogUtil.error(e);
            }
        }
    }

    @Override
    public boolean full(InputStream inputStream, String path, String fileName) {
        CloseableHttpResponse response = null;
        HttpPut httpPut = null;
        try {
            Map<String, Header> map = genUrlAndToken();
            Header storageUrl = map.get("storageUrl");
            Header authToken = map.get("authToken");
            if (storageUrl == null || authToken == null) {
                return false;
            }

            String url = storageUrl.getValue() + SEPARATOR + swiftContainer + SEPARATOR + Tools.genMD5(path + SEPARATOR + fileName);
            httpPut = new HttpPut(url);
            InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream);
            httpPut.setEntity(inputStreamEntity);
            httpPut.setHeader(authToken);
            response = httpClient.execute(httpPut);
            return response.getStatusLine().getStatusCode() < 300;

        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpPut != null) {
                    httpPut.releaseConnection();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public boolean split(InputStream inputStream, String path, String fileName, long fileLen, long startPoint) {
        return false;
    }

    @Override
    public boolean info(String filePath, String fileName, long fileSize, String clientIpAddress) {
        return fileService.addFiles(swiftContainer, filePath, fileName, fileSize, clientIpAddress);
    }

    @Override
    public InputStream download(String container, String key) {
        HttpGet httpget = null;
        CloseableHttpResponse response;
        try {
            Map<String, Header> map = genUrlAndToken();
            Header storageUrl = map.get("storageUrl");
            Header authToken = map.get("authToken");
            if (storageUrl == null || authToken == null) {
                return null;
            }
            String url = storageUrl.getValue() + SEPARATOR + container + SEPARATOR + Tools.genMD5(key);
            httpget = new HttpGet(url);
            httpget.addHeader(authToken);
            response = httpClient.execute(httpget);
            int code = response.getStatusLine().getStatusCode();
            if (code < 300) {
                return response.getEntity().getContent();
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (httpget != null) {
                    httpget.releaseConnection();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 获得地址和Token信息
     *
     * @throws IOException
     */
    public Map<String, Header> genUrlAndToken() throws IOException {
        Map<String, Header> map = new HashMap<String, Header>();
        HttpGet req;
        req = new HttpGet(swiftUrl);
        req.addHeader("X-Storage-User", swiftUser);
        req.addHeader("X-Storage-Pass", swiftPassword);
        HttpResponse rsp = httpClient.execute(req);
        Header storageUrl = rsp.getFirstHeader("X-Storage-Url");
        Header authToken = rsp.getFirstHeader("X-Auth-Token");
        map.put("storageUrl", storageUrl);
        map.put("authToken", authToken);
        req.releaseConnection();
        return map;
    }
}

package net.ys.service;

import net.ys.utils.FileUploadUtil;
import net.ys.utils.LogUtil;
import net.ys.utils.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 定时任务
 */
@Service
public class FileService {

    static long perLen;//每个分片文件大小

    static int threadNum;//线程数量

    static final String SP = "__";

    String attachmentTmp;

    List<String> paths;

    String fullUploadUrl;

    String splitUploadUrl;

    String fileInfoUrl;

    String clientIpAddress;

    @PostConstruct
    public void init() {
        fullUploadUrl = PropertyUtil.get("full_upload_url");
        splitUploadUrl = PropertyUtil.get("split_upload_url");
        paths = PropertyUtil.gets("attachment_path");
        attachmentTmp = PropertyUtil.get("temp_attachment_path");
        fileInfoUrl = PropertyUtil.get("file_info_url");
        clientIpAddress = PropertyUtil.get("client_ip_address");
        threadNum = Integer.parseInt(PropertyUtil.get("thread_num"));
        perLen = Long.parseLong(PropertyUtil.get("per_len")) * 1024 * 1024;
    }

    public void initUpload() {
        LogUtil.debug("initUpload--start");

        long lastScanTime = 867686400000L;
        long now = System.currentTimeMillis();

        List<FutureTask<Integer>> futureTasks = new ArrayList<FutureTask<Integer>>();//进行异步任务列表
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);//线程池 初始化三十个线程 和JDBC连接池是一个意思 实现重用
        Callable<Integer> callable;
        List<File> files;
        for (String path : paths) {
            files = scanFile(path, lastScanTime, now);

            if (files.size() > 0) {
                for (File file : files) {
                    callable = new TransCallable<Integer>(file, lastScanTime);
                    FutureTask<Integer> futureTask = new FutureTask<Integer>(callable);
                    futureTasks.add(futureTask);
                    executorService.submit(futureTask);
                }

                try {
                    for (FutureTask<Integer> futureTask : futureTasks) {
                        futureTask.get();
                    }
                } catch (Exception e) {
                    LogUtil.error(e);
                }
            }
        }
        LogUtil.debug("initUpload--end");
    }

    public void upload() {
        LogUtil.debug("upload--start");

        long lastScanTime = 867686400000L;
        long now = System.currentTimeMillis();

        List<File> files;
        boolean flag;
        for (String path : paths) {
            files = scanFile(path, lastScanTime, now);

            if (files.size() > 0) {
                for (File file : files) {
                    flag = upload(file);
                    if (flag) {
                        file.setLastModified(lastScanTime);
                    } else {
                        return;
                    }
                }
            }
        }
        LogUtil.debug("upload--end");
    }

    /**
     * 扫描文件
     */
    public List<File> scanFile(String attachment, final long lastScanTime, final long now) {
        try {
            Collection<File> files = FileUtils.listFiles(new File(attachment), new IOFileFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return false;
                }

                @Override
                public boolean accept(File file) {
                    long fileTime = file.lastModified();
                    if (fileTime > lastScanTime && fileTime < now) {
                        return true;
                    }
                    return false;
                }
            }, TrueFileFilter.INSTANCE);
            return new ArrayList<File>(files);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        return new ArrayList<File>();
    }

    private boolean upload(File file) {
        boolean flag = false;
        try {
            long fileLen = file.length();

            String filePath = file.getParentFile().getAbsolutePath().replaceAll("\\\\", "/");
            if (filePath.contains(":")) {
                filePath = filePath.substring(2);
            }

            if (fileLen <= perLen) {//直接上传
                flag = fullUpload(file, filePath);
            } else {//切分上传
                flag = splitUpload(file, filePath);
            }

            if (flag) { //调用接口上传数据
                flag = FileUploadUtil.uploadFileInfo(fileInfoUrl, filePath, file.getName(), file.length(), clientIpAddress);
            }
        } catch (Exception e) {
            LogUtil.error(e);
        }

        return flag;
    }

    /**
     * 直接上传文件
     *
     * @param file
     */
    private boolean fullUpload(File file, String filePath) {
        return FileUploadUtil.uploadFull(fullUploadUrl, file, filePath);
    }

    /**
     * 切分上传文件
     *
     * @param file
     */
    private boolean splitUpload(File file, String filePath) {
        try {
            List<File> files = split(file);
            return uploadFileStep(splitUploadUrl, file.getName(), files, filePath);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        return false;
    }

    private List<File> split(File file) throws IOException {

        long fileLen = file.length();
        int fileNum = (int) (fileLen / perLen + (fileLen % perLen == 0 ? 0 : 1));//切分文件个数

        FileOutputStream fos;
        RandomAccessFile fis = new RandomAccessFile(file, "rw");
        FileChannel fileChannel = fis.getChannel();
        FileLock flin = fileChannel.tryLock();

        int page;
        long readSize;
        long startPoint;
        BigDecimal per = new BigDecimal(perLen + "");
        List<File> files = new ArrayList<File>();
        File f;
        for (page = 0; page < fileNum; page++) {
            readSize = 0;
            startPoint = per.multiply(new BigDecimal(page)).longValue();
            f = new File(attachmentTmp + "/" + UUID.randomUUID().toString() + SP + fileLen + SP + startPoint);
            fos = new FileOutputStream(f);
            fis.seek(startPoint);
            int len;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) > 0) {
                fos.write(bytes, 0, len);
                readSize += len;
                if (readSize == perLen) {
                    fos.flush();
                    break;
                }
            }
            fos.close();
            files.add(f);
        }

        flin.release();
        fileChannel.close();
        fis.close();

        return files;
    }

    private boolean uploadFileStep(String splitUploadUrl, String fileName, List<File> files, String filePath) throws IOException {
        boolean flag = true;
        for (File file : files) {
            flag = FileUploadUtil.uploadSplit(splitUploadUrl, fileName, file, filePath);
            if (!flag) {
                flag = false;
                break;
            } else {
                file.delete();
            }
        }

        return flag;
    }

    class TransCallable<T> implements Callable<T> {

        private File file;

        private long lastScanTime;

        TransCallable(File file, long lastScanTime) {
            this.lastScanTime = lastScanTime;
            this.file = file;
        }

        @Override
        public T call() throws Exception {
            try {
                LogUtil.debug(Thread.currentThread().getName());
                boolean flag = upload(file);
                if (flag) {
                    file.setLastModified(lastScanTime);
                }
            } catch (Exception e) {
                LogUtil.error(e);
            }
            return null;
        }
    }
}

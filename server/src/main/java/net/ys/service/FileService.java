package net.ys.service;

import net.ys.bean.SysFile;
import net.ys.dao.FileDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FileService {

    @Resource
    private FileDao fileDao;

    public long queryFileCount(String fileName) {
        return fileDao.queryFileCount(fileName);
    }

    public List<SysFile> queryFiles(String fileName, int page, int pageSize) {
        return fileDao.queryFiles(fileName, page, pageSize);
    }

    public boolean addFiles(String storageRootPath, String filePath, String fileName, long fileSize, String clientIpAddress) {
        return fileDao.addFiles(storageRootPath, filePath, fileName, fileSize, clientIpAddress);
    }

    public SysFile queryFile(String id) {
        return fileDao.queryFile(id);
    }
}

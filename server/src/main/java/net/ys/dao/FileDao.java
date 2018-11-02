package net.ys.dao;

import net.ys.bean.SysFile;
import net.ys.mapper.SysFileMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class FileDao {

    public long queryFileCount(String fileName) {
        String sql = "SELECT COUNT(*) FROM sys_file ";

        if (StringUtils.isNotBlank(fileName)) {
            sql += " WHERE `file_name` LIKE ?";
            return jdbcTemplate.queryForObject(sql, Long.class, "%" + fileName + "%");
        } else {
            return jdbcTemplate.queryForObject(sql, Long.class);
        }
    }

    public List<SysFile> queryFiles(String fileName, int page, int pageSize) {
        String sql = "SELECT id, client_ip_address, storage_root_path, file_path, file_name, file_size, file_status, create_time FROM sys_file ";

        if (StringUtils.isNotBlank(fileName)) {
            sql += " WHERE `file_name` LIKE ? ORDER BY id LIMIT ?,?";
            return jdbcTemplate.query(sql, new SysFileMapper(), "%" + fileName + "%", (page - 1) * pageSize, pageSize);
        } else {
            sql += " ORDER BY id LIMIT ?,?";
            return jdbcTemplate.query(sql, new SysFileMapper(), (page - 1) * pageSize, pageSize);
        }
    }

    @Resource
    private JdbcTemplate jdbcTemplate;

    public SysFile queryFile(String id) {
        String sql = "SELECT id, client_ip_address, storage_root_path, file_path, file_name, `file_size`, file_status, create_time FROM sys_file WHERE id = ?";
        List<SysFile> list = jdbcTemplate.query(sql, new SysFileMapper(), id);
        if (list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public boolean addFiles(String storageRootPath, String filePath, String fileName, long fileSize, String clientIpAddress) {
        String sql = "INSERT INTO sys_file (storage_root_path, file_path, file_name, file_size, create_time, client_ip_address ) VALUES (?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, storageRootPath, filePath, fileName, fileSize, System.currentTimeMillis(), clientIpAddress) > 0;
    }
}

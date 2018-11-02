package net.ys.mapper;

import net.ys.bean.SysFile;
import net.ys.util.Tools;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SysFileMapper implements RowMapper<SysFile> {
    @Override
    public SysFile mapRow(ResultSet resultSet, int i) throws SQLException {
        SysFile sysFile = new SysFile();
        sysFile.setId(resultSet.getLong("id"));
        sysFile.setClientIpAddress(resultSet.getString("client_ip_address"));
        sysFile.setStorageRootPath(resultSet.getString("storage_root_path"));
        sysFile.setFilePath(resultSet.getString("file_path"));
        sysFile.setFileName(resultSet.getString("file_name"));
        sysFile.setFileSize(Tools.formatFileSize(resultSet.getLong("file_size")));
        sysFile.setFileStatus(resultSet.getInt("file_status"));
        sysFile.setCreateTime(resultSet.getLong("create_time"));
        return sysFile;
    }
}
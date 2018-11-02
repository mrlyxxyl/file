package net.ys.controller;

import net.ys.bean.SysFile;
import net.ys.service.FileService;
import net.ys.util.LogUtil;
import net.ys.util.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "file")
public class FileController {

    @Resource
    private FileService fileService;

    public static String uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = PropertyUtil.get("upload_path");
    }

    @RequestMapping(value = "list")
    public ModelAndView list(@RequestParam(defaultValue = "") String fileName, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        ModelAndView modelAndView = new ModelAndView("file/list");

        if (page < 1) {
            page = 1;
        }
        long count = fileService.queryFileCount(fileName);

        long t = count / pageSize;
        int k = count % pageSize == 0 ? 0 : 1;
        int totalPage = (int) (t + k);

        if (page > totalPage && count > 0) {
            page = totalPage;
        }

        List<SysFile> files;
        if ((page - 1) * pageSize < count) {
            files = fileService.queryFiles(fileName, page, pageSize);
        } else {
            files = new ArrayList<SysFile>();
        }

        modelAndView.addObject("count", count);
        modelAndView.addObject("currPage", page);
        modelAndView.addObject("totalPage", totalPage);
        modelAndView.addObject("files", files);
        modelAndView.addObject("fileName", fileName);
        return modelAndView;
    }

    @RequestMapping(value = "full", method = RequestMethod.POST)
    @ResponseBody
    public String fullUpload(@RequestParam(required = true, defaultValue = "") String filePath,
                             @RequestParam(required = true) MultipartFile file) {
        try {
            filePath = URLDecoder.decode(filePath, "UTF-8");//文件路径
            String fileName = file.getOriginalFilename();//文件名称
            String targetPath = uploadPath + filePath + "/";
            File desPath = new File(targetPath);
            if (!desPath.exists()) {
                desPath.mkdirs();
            }
            File targetFile = new File(targetPath + fileName);
            FileUtils.copyInputStreamToFile(file.getInputStream(), targetFile);
            return "true";
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return "false";
    }

    @RequestMapping(value = "split", method = RequestMethod.POST)
    @ResponseBody
    public String splitUpload(@RequestParam(required = true, defaultValue = "") String filePath,
                              @RequestParam(required = true, defaultValue = "") String fileName,
                              @RequestParam(required = true) MultipartFile file) {
        try {
            filePath = URLDecoder.decode(filePath, "UTF-8");//文件路径
            fileName = URLDecoder.decode(fileName, "UTF-8");//文件名称

            String[] temps = file.getOriginalFilename().split("__");
            long fileLen = Long.parseLong(temps[1]);
            long startPoint = Long.parseLong(temps[2]);

            String targetPath = uploadPath + filePath + "/";
            File desPath = new File(targetPath);
            if (!desPath.exists()) {
                desPath.mkdirs();
            }
            InputStream is = file.getInputStream();
            String targetFile = targetPath + fileName;
            RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
            if (!new File(targetFile).exists()) {
                randomAccessFile.setLength(fileLen);
            }
            randomAccessFile.seek(startPoint);
            int len;
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) > 0) {
                randomAccessFile.write(bytes, 0, len);
            }
            randomAccessFile.close();
            return "true";
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return "false";
    }

    @RequestMapping(value = "info", method = RequestMethod.POST)
    @ResponseBody
    public String uploadData(@RequestParam(required = true, defaultValue = "") String filePath,
                             @RequestParam(required = true, defaultValue = "") String fileName,
                             @RequestParam(required = true, defaultValue = "") String clientIpAddress,
                             @RequestParam(required = true, defaultValue = "0") long fileSize) {
        try {
            filePath = URLDecoder.decode(filePath, "UTF-8");//文件路径
            fileName = URLDecoder.decode(fileName, "UTF-8");//文件名称
            boolean flag = fileService.addFiles(uploadPath, filePath, fileName, fileSize, clientIpAddress);
            if (flag) {
                return "true";
            }
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return "false";
    }

    @RequestMapping("download")
    public void download(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {

        SysFile sysFile = fileService.queryFile(id);

        if (sysFile == null) {
            return;
        }

        String key = sysFile.getStorageRootPath() + sysFile.getFilePath() + "/" + sysFile.getFileName();

        String fileName = sysFile.getFileName();
        String agent = request.getHeader("USER-AGENT").toLowerCase();
        if (agent.indexOf("firefox") > -1) {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        } else {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        }

        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        InputStream is = new FileInputStream(key);
        ServletOutputStream out = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = is.read(bytes)) > 0) {
            out.write(bytes, 0, len);
            out.flush();
        }
        out.close();
        is.close();
    }
}

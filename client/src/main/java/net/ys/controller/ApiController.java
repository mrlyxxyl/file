package net.ys.controller;

import net.ys.constant.GenResult;
import net.ys.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 客户端api接口
 */
@Controller
@RequestMapping(value = "api", produces = {"application/json;charset=utf-8"})
public class ApiController {

    @Resource
    private FileService fileService;

    @RequestMapping(value = "upload", headers = "Accept=application/json")
    @ResponseBody
    public Map<String, Object> multiplyPath() throws IOException {
        long start = System.currentTimeMillis();
        fileService.upload();
        return GenResult.SUCCESS.genResult(System.currentTimeMillis() - start);
    }
}

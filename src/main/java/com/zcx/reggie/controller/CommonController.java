package com.zcx.reggie.controller;

import com.zcx.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用模块
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file 该变量名需要与前端的名字保持一致
     * @return 返回上传结果
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file是一个临时文件，需要转存到指定位置，本次请求完成后临时文件就会删除
        log.info(file.toString());

        // 获取原始文件后缀
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        // 判断uploadPath目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()) dir.mkdirs();

        // 转存临时文件
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param response 获取输出流
     * @param name 文件名称
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) {
        try (FileInputStream is = new FileInputStream(new File(basePath + name)); ServletOutputStream os = response.getOutputStream()) {
            // 设置输出流的文件类型为图片文件
            response.setContentType("image/jpeg");

            // 通过输入流读取文件内容，输出流将文件写回浏览器，在浏览器展示图片
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

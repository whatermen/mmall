package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.FileService;
import com.mmall.util.FtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件Service的实现类
 * <p>
 * @Author LeifChen
 * @Date 2019-02-28
 */
@Slf4j
@Service("fileService")
public class FileServiceImpl implements FileService {

    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        log.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            if (!fileDir.setWritable(true)) {
                log.error("无法获取路径:{}的写入权限", fileDir);
            }
            if (!fileDir.mkdirs()) {
                log.error("文件:{}创建失败", fileDir);
            }
        }
        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            // 文件已经上传成功
            FtpUtils.uploadFile(Lists.newArrayList(targetFile));
            // 文件已经上传到FTP服务器上
            if (!targetFile.delete()) {
                log.error("临时文件删除失败");
            }
        } catch (IOException e) {
            log.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}

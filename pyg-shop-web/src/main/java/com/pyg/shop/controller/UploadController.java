package com.pyg.shop.controller;

import com.pyg.utils.PygResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${uploadServer}")
    private String uploadServer;

    @RequestMapping("/uploadFile")
    public PygResult uploadFile(MultipartFile file) {
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("D:\\Online mall\\pyg-parent\\pyg-shop-web\\src\\main\\resources\\config\\fdfs_client.conf");

            String filename = file.getOriginalFilename();
            String extName = filename.substring(filename.indexOf(".")+1);
            String fileUrl = fastDFSClient.uploadFile(file.getBytes(),extName);
            return new PygResult(true,uploadServer+fileUrl);
        } catch (Exception e){
            e.printStackTrace();
            return new PygResult(false,"上传失败");
        }
    }
}

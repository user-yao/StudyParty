package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.Image;
import com.studyParty.entity.group.GroupTask;
import com.studyparty.group.Utils.FileUploadUtil;
import com.studyparty.group.mapper.GroupTaskMapper;
import com.studyparty.group.mapper.ImageMapper;
import com.studyparty.group.services.GroupTaskServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service

public class GroupTaskServerImpl extends ServiceImpl<GroupTaskMapper, GroupTask> implements GroupTaskServer {
    @Autowired
    private ImageMapper imageMapper;
    @Transactional
    public String uploadImage(MultipartFile file, Long groupTaskId) {
        // 调用工具类上传文件
        String url = FileUploadUtil.uploadImage(file);

        // 保存图片记录到数据库
        Image image = new Image();
        image.setGroupTaskId(groupTaskId);
        image.setImage(file.getOriginalFilename());
        image.setUrl(url);
        imageMapper.insert(image);
        return url;
    }
    @Transactional
    public boolean deleteImage(String url) {
        // 从数据库查询图片
        Image image = imageRepository.findByUrl(url);
        if (image == null) {
            return false;
        }

        // 删除文件
        boolean deleted = FileUploadUtil.deleteFile(url);

        // 删除数据库记录
        if (deleted) {
            imageRepository.delete(image);
            return true;
        }

        return false;
    }
}

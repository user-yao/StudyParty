package com.studyparty.group.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.studyParty.entity.Image;
import com.studyparty.group.Utils.FileUploadUtil;
import com.studyparty.group.mapper.ImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
public class ImageServer {
    @Autowired
    private ImageMapper imageMapper;

    @Transactional
    public void uploadImage(MultipartFile[] file, Long groupTaskId) {
        for (MultipartFile multipartFile : file){
            // 调用工具类上传文件
            String url = FileUploadUtil.uploadImage(multipartFile);

            // 保存图片记录到数据库
            Image image = new Image();
            image.setGroupTaskId(groupTaskId);
            image.setImage(multipartFile.getOriginalFilename());
            image.setUrl(url);
            imageMapper.insert(image);
        }
    }
    @Transactional
    public boolean deleteImage(Long groupTaskId) {
        // 从数据库查询图片
        QueryWrapper<Image> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", groupTaskId);
        List<Image> images = imageMapper.selectList(queryWrapper);
        if (images.isEmpty()) {
            return false;
        }
        boolean deleted = true;
        // 删除文件
        for (Image image : images){
            if (!FileUploadUtil.deleteFile(image.getFilePath())){
                deleted = false;
            }
        }
        if (deleted) {
            // 删除数据库记录
            imageMapper.delete(queryWrapper);
            return true;
        }
        return false;
    }
}

package com.studyParty.group.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.studyParty.entity.Source;
import com.studyParty.group.Utils.FileUploadUtil;
import com.studyParty.group.mapper.SourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
public class SourceServer {
    @Autowired
    private SourceMapper sourceMapper;

    @Transactional
    public Source getSourceUrl(MultipartFile file) {
        // 调用工具类上传文件
        Source source = FileUploadUtil.uploadImage(file);

//        // 保存图片记录到数据库
//        Source source = new Source();
//        source.setGroupTaskId(groupTaskId);
//        source.setImage(file.getOriginalFilename());
//        source.setUrl(url);
//        sourceMapper.insert(source);
        return source;
    }
    @Transactional
    public boolean deleteSource(Long Id,boolean isAnswer) {
        // 从数据库查询图片
        QueryWrapper<Source> queryWrapper = new QueryWrapper<>();
        if(isAnswer){
            queryWrapper.eq("group_task_answer_id", Id);
        }else {
            queryWrapper.eq("group_task_id", Id);
        }
        List<Source> sources = sourceMapper.selectList(queryWrapper);
        if (sources.isEmpty()) {
            return false;
        }
        boolean deleted = true;
        // 删除文件
        for (Source source : sources){
            // 使用新的方法通过物理路径删除文件
            if (!FileUploadUtil.deleteFileByPath(source.getFilePath())){
                deleted = false;
            }
        }
        if (deleted) {
            // 删除数据库记录
            sourceMapper.delete(queryWrapper);
            return true;
        }
        return false;
    }
}

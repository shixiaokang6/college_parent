package com.xn2001.college.service.edu.service.impl;

import com.xn2001.college.common.base.result.R;
import com.xn2001.college.service.edu.entity.Teacher;
import com.xn2001.college.service.edu.entity.vo.TeacherQueryVo;
import com.xn2001.college.service.edu.feign.OssFileService;
import com.xn2001.college.service.edu.mapper.TeacherMapper;
import com.xn2001.college.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author 乐心湖
 * @since 2020-06-23
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private OssFileService ossFileService;

    @Override
    public Page<Teacher> selectPage(Page<Teacher> pageParam, TeacherQueryVo teacherQueryVo) {

        //条件构造器
        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        //排序
        wrapper.orderByAsc("sort");
        //普通的排序分页查询
        if (teacherQueryVo == null) {
            return baseMapper.selectPage(pageParam, wrapper);
        }

        //获取对象参数
        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String joinDateBegin = teacherQueryVo.getJoinDateBegin();
        String joinDateEnd = teacherQueryVo.getJoinDateEnd();

        //条件
        if (!StringUtils.isEmpty(name)) {
            wrapper.likeRight("name", name);
        }

        if (level != null) {
            wrapper.eq("level", level);
        }

        if (!StringUtils.isEmpty(joinDateBegin)) {
            wrapper.ge("join_date", joinDateBegin);
        }

        if (!StringUtils.isEmpty(joinDateEnd)) {
            wrapper.le("join_date", joinDateEnd);
        }

        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectNameListByKey(String key) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name");
        queryWrapper.likeRight("name",key);
        List<Map<String,Object>> nameList = baseMapper.selectMaps(queryWrapper);

        return nameList;
    }

    @Override
    public boolean removeAvatarById(String id) {
        //根据ID查询讲师Avatar头像url地址
        Teacher teacher = baseMapper.selectById(id);

        if (teacher!=null){
            String avatar = teacher.getAvatar();
            if (!StringUtils.isEmpty(avatar)){
                R r = ossFileService.removeFile(avatar);
                return r.getSuccess();
            }
        }
        return false;
    }
}
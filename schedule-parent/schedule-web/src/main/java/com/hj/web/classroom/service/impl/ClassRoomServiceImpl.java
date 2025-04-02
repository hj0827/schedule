package com.hj.web.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.web.classroom.entity.ClassRoom;
import com.hj.web.classroom.entity.ListParm;
import com.hj.web.classroom.mapper.ClassRoomMapper;
import com.hj.web.classroom.service.ClassRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ClassRoomServiceImpl extends ServiceImpl<ClassRoomMapper, ClassRoom> implements ClassRoomService {
    @Override
    public IPage<ClassRoom> getList(ListParm listParm) {
//        构造分页查询对象
        IPage<ClassRoom> page = new Page<>();
        page.setCurrent(listParm.getCurrentPage());
        page.setSize(listParm.getPageSize());
//        构造查询条件
        QueryWrapper<ClassRoom> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(listParm.getRoomName())) {
            query.lambda().like(ClassRoom::getRoomName, listParm.getRoomName());
        }
        return this.baseMapper.selectPage(page, query);
    }

    @Override
    public boolean isRoomNameExists(String roomName) {
        QueryWrapper<ClassRoom> query = new QueryWrapper<>();
        query.lambda().eq(ClassRoom::getRoomName, roomName);
        return this.baseMapper.selectCount(query) > 0;
    }
}

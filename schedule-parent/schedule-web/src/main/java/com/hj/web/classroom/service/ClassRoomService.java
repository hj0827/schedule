package com.hj.web.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.classroom.entity.ClassRoom;
import com.hj.web.classroom.entity.ListParm;

public interface ClassRoomService extends IService<ClassRoom> {
    IPage<ClassRoom> getList(ListParm listParm);
    boolean isRoomNameExists(String roomName);
}

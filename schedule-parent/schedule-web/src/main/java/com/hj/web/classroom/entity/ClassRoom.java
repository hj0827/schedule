package com.hj.web.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("classroom")
public class ClassRoom {
//    自增
    @TableId(type= IdType.AUTO)
    private Long roomId;
    private String roomName;
    private String roomAddress;

}

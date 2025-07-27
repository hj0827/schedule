package com.hj.web.teacher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
// 映射表
@TableName("teacher")
public class Teacher {
    @TableId(type = IdType.AUTO)
    private Long teacherId;
    private String teacherName;
    // 教师编号
    private String teacherNum;
    // 教师描述
    private String teacherDesc;

    // 新增字段
    private String idCard; // 身份证号
    private String phone; // 手机号码
    private String bankName; // 开户行
    private String bankCity; // 开户市
    private String bankBranch; // 支行
    private String bankAccount; // 银行账号
    private Double salaryStandard; // 课酬标准
    private String teacherType; // 教师类型：全职、外聘
}

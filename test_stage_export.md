# 排课列表导出阶段名称修改说明

## 修改内容

### 1. 后端修改

#### 1.1 ScheduleCourse实体类
- 文件：`schedule-parent/schedule-web/src/main/java/com/hj/web/schedule/entity/ScheduleCourse.java`
- 添加了 `stageDescription` 字段：
```java
@TableField(exist = false)
private String stageDescription;
```

#### 1.2 SQL查询修改
- 文件：`schedule-parent/schedule-web/src/main/resources/mapper/ScheduleCourseMapper.xml`
- 在 `<sql id="column">` 中添加了：
```sql
stage_table.description as stage_description
```
- 在所有相关查询中添加了连表查询：
```sql
left join stage_table on cs.stage_id = stage_table.id
```

涉及的查询方法：
- `getScheduleList`
- `selectCourseSchedulingList`
- `selectClassRoomlingList`
- `selectTeaList`
- `getScheduleCourseList`

### 2. 前端修改

#### 2.1 导出逻辑修改
- 文件：`paike-web/src/views/schedule/ScheduleList.vue`
- 在导出函数的switch语句中添加了对"阶段名称"字段的处理：
```javascript
case '阶段名称':
    // 使用stage_table表的description字段
    return item.stageDescription || '';
```

## 修改效果

现在排课列表导出功能中的"阶段名称"字段将：
1. 通过连表查询从 `stage_table` 表获取 `description` 字段的值
2. 在导出的Excel中正确显示阶段的描述信息，而不是空值

## 数据库表结构参考

### stage_table表
```sql
CREATE TABLE `stage_table` (
  `id` int NOT NULL AUTO_INCREMENT,
  `stage_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '阶段名称',
  `description` text COLLATE utf8mb4_bin,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
```

### 示例数据
```sql
INSERT INTO `stage_table` VALUES 
(1,'精讲','基础精讲','2025-05-16 00:00:00','2025-07-10 10:49:02'),
(2,'真题','真题解析','2025-05-16 00:00:00','2025-07-10 10:51:33'),
(3,'密训','考前密训','2025-05-16 00:00:00','2025-07-10 10:51:26'),
(7,'考点','考点解析','2025-07-03 15:11:09','2025-07-10 10:51:19');
```

## 测试建议

1. 重新编译后端项目
2. 测试排课列表导出功能
3. 检查导出的Excel文件中"阶段名称"列是否显示正确的描述信息
4. 验证不同阶段的课程是否显示对应的描述

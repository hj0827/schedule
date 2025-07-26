import http from "../../http";
import { ListParm, scheduleParm, UpdateParm, Parms, StatisticsQueryParams } from "./ScheduleType";
//获取教室列表
export const getRoomListApi = () => {
    return http.get({
        url: "/api/schedule/getRoomList"
    })
}

//获取教师列表
export const getTeacherApi = () => {
    return http.get({
        url: "/api/schedule/getTeacher"
    })
}
// 获取课程列表
export const getCourseListApi = () => {
    return http.get({
        url: "/api/schedule/getCourseList"
    })
}

// 获取阶段列表
export const getStageListApi = () => {
    return http.get({
        url: "/api/stages"
    })
}

// 获取已排课程的信息
export const getScheduleInfoApi = (params: StatisticsQueryParams) => {
    console.log("获取已排课程的信息"+JSON.stringify(params, null, 2));
    return http.post({
        url: "/api/schedule/getScheduleInfo",
        data: params
    });
};

// 排课
export const saveScheduleApi = (parm: scheduleParm) => {
    return http.post({
        url: "/api/schedule/saveSchedule",
        data: parm
    })
}
 
// 排课列表(旧)
export const getScheduleListApi = (parm:ListParm) =>{
    console.log("获取排课列表1"+JSON.stringify(parm, null, 2));
    return http.get({
        url: "/api/schedule/getScheduleList",
        params: parm
    })
}

// 日历专用排课列表查询（非分页，获取全部数据）
export const getScheduleListForCalendarApi = (parm:ListParm) =>{
    console.log("获取日历排课列表"+JSON.stringify(parm, null, 2));
    return http.get({
        url: "/api/schedule/getScheduleListForCalendar",
        params: parm
    })
}



// 新增排课
export const addCalendarApi = (parm: UpdateParm) => {
    return http.post({
        url: "/api/schedule/addSchedule",
        data: parm
    })
}

// 编辑排课
export const updateCalendarApi = (parm: UpdateParm) => {
    console.log("编辑2222222222222" + JSON.stringify(parm, null, 2));
    // 创建一个新的对象，排除 courseType 字段
    const { courseType, ...newParm } = parm;
    console.log("编辑3333333333" + JSON.stringify(parm, null, 2));
    return http.put({
        url: "/api/schedule/updateScheduleById",
        data: newParm
    })
}

export const updateCalendarListApi = (parm: UpdateParm) => {
    return http.put({
        url: "/api/schedule/updateScheduleListById",
        data: parm
    })
}

// 删除排课

export const deleteCalendarApi = (id: string) => {
    console.log("删除课程id"+id);
    return http.delete(
        {
            url: `/api/schedule/deleteCalendarById/${id}`
        }
    )
}

// 新增排课页面的删除
// 删除主课程还得删除子课程
export const deleteCalendarListApi = (delId: string) => {
    console.log("新增排课页面的删除主课程id:"+delId);
    return http.delete(
        {
            url: `/api/schedule/deleteCalendarListById/${delId}`
        }
    )
}

// 批量删除课程排程
export const batchDeleteCalendarListApi = (delIds: string[]) => {
    console.log("批量删除课程排程delIds:", delIds);
    return http.delete(
        {
            url: `/api/schedule/batchDeleteCalendarListByIds`,
            data: delIds
        }
    )
}

// 根据考期、教师、教室查询到的日期数据去重后返回给前端，实现推荐课时
export const getClassRoomListApi = (parm:ListParm) =>{
    console.log("获取排课列表2"+JSON.stringify(parm, null, 2));
    return http.get({
        url: "/api/schedule/getClassRoomListApi",
        params: parm
    })
}

export const getTeaListApi = (parm:ListParm) =>{
    console.log("获取排课列表2"+JSON.stringify(parm, null, 2));
    return http.get({
        url: "/api/schedule/getTeaListApi",
        params: parm
    })
}

// 获取考期时间
export const getCourseTypeListApi = (parm:ListParm) =>{
    console.log("获取排课列表2"+JSON.stringify(parm, null, 2));
    return http.get({
        url: "/api/schedule/getCourseTypeListApi",
        params: parm
    })
}

// 获取合并课程的所有考期
export const getMergedCourseTermsApi = (courseId: string) => {
    return http.get({
        url: "/api/schedule/getMergedCourseTerms",
        params: { courseId }
    })
}

// 获取考期已排时间
export const getAllTermSchedulesApi = (courseId: string) => {
    return http.get({
        url: "/api/schedule/getAllTermSchedules",
        params: { courseId }
    })
}





import http from "../../http";
import { ListParm, scheduleParm, UpdateParm } from "./ScheduleType";
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

// 获取已排课程的信息(新)
export const getScheduleInfoApi = (params: Partial<ListParm>) => {
    return http.get({
        url: "/api/schedule/getScheduleInfo",
        params: params
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
    console.log("获取排课列表111111111"+JSON.stringify(parm, null, 2));
    return http.get({
        url: "/api/schedule/getScheduleList",
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
export const deleteCalendarListApi = (id: number) => {
    console.log("新增排课页面的删除主课程id:"+id);
    return http.delete(
        {
            url: `/api/schedule/deleteCalendarListById/${id}`
        }
    )
}

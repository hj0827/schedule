// 业务处理
import { ListParm, CourseModel } from "./BaseCourse"
import http from "../../http"


// 新增课程
export const addCourseApi = (parm: CourseModel[]) => {
    return http.post(
        {
            url: '/api/course',
            data: JSON.stringify(parm), // 将对象数组转换为 JSON 字符串
            headers: {
                'Content-Type': 'application/json' // 设置请求头
            }
        }
    );
};


// 列表查询
export const getListApi = (parm: ListParm) => {
    return http.get(
        {
            url: '/api/course/list',
            params: parm
        }
    )
}

// 编辑
export const editCourseApi = (parm: CourseModel[]) => {
    return http.put(
        {
            url: '/api/course',
            data: JSON.stringify(parm), // 将对象数组转换为 JSON 字符串
            headers: {
                'Content-Type': 'application/json' // 设置请求头
            }
        }
    )
}

// 删除
export const deleteCourseApi = (courseId: string) => {
    console.log("删除课程id"+courseId);
    return http.delete(
        {
            url: `/api/course/${courseId}`
        }
    )
}
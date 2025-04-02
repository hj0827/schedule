import { idText } from "typescript";

export type scheduleParm = {
    courseId: string,
    roomId: string,
    teacherId: string,
    //开课时间
    startDate: string,
    //结课时间
    endDate: string,
    weeks: number[],
    //上课时间
    beginTime: string,
    //下课时间
    endTime: string,
    duration: number | string

}

//列表参数
export type ListParm = {
    roomIdList: string[],
    courseIdList: string[],
    teacherIdList: string[],
    startDate: string,
    endDate: string,
    beginTime: string,
    endTime: string,
    duration?: number | string,
    courseName?: string;
    courseType?: string;
    currentPage?: number;
    teacherName?: string;
}

export type EditModel = {
    beginTime: string,
    courseColor: string,
    courseId: string,
    courseName: string,
    dateTime: string,
    duration: number,
    endTime: string,
    id: string | number,
    roomAddress: string,
    roomId: string,
    roomName: string,
    teacherName: string,
    teacherId: string,
}

// 更新参数类型
export type UpdateParm = {
    id: string | number,
    roomId?: string | number,
    courseId?: string | number,
    teacherId?: string | number,
    dateTime: string,
    beginTime: string,
    duration: string | number,
    endTime: string,

    courseType: string,
    majorName: string,
    courseYear: string,
    teacherName: string,
    courseName: string,
    roomName: string,
    courseColor: string,
    roomAddress: string,
    startDate: string,
    endDate: string
}

// 移动日历参数类型
export type ReMoveParm = {
    id: string | number,
    dateTime: string,
    beginTime: string,
    endTime: string
}

export type ScheduleModel = {
    id: string | number,
    courseId: string,
    roomId: string,
    teacherId: string,
    beginTime: string,
    endTime: string,
    duration: number,
    dateTime: string
}




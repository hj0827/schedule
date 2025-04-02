// 列表参数
export interface ListParm {
    currentPage: number;
    pageSize: number;
    courseName?: string;
    courseType: string;

}


// 新增、编辑
export interface CourseModel {
    courseId: string;
    courseName: string;
    majorName:string;
    courseType: string;
    courseYear: string;
    courseColor: string;
    type: string;
    isMergeClasses: string;
}
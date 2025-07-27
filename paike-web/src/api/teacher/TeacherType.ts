//列表查询参数
export type ListParm={
    pageSize:number;
    currentPage:number;
    teacherName:string;
    
}
//表单类型
export type TeacherModel = { 
    type: string,
    teacherId: string, 
    teacherName: string,
    teacherNum: string, 
    teacherDesc: string,
    // 新增字段
    idCard: string,
    phone: string,
    bankName: string,
    bankCity: string,
    bankBranch: string,
    bankAccount: string,
    salaryStandard: number,
    teacherType: string
}
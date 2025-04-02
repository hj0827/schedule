// 新增数据类型
export interface RoleType {
    type: string,
    roleId: string,
    roleName: string,
    roleDesc: string
}

// 列表参数
export interface RoleListParm {
    pageSize: number,
    currentPage: number,
    roleName: string
}

export interface TreeParm{
    userId: string,
    roleId: string
}

export interface SaveAssign{
    roleId: string,
    list:[]
}
import http from "@/http";
import { UserType } from "@/api/user/UserType";
import { ListParm } from "../schedule/ScheduleType";
// 获取角色
export const getRoleListApi = () =>{
    return http.get({
        url:'/api/user/role'
    })
}
// 新增
export const addUserApi = (parm:UserType) =>{
    console.log("角色新增：",parm)
    return http.post({
        url:'/api/user',
        data:parm
    })
}

// 列表
export const getListApi = (parm:ListParm)=>{
    return http.get({
        url:'/api/user/list',
        params:parm
    })
}

// 根据id查询用户
export const getUserByIdApi = (useId:string)=>{
    return http.get({
        url:'/api/user/getUser',
        params:{
            userId:useId
        }
    })
}

// 编辑
export const editUserApi = (parm:UserType) =>{
    console.log("角色编辑：",JSON.stringify(parm))
    return http.put({
        url:'/api/user',
        data:parm
    })
}

// 其他用户修改密码
export const updateUserApi = (parm:UserType) =>{
    console.log("角色编辑：",JSON.stringify(parm))
    return http.put({
        url:'/api/user/updateUser',
        data:parm
    })
}

// 删除
export const deleteUserApi = (userId: string) => {
    return http.delete({
        url: `/api/user/${userId}`
    });
}

// 导入
export const importUserApi = (data: FormData) => {
    console.log("导入：",data)
    return http.post({
        url: '/api/user/importUser',
        data: data,
        headers: {
             'Content-Type': 'application/json'
        }
    });
};

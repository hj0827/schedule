import http from "@/http";
import { MenuModel } from "./MenuType";

// 获取上级列表
export const getParentListApi = () => {
    return http.get({
        url: "/api/menu/parent"
    })
}

// 新增
export const addApi = (parm:MenuModel)=>{
    return http.post({
        url:'/api/menu',
        data:parm
    })
}

// 列表
export const getListApi = ()=>{
    return http.get({
        url:'/api/menu/list'
    })
}

// 编辑
export const editApi = (parm:MenuModel)=>{
    return http.put({
        url:'/api/menu',
        data:parm
    })
}
// 删除
export const deleteApi = (menuId:string)=>{
    return http.delete({
        url:`/api/menu/${menuId}`
    })
}
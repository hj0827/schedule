// 表单提交
import http from "../../http";
import { ListParm, RoomModel } from "./RoomType";
// 新增
export const addApi = (parm: RoomModel) => {
    return http.post({
        url: '/api/classroom',
        data: parm
    })

}
//列表查询
export const getListApi = (parm: ListParm) => {
    return http.get({
        url: '/api/classroom/list',
        params: parm
    })
}
// 编辑
export const editApi = (parm: RoomModel) => {
    return http.put({
        url: '/api/classroom',
        data: parm
    })
}
// 删除
export const deleteApi = (roomId: string) =>{
    return http.delete({
        url:`/api/classroom/${roomId}`
    })
}


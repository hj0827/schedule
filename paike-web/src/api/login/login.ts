import http from "@/http";
import { LoginParm } from "./LoginType";
import { getUserId } from "@/utils/auth";
// 登录
export const loginApi = (parm: LoginParm) => {
    return http.post({
        url: '/api/login/login',
        data: parm
    })
}

// 获取菜单数据
export const getMenuListApi = () => {
    return http.get({
        url: '/api/login/getMenuList',
        params: {
            userId: getUserId() || ''
        }
    })
}
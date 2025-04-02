import { defineStore } from "pinia";
import { getMenuListApi } from "@/api/login/login";
import { RouteRecordRaw } from "vue-router";
import Layout from "@/layout/index.vue";
import Center from "@/layout/center/center.vue";

const modules = import.meta.glob('../../views/**/*.vue');

export const menuStore = defineStore('menuStore', {
    state: () => ({
        menuList: [],
        authList: [],
    }),
    getters: {
        getMenu: (state) => {
            return state.menuList
        },
        getAuth: (state) => {
            return state.authList
        },
    },
    actions: {
        async getMenuList(router: any) {
            // 请求菜单数据
            let res = await getMenuListApi() as any;
            let accessedRoutes = [] as any;
            if (res && res.code == 200) {
                // 处理获取到的菜单数据
                accessedRoutes = filterAsyncRoutes(res.data.menuList, router);
                // 存储权限字段
                this.authList = res.data.authList;
            }
            if (accessedRoutes.length > 0) {
                // const desk = [
                //     {
                //         path:'dashboard',
                //         component:'Layout',
                //         meta:{
                //             title:"首页",
                //             icon:"home-outlined",
                //             roles:["sys:manage"]
                //         },
                //         children:[]
                //     }
                // ] as any;
                // this.menuList = this.menuList.concat(desk).concat(accessedRoutes);
                this.menuList = this.menuList.concat(accessedRoutes);
            }
        }
    }
});

// 生成动态路由
export function filterAsyncRoutes(routes: RouteRecordRaw[], router: any) {
//    定义接收生成的路由数据
    const res: Array<RouteRecordRaw> = [];
    // 遍历路由数据
    routes.forEach((route: any) => {
        // 解构路由数据
        let tmp = { ...route } as any;
        const component = tmp.component;
        if (route.component) {
            if (component == 'Layout') {
                tmp.component = Layout;
            } else {
                tmp.component = modules[`../../views${component}`];
                console.log("modules", modules,tmp.component);
            }
        }
        
        if (tmp.name === 'index') {
            console.log("找到了index",tmp.name);
            tmp.children = [];
        } 
        if (tmp.name === 'anxiaoqiao') {
            console.log("找到了anxiaoqiao",tmp.name);
            tmp.children = [];
        } 

        // 递归
        if (tmp.children && tmp.children.length > 0) {
            if (route.component != 'Layout') {
                tmp.component = Center; // 修正这里的变量名
            }
            // 递归下级
            tmp.children = filterAsyncRoutes(tmp.children, router);
        }
        // 添加路由
        router.addRoute(tmp);
        res.push(tmp);
    });
    console.log('res', res);
    
    return res;
}
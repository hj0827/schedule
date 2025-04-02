// 引入路由
import router from "./router";
// 获取token
import { getToken, clearSession } from "./utils/auth";
// 引入store
import { menuStore } from "./store/menu/index";
import { el } from "node_modules/@fullcalendar/core/internal-common";

// 白名单
const whiteList = ["/login"];

// 全局守卫路由处理
router.beforeEach(async(to, from, next) => {
    const store = menuStore();
    //   获取token
    const token = getToken();
    if (token) {
        if (to.path === '/login' || to.path === '/') {
            next({ path: '/' })
        } else {
            // 从store里面获取菜单数据
            let hasRoles = store.menuList && store.menuList.length > 0
            if (hasRoles) {
                next()
            } else {
                try {
                    // 从服务器获取菜单数据
                    await store.getMenuList(router)
                    // 等待路由全部挂载
                    next({...to, replace: true })
                } catch (error) {
                    console.log(error)
                    clearSession()
                    next({ path: '/login' })
                }

            }
        }
    } else {
        // token不存在
        // 判断是否在白名单中
        if (whiteList.indexOf(to.path) !== -1) {
            // 在白名单中
            next();
        } else {
            // 不在白名单中
            next({ path: '/login' });
        }
    }

});

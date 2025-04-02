import { Directive } from "vue";
import { menuStore } from "@/store/menu/index";

export const permission: Directive = {
    mounted(el, binding) {
        //    获取store
        const store = menuStore();
        // 获取用户的所有权限
        const permissions = store.authList;
        // 获取传递进来的按钮权限
        const { value } = binding;
        // 当用户没有权限时，将元素隐藏
        if (value && value instanceof Array && value.length > 0) {
            const permissionRoles = value;
            //   判断按钮权限，是否存在所有的权限字段里面
            const hasPermission = permissions.some((role) => {
                return permissionRoles.includes(role)
            })
            // 如果不存在，隐藏按钮
            if (!hasPermission) {
                el.style.display = "none";
            }
        }else{
            throw new Error(`need roles! Like v-permission="[\'admin\',\'edit\']"`)
        }

    }
}
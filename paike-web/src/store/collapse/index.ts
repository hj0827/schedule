import { defineStore } from "pinia";

export const collapseStore = defineStore('isCollapse', {
    // 定义state
    state: () => {
        return {
            // 控制导航栏伸缩，从localStorage读取初始状态
            isCollapse: JSON.parse(localStorage.getItem('isCollapse') || 'false')
        }
    },
    // 定义getters
    getters: {
        getCollapse: (state) => {
            return state.isCollapse;
        }
    },
    // 定义actions
    actions: {
        setCollapse() {
            this.isCollapse = !this.isCollapse;
            // 保存状态到localStorage
            localStorage.setItem('isCollapse', JSON.stringify(this.isCollapse));
        }
    }
});
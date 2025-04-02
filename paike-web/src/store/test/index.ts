import { defineStore } from 'pinia'
export const testStore = defineStore('testStore', {
    // 定义state
    state: () => ({
        count: 10
    }),
    // 定义getters
    getters: {
        // 获取count
        getCount(state) {
            return state.count
        }
    },
    // 定义actions 操作state
    actions: {
        // 不能使用箭头函数，箭头函数会改变this的指向
        setCount() {
            this.count++
        }
    }
})
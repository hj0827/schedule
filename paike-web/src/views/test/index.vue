<template>
    <div class="testTotal">
        <div>{{ count }}</div>
        <a-button type="primary" @click="addBtn">新增</a-button>
    </div>
    
</template>

<script setup>
import {ref,reactive, computed} from 'vue'
import { testStore } from '../../store/test';
import { storeToRefs } from 'pinia';

// 获取store
const store = testStore()

// 方式一：
//此方式获取到的count失去响应性
// 获取store里面的count 
// const {count} = store;

// 方式二
// 解决方式一失去响应性的问题
// const {count} = storeToRefs(store)

// 方式三
const count = computed(()=>{
    return store.getCount
})
// 按钮点击事件
const addBtn = ()=>{
    // 方式一
    // store.setCount()
    
    // 方式二
    // store.$patch({
    //     count:store.count+10
    // })

    // 方式三
    store.$patch((state)=>{
        state.count += 10
    })

}
</script>

<style scoped lang="scss">
    .testTotal{
        display: flex;
        justify-content: center;
        align-items: center;
        // 垂直排列
        flex-direction: column;
    }
</style>



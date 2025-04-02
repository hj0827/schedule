<template>
    <!-- <div>
        <ul>
            <li v-for="(tab, index) in tabs" :key="index">
                {{ tab.meta.title }}
            </li>
        </ul>
    </div> -->
    <a-breadcrumb class="bred">
        <a-breadcrumb-item class="item" v-for="item in tabs" :key="item">{{ item.meta.title }}</a-breadcrumb-item>
    </a-breadcrumb>
</template>

<script setup lang="ts">
import { onMounted, ref,watch } from 'vue'
import { RouteLocationMatched, useRoute } from 'vue-router';

// 当前路由
const route = useRoute();

// 定义面包屑导航数据
const tabs = ref<RouteLocationMatched[]>([]);

// 获取面包屑导航数据
const getBred = () => {
    let mached = route.matched.filter(item => item.meta && item.meta.title);
    // console.log(mached);
    const first = mached[0];
    // 加上首页
    // if (first && first.path !== '/dashboard') {
    //     mached = [{ path: '/dashboard', meta: { title: '首页' } } as any].concat(mached);
    // }
    // 设置面包屑导航数据
    tabs.value = mached;
}

onMounted(() => {
    getBred();
})

// 监听路由变化
watch(() => route.path, () => {
    getBred();
})
</script>

<style lang="scss" scoped>
.bred {
    margin-left: 15px;

    .item {
        color: #fff;
    }
}

:deep(.ant-breadcrumb-separator){
    color: #fff !important;
}
</style>
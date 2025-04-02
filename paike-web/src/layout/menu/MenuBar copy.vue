<template>
    <!-- logo -->
    <menu-logo></menu-logo>
    <a-menu v-model:openKeys="menuData.openKeys" v-model:selectedKeys="menuData.selectedKeys" mode="inline"
         router="true" @openChange="onOpenChange" :inline-collapsed="menuData.collapsed">
        <a-menu-item key="/dashboard">
            <template #icon>
                <home-outlined />
            </template>
            <router-link to="/dashboard">首页</router-link>
        </a-menu-item>

        <!-- 系统管理 -->
        <a-sub-menu key="/system">
            <template #icon>
                <setting-outlined />
            </template>
            <template #title>
                系统管理
            </template>
            <a-menu-item key="/user" index="/user">
                <template #icon>
                    <user-outlined />
                </template>
                <router-link to="/user">用户管理</router-link>
            </a-menu-item>
            <a-menu-item key="/role">
                <template #icon>
                    <PieChartOutlined />
                </template>
                <router-link to="/role">角色管理</router-link>
            </a-menu-item>
            <a-menu-item key="/menu">
                <template #icon>
                    <unordered-list-outlined />
                </template>
                <router-link to="/menu">菜单管理</router-link>
            </a-menu-item>
        </a-sub-menu>

        <!-- 课程管理 -->
        <a-sub-menu key="/courseRoot">
            <template #icon>
                <file-done-outlined />
            </template>
            <template #title>
                课程管理
            </template>
            <a-menu-item key="/course" index="/course">
                <template #icon>
                    <user-outlined />
                </template>
                <router-link to="/course">课程列表</router-link>
            </a-menu-item>
        </a-sub-menu>

        <!-- 教师管理 -->
        <a-sub-menu key="/teacherRoot">
            <template #icon>
                <user-switch-outlined />
            </template>
            <template #title>
                教师管理
            </template>
            <a-menu-item key="/teacher" index="/teacher">
                <template #icon>
                    <user-delete-outlined />
                </template>
                <router-link to="/teacher">教师列表</router-link>
            </a-menu-item>
        </a-sub-menu>

        <!-- 教室管理 -->
        <a-sub-menu key="/classroomRoot">
            <template #icon>
                <shop-outlined />
            </template>
            <template #title>
                教室管理
            </template>
            <a-menu-item key="/classroom" index="/classroom">
                <template #icon>
                    <align-center-outlined />
                </template>
                <router-link to="/classroom">教室列表</router-link>
            </a-menu-item>
        </a-sub-menu>

        <!-- 排课管理 -->
        <a-sub-menu key="/scheduleRoot">
            <template #icon>
                <AppstoreOutlined />
            </template>
            <template #title>
                排课管理
            </template>
            <a-menu-item key="/schedule" index="/schedule">
                <template #icon>
                    <user-outlined />
                </template>
                <router-link to="/schedule">排课日历</router-link>
            </a-menu-item>
            <a-menu-item key="/scheduleList">
                <template #icon>
                    <menu-outlined />
                </template>
                <router-link to="/scheduleList">排课列表</router-link>
            </a-menu-item>

        </a-sub-menu>

    </a-menu>

</template>
<script setup lang="ts">
import { useRoute } from 'vue-router';
import { onMounted, reactive, watch } from 'vue';

// 引入伸缩组件
import MenuLogo from './MenuLogo.vue';
// 引入路由组件
import { routes } from '@/router';

// 当前路由
const route = useRoute();
const menuData = reactive({
    selectedKeys: [route.path] as string[],
    openKeys: [] as string[], // 明确 openKeys 的类型为 string[]
    collapsed: false,
});

// 设置当前选中的菜单
const selectKey = () => {
    if (!menuData.selectedKeys.includes(route.path)) {
        menuData.selectedKeys = [route.path];
    }
};

// 监听路由发生变化的时候
watch(() => route.path, () => {
    // 清空原来的数据
    menuData.selectedKeys = [''];
    selectKey();
    setMenuOpen(routes);
});

onMounted(() => {
    selectKey();
    setMenuOpen(routes);
});

// 刷新时，需要展开相应下级菜单
const setMenuOpen = (result: any) => {
    for (let i = 0; i < result.length; i++) {
        if (result[i].children) {
            for (let y = 0; y < result[i].children.length; y++) {
                if (result[i].children[y].path === route.path) {
                    menuData.openKeys = [result[i].path]; // 现在不会报错了
                }
            }
        }
    }
};

// 一次只能展开一个菜单
const onOpenChange = (openKeys: string[]) => {
    console.log(openKeys);
    if (openKeys.length !== 0) {
        menuData.openKeys = [openKeys[1]];
    } else {
        menuData.openKeys = [''];
    }
};
</script>
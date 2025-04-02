<template>
    <a-tabs v-model:activeKey="activeKey" hide-add type="editable-card" @tabClick="tabClick" @edit="onEdit">
        <a-tab-pane v-for="pane in panes" :key="pane.path" :tab="pane.title"
            :closable="pane.path !== '/dashboard'"></a-tab-pane>
    </a-tabs>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { tabsStore } from "../../store/tabs"
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';
// 获取store
const store = tabsStore()

// 当前路由
const route = useRoute()
const router = useRouter()

// 选项卡的数据，从store里面获取
const panes = computed(() => {
    return store.getTabs
})

// 当前激活选项卡的key
const activeKey = ref('')
// 设置当前激活的选项卡
const setActiveTab = () => {
    activeKey.value = route.path
}

// 关闭
const remove = (targetKey: string) => {
    // 首页不能关闭
    if (targetKey === '/dashboard') return
    console.log('targetKey', targetKey)
    // 清除特定页面的会话数据
    if (targetKey == '/course') {
        sessionStorage.removeItem('courseSearchParm');
    } else if (targetKey == '/user') {
        console.log("清除特定页面的会话数据")
        sessionStorage.removeItem('searchState');
    }else if (targetKey == '/teacher') {
        sessionStorage.removeItem('savedteacher');
    } else if (targetKey == '/classroom') {
        sessionStorage.removeItem('classroomSearchParm');
    } else if (targetKey == '/schedule') {
        sessionStorage.removeItem('scheduleSearchParm');
    } else if (targetKey == '/scheduleCalendar') {
        sessionStorage.removeItem('scheduleCalendarSearchParm');
    }

    let lastIndex = 0;
    store.tabsList.forEach((pane, i) => {
        if (pane.path === targetKey) {
            lastIndex = i - 1;
        }
    });
    store.tabsList = store.tabsList.filter(pane => pane.path !== targetKey);
    if (store.tabsList.length && activeKey.value === targetKey) {
        if (lastIndex >= 0) {
            activeKey.value = store.tabsList[lastIndex].path;
        } else {
            activeKey.value = store.tabsList[0].path;
        }
    }
    // 跳转路由
    router.push({ path: activeKey.value })
};

// 删除选项卡的方法
const onEdit = (targetKey: string) => {
    remove(targetKey)
}

// 监听当前路由，把当前菜单的数据添加到选项卡数据
const addTab = () => {
    // 获取title和path
    const { path, meta } = route;
    const tab = {
        path: path,
        title: meta.title as string
    }
    // 添加到选项卡数据
    store.addTab(tab)
}

watch(() => route.path, () => {
    // 设置激活的选项卡key,就是当前路由的path
    setActiveTab()
    // 添加选项卡数据
    addTab()
})

// 解决浏览器刷新选项卡数据丢失的问题
const beforeRefresh = () => {
    if (route.path != '/login') {
        window.addEventListener('beforeunload', () => {
            sessionStorage.setItem('tabsview', JSON.stringify(store.tabsList))
        })
        let tabSesson = sessionStorage.getItem('tabsview')
        if (tabSesson) {
            let oldTabs = JSON.parse(tabSesson)
            if (oldTabs.length > 0) {
                store.tabsList = oldTabs
            }
        }
    }
}

onMounted(() => {
    beforeRefresh()
    // 设置激活的选项卡key,就是当前路由的path
    setActiveTab()
    // 添加选项卡数据
    addTab()
})
// 选项卡的点击事件
const tabClick = (key: string) => {
    // 点击的时候跳转路由
    router.push(key)
}
</script>
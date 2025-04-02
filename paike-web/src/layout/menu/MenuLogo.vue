<template>
    <div class="logo">
        <router-link to="/">
            <img src="@/assets/images/1.png" alt="logo" />
            <span v-show="show" class="title">安桥排课系统</span>
        </router-link>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { collapseStore } from '../../store/collapse';

const store = collapseStore();
const collapse = computed(() => store.getCollapse);

// 根据初始状态设置show
const show = ref(!collapse.value);

// 监听collapse值，做延时处理
watch(() => collapse.value, (collapsed: boolean) => {
    if (!collapsed) {
        setTimeout(() => {
            show.value = true;
        }, 250);
    } else {
        show.value = false;
    }
});
</script>

<style lang="scss" scoped>
.logo {
    background-color: #2f54eb;
    height: 50px;
    border: none;
    line-height: 50px;
    display: flex;
    align-items: center;
    padding-left: 28px;
    color: #fff;

    img {
        width: 25px;
        height: 25px;
        // margin-right: 12px;
    }

    .title {
        height: 50px;
        background-color: #2f54eb;
        text-align: center;
        color: #fff;
        box-sizing: border-box;
        font-weight: 600;
        line-height: 50px;
        font-size: 18px;
        vertical-align: middle;
    }
}
</style>
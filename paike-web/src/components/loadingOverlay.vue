<!-- src/components/loadingOverlay.vue -->
<template>
    <div v-if="visible" class="loading-overlay">
        <a-spin />
    </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';

const props = defineProps<{
    visible: boolean;
}>();
</script>

<style scoped>
.loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(255, 255, 255, 0);
    /* 黑色背景，透明度 80% */
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    /* 确保加载动画在最上方 */
}
</style>
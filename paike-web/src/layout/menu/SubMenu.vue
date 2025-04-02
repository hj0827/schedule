<template>
    <a-sub-menu :key="menuInfo.path">
        <template #icon>
            <component :is="menuInfo.meta.icon" />
        </template>
        <template #title>{{ menuInfo.meta.title }}</template>
        <template v-for="item in menuInfo.children" :key="item.path">
            <template v-if="!item.children">
                <a-menu-item :key="item.path">
                    <template #icon>
                        <component :is="item.meta.icon" />
                    </template>
                    <router-link :to="item.path">{{ item.meta.title }}</router-link>
                </a-menu-item>
            </template>
            <template v-else>
                <sub-menu :menu-info="item" :key="item.path"></sub-menu>
            </template>
        </template>
    </a-sub-menu>
</template>

<script setup lang="ts">
    defineProps(['menuInfo'])
</script>
<style lang="scss" scoped>
    
</style>
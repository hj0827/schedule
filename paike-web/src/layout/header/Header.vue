<template>
    <div class="header-bg">
        <div class="header-left">
            <Collapse></Collapse>
            <Bred></Bred>
        </div>
        <div>
            <right-menu></right-menu>
        </div>
    </div>
</template>

<script setup>
import Bred from './Bred.vue';
import Collapse from '../header/Collapse.vue'
import RightMenu from './RightMenu.vue';
</script>

<style lang="scss" scoped>
.header-bg {
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-grow: 1;
}

.header-left {
    display: flex;
}

// .ant-layout-header {
//     padding: 0 20px;
// }
</style>
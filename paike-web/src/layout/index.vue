<template>
  <a-layout class="layout">
    <a-layout-sider v-model:collapsed="collapsed" :trigger="null" class="asside">
      <MenuBar />
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="header">
        <Header></Header>
      </a-layout-header>
      <a-layout-content class="main">
        <!-- 选项卡 -->
        <Tabs />
        <!-- 路由 -->
        <div class="router-view-container">
          <router-view></router-view>
        </div>
      </a-layout-content>
      <a-layout-footer class="footer">
        © 2024, 版权所有：安桥 | 关于 | <a href="src\assets\ExcelTemplates\《安桥排课系统》免责声明.docx">免责声明</a>
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
// 引入侧边导航栏
import MenuBar from './menu/MenuBar.vue';
import Header from './header/Header.vue';
import { collapseStore } from '../store/collapse';
// 引入选项卡
import Tabs from './tabs/Tabs.vue';

const store = collapseStore();
const collapsed = computed(() => {
  return store.getCollapse
});
</script>

<style scoped lang="scss">
.layout {
  height: 100vh; // 设置布局高度为视口高度

  .asside {
    width: 100px;
    background-color: #fff;
    border-right: 1px solid #f0f0f0 !important;

    :deep(.ant-menu-inline) {
      border-right: none !important;
    }
  }

  .header {
    height: 50px;
    padding: 0px 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #2f54eb;
  }

  .main {
    padding: 3px 15px !important;
    background-color: #fff !important;
    flex: 1; // 使 main 区域占据剩余空间
    display: flex;
    flex-direction: column;
  }

  .router-view-container {
    flex: 1; // 使 router-view 容器占据剩余空间
    // overflow-y: auto; // 启用垂直滚动
    // padding: 10px 0; // 可选：添加一些内边距
  }

  .footer {
    text-align: center;
    background-color: #fff !important;
  }

  .ant-layout-footer {
    padding: 3px 0px; 
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
    background: #f0f2f5;
  }

  .ant-tabs-nav{
    margin: 0;
  }

  
  // 添加 Tabs 的样式
  .main> :first-child {
    // margin-bottom: 10px;
  }
}
</style>
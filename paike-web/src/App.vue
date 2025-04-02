<template>
  <a-config-provider :locale="locale">
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
// 页数国际化配置
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
import {ref} from 'vue'
dayjs.locale('zh-cn');
const locale = ref(zhCN);

</script>

<style lang="scss">
  .scheduleClass{
    height: 400px;
    overflow-y: auto;
  }
  // 滚动条样式
  ::-webkit-scrollbar {
    width: 8px;
    height: 10px;
    background-color: #f5f5f5;
  }
  // 滚动条的轨道 内阴影+圆角
  ::-webkit-scrollbar-track {
    background-color: #f5f5f5;
    box-shadow: inset 0 0 5px rgba(0,0,0,0.3);
    -webkit-box-shadow: inset 0 0 5px rgba(0,0,0,0.3);
    border-radius: 10px;
  }
  // 滚动条的滑块
  ::-webkit-scrollbar-thumb {
    border-radius: 10px;
    box-shadow: inset 0 0 6px rgba(0,0,0,0.5);
    background-color: #d7d7d7;
  }
  
  ::-webkit-scrollbar-thumb:after{
    background-color: #b1b1b1;
  }
  
  :deep(.ant-modal-body){
    padding: 10px !important;
  }
</style>

<template>
  <a-dropdown placement="bottomLeft">
    <a class="ant-dropdown-link" @click.prevent>
      <span>{{ name }}</span>
      <user-outlined class="right-img" />
    </a>
    <template #overlay>
      <a-menu>
        <a-menu-item key="1" @click="showChangePasswordDialog">修改密码</a-menu-item>
        <a-menu-item key="2" @click="loginOut">退出登录</a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
  <change-password-modal :visible="isChangePasswordModalVisible" @onClose="closeChangePasswordModal" @onConfirm="handlePasswordChange" />
</template>

<script setup lang="ts">
import { clearSession, getUserName, getName } from '@/utils/auth';
import { useRouter } from 'vue-router';
import { ref, onMounted } from 'vue';
import { tabsStore } from '@/store/tabs';
import ChangePasswordModal from './ChangePasswordModal.vue';
import { getUserId } from '@/utils/auth';

const tabs = tabsStore();
const router = useRouter();
const name = ref('');
const isChangePasswordModalVisible = ref(false);

// 获取用户名
onMounted(() => {
  name.value = getName() || '';
});

// 退出登录
const loginOut = () => {
  clearSession();
  router.push({
    name: 'login',
  });
  // 清空tabs
  tabs.tabsList = [];
};

const showChangePasswordDialog = () => {
  isChangePasswordModalVisible.value = true;
};

const closeChangePasswordModal = () => {
  isChangePasswordModalVisible.value = false;
};

const handlePasswordChange = () => {
  // 这里可以添加修改密码后的逻辑
  console.log('密码修改成功');
  closeChangePasswordModal();
};
</script>

<style scoped>
.ant-dropdown-link {
  color: #fff;
}
</style>
<template>
  <sys-dialog :title="dialog.title" :width="dialog.width" :height="dialog.height" :visible.sync="dialog.visible"
    @onClose="onClose" @onConfirm="confirm">
    <template #content>
      <a-tree ref="assignTree" v-if="treeData && treeData.length > 0" class="myTree" checkable defaultExpandAll
        :show-line="showLine" :tree-data="treeData" :fieldNames="fieldNames" v-model:checkedKeys="checkedKeys">

      </a-tree>
    </template>
  </sys-dialog>
</template>

<script setup lang="ts">
import { RoleType } from '@/api/role/RoleType';
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import useAssign from '@/api/role/useAssign';
import { getUserId } from '@/utils/auth';
import { assignSaveApi } from '@/api/role/role';
import { ref, defineExpose } from 'vue';
import { message } from 'ant-design-vue';

const roleId = ref();
const { assignTree, showLine, checkedKeys, fieldNames, treeData, getTreeData } = useAssign();
const { dialog, onClose, onShow } = useDialog();

// 弹窗确认
const confirm = async () => {
  const ids = assignTree.value.checkedKeys.concat(assignTree.value.halfCheckedKeys);
  let parm = {
    roleId: roleId.value,
    list: ids
  }
  let res = await assignSaveApi(parm) as any;
  if (res && res.code == 200) {
    message.success(res.msg);
    onClose();
  } else {
    message.error(res.msg)
  }

};

// 显示弹窗
const show = (row: RoleType) => {
  roleId.value = row.roleId;
  dialog.title = '为【' + row.roleName + '】分配权限';
  dialog.height = 450;
  dialog.width = 300;

  const userId = getUserId() || ''; // 确保 userId 不为 null
  if (!userId) {
    console.error('用户ID无效');
    return;
  }

  const parm = {
    userId,
    roleId: row.roleId
  };

  console.log('请求参数:', parm); // 打印请求参数

  // 获取树的数据
  getTreeData(parm);

  onShow();
};

defineExpose({
  show
});
</script>
<style lang="scss">
.myTree {
  .ant-tree-switcher-noop {
    display: none !important;
  }
}
</style>
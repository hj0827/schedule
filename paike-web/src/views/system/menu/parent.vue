<template>
    <sys-dialog :title="dialog.title" :width="dialog.width" :height="dialog.height" :visible="dialog.visible"
        @onClose="onClose" @onConfirm="confirm">
        <template #content>
            <a-tree
            :show-line="showLine"
            :tree-data="treeData"
            :fieldNames="fieldNames"
            @select="onSelect"
            >

            </a-tree>
        </template>
    </sys-dialog>
</template>
<script setup lang="ts">
import { ref, reactive } from 'vue'
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import useParent from '@/composable/menu/useParent';
import { message } from 'ant-design-vue';
const { dialog, onClose, onShow } = useDialog();
const { showLine,treeData,fieldNames,onSelect,selectNode,getTreeData } = useParent();
// 显示弹窗
const show = (type: string) => {
    getTreeData()
    dialog.height = 450
    dialog.width = 300
    dialog.title = "选择上级菜单"
    onShow()
}
// 定义事件
const emit = defineEmits(['selectId'])
// 弹框确定
const confirm = () => {
    if(!selectNode.parentName){
        message.error("请选择上级菜单")
        return
    }
    emit('selectId',selectNode)
    onClose()
}

// 暴露出去
defineExpose({
    show
})
</script>
<style scoped lang="scss"></style>
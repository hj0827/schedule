<template>
    <sys-dialog :title="dialog.title" :height="dialog.height" :visible="dialog.visible" :width="dialog.width"
        @onClose="onClose" @onConfirm="confirm">
        <template #content>
            <a-form>
                <a-form-item v-bind="validateInfos.roomName" label="直播间名称">
                    <a-input v-model:value="addModel.roomName" placeholder="请输入直播间名称"></a-input>
                </a-form-item>
                <a-form-item v-bind="validateInfos.roomAddress" label="直播间地址">
                    <a-input v-model:value="addModel.roomAddress" placeholder="请输入直播间地址"></a-input>
                </a-form-item>
            </a-form>
        </template>
    </sys-dialog>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
// 引入弹窗组件
import SysDialog from '../../components/SysDialog.vue';
import useDialog from '../../hooks/useDialog';
import { EditType, Title } from '../../type/BaseEnum';
import { RoomModel, ListParm } from '../../api/classroom/RoomType';
import {useForm} from 'ant-design-vue/lib/form';
import useInstance from '../../hooks/useInstance';
// 获取全局属性
const { global } = useInstance()

// 获取弹窗属性
const { dialog, onShow, onClose } = useDialog()
// 表单验证规则
const rules = reactive({
    roomName: [{
        required: true,
        message: '请输入直播间名称',
        trigger: 'change'
    }],
    roomAddress: [{
        required: true,
        message: '请输入直播间地址',
        trigger: 'change'
    }]
})

// 表单绑定的对象
const addModel = reactive<RoomModel>({
    type: '',
    roomId: '',
    roomAddress: '',
    roomName: ''
})

// 获取表单验证属性
const { validate, resetFields, validateInfos } = useForm(addModel, rules)
// 注册事件
const emit = defineEmits(['save'])

// 弹窗确认
const confirm = () => {
    validate().then(() => {
        // console.log(addModel)
        emit('save', addModel)
        onClose()
    })

}

// 显示弹窗
const show = (type: string, row: RoomModel) => {
    // 清空表单
    resetFields()
    dialog.height = 150

    if (type === EditType.ADD) {
        dialog.title = Title.ADD
    } else {
        dialog.title = Title.EDIT
        // 设置回显
        global.$objCoppy(row, addModel)
    }
    // 调用表单提交
    addModel.type = type

    onShow()

}

// 暴露
defineExpose({
    show
})

</script>
<style lang="scss" scoped></style>
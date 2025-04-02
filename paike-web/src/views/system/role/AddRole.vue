<template>
    <sys-dialog :title="dialog.title" :width="dialog.width" :height="dialog.height" :visible="dialog.visible"
        @onClose="onClose" @onConfirm="onConfirm">
        <template #content>
            <a-form>
                <a-form-item v-bind="validateInfos.roleName" :labelCol="{ style: 'width:80px' }" label="角色名称">
                    <a-input v-model:value="addModel.roleName"></a-input>
                </a-form-item>
                <a-form-item :labelCol="{ style: 'width:80px' }" label="角色描述">
                    <a-input v-model:value="addModel.roleDesc"></a-input>
                </a-form-item>
            </a-form>
        </template>
    </sys-dialog>
</template>
<script setup lang="ts">
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { EditType, Title } from '@/type/BaseEnum';
import { useForm } from 'ant-design-vue/lib/form';
import { reactive } from 'vue';
import useInstance from "@/hooks/useInstance"
import { RoleType } from '@/api/role/RoleType';
const {global} = useInstance()
// 弹窗属性
const { dialog, onClose, onShow } = useDialog()

// 注册事件
const emit = defineEmits(['save'])


// 绑定弹窗的对象
const addModel = reactive({
    type: '',
    roleId: '',
    roleName: '',
    roleDesc: '',
})

// 弹窗确认
const onConfirm = () => {
    // 验证
    validate().then(() => {
        emit('save', addModel)

        onClose()
    })

}

// 表单验证规则
const rules = reactive({
    roleName: [
        { required: true, message: '请输入角色名称', trigger: 'change' },
    ],
})
// 获取验证属性
const { validate, resetFields, validateInfos } = useForm(addModel, rules)

// 弹窗显示
const show = (type: string,row:RoleType) => {
    // 初始化表单
    resetFields()
    dialog.height = 150
    if (type == EditType.ADD) {
        dialog.title = Title.ADD

    } else {
        dialog.title = Title.EDIT
        // 把要编辑的数据赋值给弹窗对象
        global.$objCoppy(row, addModel)

    }
    // 设置type
    addModel.type = type

    // 显示弹窗
    onShow()
}

// 暴露
defineExpose({
    show,
})


</script>
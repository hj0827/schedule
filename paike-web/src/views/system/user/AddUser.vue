<template>
    <sys-dialog :title="dialog.title" :width="dialog.width" :height="dialog.height" :visible="dialog.visible"
        @onConfirm="onConfirm" @onClose="onClose">
        <template #content>
            <a-form style="margin-right: 30px;">
                <a-row>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.name" :labelCol="{ style: 'width:80px' }" label="姓名">
                            <a-input v-model:value="addModel.name" placeholder="请输入姓名" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.phone" :labelCol="{ style: 'width:80px' }" label="电话">
                            <a-input v-model:value="addModel.phone" placeholder="请输入手机号" />
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="12">
                        <a-form-item :labelCol="{ style: 'width:80px' }" label="邮箱">
                            <a-input v-model:value="addModel.email" placeholder="请输入邮箱" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.sex" :labelCol="{ style: 'width:80px' }" label="性别">
                            <a-radio-group v-model:value="addModel.sex">
                                <a-radio value="0">男</a-radio>
                                <a-radio value="1">女</a-radio>
                            </a-radio-group>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.roleId" :labelCol="{ style: 'width:80px' }" label="角色">
                            <a-select v-model:value="addModel.roleId" show-search placeholder=" 请选择角色"
                                style="width: 100%;" :options="roleList" :filter-method="filter">

                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.username" :labelCol="{ style: 'width:80px' }" label="账户">
                            <a-input v-model:value="addModel.username" placeholder="请输入登录账户" />
                        </a-form-item>
                    </a-col>
                </a-row>
                <!-- <a-row v-if="addModel.type == '0'"> -->
                <a-row>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.password" :labelCol="{ style: 'width:80px' }" label="密码">
                            <a-input v-model:value="addModel.password" placeholder="请输入密码" />
                        </a-form-item>
                    </a-col>
                </a-row>

            </a-form>
        </template>
    </sys-dialog>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { EditType, Title } from '@/type/BaseEnum';
import { UserType } from '@/api/user/UserType';
import useRole from '@/composable/user/useRole';
import { useForm } from 'ant-design-vue/lib/form';
import { addUserApi, getUserByIdApi, editUserApi } from '@/api/user/user'
import { message } from 'ant-design-vue';
import useInstance from '@/hooks/useInstance';
const { global } = useInstance()
const { roleList, filter } = useRole()
// 弹窗属性
const { dialog, onClose, onShow } = useDialog();

// 初始化 dialog.height
dialog.height = 400; // 根据实际需求设置高度

// 显示弹框
const show = async (type: string, row: UserType) => {
    // 重置表单
    await resetFields()
    // 显示弹框
    if (type == EditType.ADD) {
        dialog.title = Title.ADD
    } else {
        // 获取编辑的数据
        dialog.title = Title.EDIT
        let res = await getUserByIdApi(row.userId) as any
        if (res && res.code == 200) {
            global.$objCoppy(res.data, addModel)
        }
    }
    addModel.type = type
    onShow()
}

defineExpose({
    show
})

// 表单数据
const addModel = reactive<UserType>({
    userId: '',
    roleId: '',
    type: '',
    name: '',
    username: '',
    password: '',
    email: '',
    phone: '',
    sex: '',
})

// 表单验证
const rules = reactive({
    roleId: [{
        required: true,
        message: '请选择角色',
        trigger: 'blur'
    }],
    name: [{
        required: true,
        message: '请输入用户名',
        trigger: 'blur'
    }],
    username: [{
        required: true,
        message: '请输入登录名',
        trigger: 'blur'
    }],
    password: [{
        required: true,
        message: '请输入密码',
        trigger: 'blur'
    }],
    phone: [{
        required: true,
        message: '请输入手机号',
        trigger: 'blur'
    }],
    email: [{
        required: true,
        message: '请输入邮箱',
        trigger: 'blur'
    }],
    sex: [{
        required: true,
        message: '请选择性别',
        trigger: 'blur'
    }]
})

// 获取表单验证属性
const { validate, resetFields, validateInfos } = useForm(addModel, rules)

// 注册事件
const emit = defineEmits(['save', 'refresh'])
// 弹框确认
const onConfirm = () => {
    validate().then(async () => {
        // 发送请求
        // emit('save', addModel)
        // // 关闭弹窗
        // onClose()

        let res = null as any
        if (addModel.type == EditType.ADD) {
            res = await addUserApi(addModel)
        } else {
            res = await editUserApi(addModel)
        }
        if (res && res.code == 200) {
            message.success(res.msg)
            // 刷新列表
            emit('refresh')
            onClose()
        } else {
            message.error(res.msg)
        }
    }).catch(() => {

    })

}
</script>
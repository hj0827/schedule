<template>
    <a-modal :visible="visible" destroyOnClose title="修改密码" @cancel="onClose" :maskClosable="false">
        <template #footer>
            <a-button type="danger" @click="onClose">取消</a-button>
            <a-button type="primary" @click="onConfirm">确定</a-button>
        </template>
        <div>
            <a-form :model="formState" :rules="rules" ref="formRef">
                <a-form-item label="账号" name="username">
                    <a-input v-model:value="formState.username" disabled />
                </a-form-item>
                <a-form-item label="新密码" name="password">
                    <a-input-password v-model:value="formState.password" />
                </a-form-item>
                <a-form-item label="确认密码" name="confirmPassword">
                    <a-input-password v-model:value="formState.confirmPassword" />
                </a-form-item>
            </a-form>
        </div>
    </a-modal>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, ref, reactive } from 'vue';
import { FormInstance, message } from 'ant-design-vue';
import { clearSession, getUserName, getUserId } from '@/utils/auth';
import { updateUserApi } from '@/api/user/user';

const props = withDefaults(defineProps<{
    visible: boolean;
}>(), {
    visible: false,
});

const emit = defineEmits(['onClose', 'onConfirm']);

const formRef = ref<FormInstance>();
const formState = reactive({
    username: getUserName(), // 假设 getUserName 是获取当前用户名的方法
    password: '',
    confirmPassword: '',
    userId: getUserId(),
});

const validateConfirmPassword = (rule: any, value: string) => {
    if (value !== formState.password) {
        return Promise.reject('两次输入的密码不一致');
    }
    return Promise.resolve();
};

const rules = {
    password: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
    confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' },
    ],
};



const onClose = () => {
    emit('onClose');
};

const onConfirm = async () => { 
    try {
        await formRef.value?.validate(); 
        console.log('修改密码', formState);
        const res = await updateUserApi(formState as any) as any; 
        if(res && res.code == 200){
            message.success(res.msg);
        }
        emit('onConfirm');
    } catch (error) {
        console.log('验证失败', error);
    }
};
</script>
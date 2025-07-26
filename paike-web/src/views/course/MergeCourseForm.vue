<template>
    <!-- 灰色的线 -->
    <a-divider dashed orientation="left" style="margin-top: 0px;"></a-divider>
    <a-form labelAlign="right">
        <a-form-item v-bind="validateInfos.courseName" :labelCol="{ style: 'width:150px;' }" label="【合并】课程名称">
            <a-input v-model:value="mergeForm.courseName" placeholder="请输入课程名称"></a-input>
        </a-form-item>
        <a-row class="custom-icon">
            <a-col :span="12">
                <a-form-item v-bind="validateInfos.courseType" :labelCol="{ style: 'width:80px;' }" label="考期">
                    <a-input v-model:value="mergeForm.courseType" placeholder="请输入考期"></a-input>
                </a-form-item>
            </a-col>
            <a-col :span="12">
                <a-form-item v-bind="validateInfos.majorName" :labelCol="{ style: 'width:80px;' }" label="专业名称">
                    <a-input v-model:value="mergeForm.majorName" placeholder="请输入专业名称"></a-input>
                </a-form-item>
            </a-col>
        </a-row>
        <a-row class="custom-icon">
            <a-col :span="24">
                <a-form-item :labelCol="{ style: 'width:150px;' }" label="【合并】远智课程ID">
                    <a-input v-model:value="mergeForm.yuanzhiCourseId" placeholder="请输入远智课程ID"></a-input>
                </a-form-item>
            </a-col>
        </a-row>
        <a-row class="custom-icon">
            <!-- 取消 -->
            <a-col :span="24">
                <a-form-item>
                    <close-circle-outlined class="custom-icon-size" @click="handleClose" />
                </a-form-item>
            </a-col>
        </a-row>
    </a-form>

</template>
<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import dayjs, { Dayjs } from 'dayjs'
import { useForm } from 'ant-design-vue/lib/form';

// 年份数据
const inputYear = ref<Dayjs>()

// 定义合并课程表单的数据模型
interface MergeForm {
    courseId: string;
    courseName: string;
    majorName: string;
    courseType: string;
    courseYear: string;
    courseColor: string;
    type: string;
    isMergeClasses: string;
    yuanzhiCourseId: string;
}


// 定义 props
const props = defineProps<{
    courseColor: string;
    courseYear: string;
}>()

// 初始化合并课程表单的数据
const mergeForm = reactive<MergeForm>({
    courseId: '',
    courseName: '',
    majorName: '',
    courseType: '',
    courseYear: props.courseYear,
    courseColor: props.courseColor,
    type: '',
    isMergeClasses: '1',
    yuanzhiCourseId: ''
})

// 表单验证规则
const rules = reactive({
    courseName: [{
        required: true,
        message: '请输入课程名称',
        trigger: 'change'
    }],
    majorName: [{
        required: true,
        message: '请输入专业名称',
        trigger: 'change'
    }],
    courseType: [{
        required: true,
        message: '请输入考期',
        trigger: 'change'
    }],
    courseColor: [{
        required: true,
        message: '请选择背景颜色',
        trigger: 'change'
    }],
    courseYear: [{
        required: true,
        message: '请选择学年',
        trigger: 'change'
    }]
})

// 获取表单验证相关的属性
const { resetFields, validate, validateInfos } = useForm(mergeForm, rules)

// 监听 props 变化并更新 mergeForm
watch(() => props.courseColor, (newColor) => {
    mergeForm.courseColor = newColor;
});

watch(() => props.courseYear, (newYear) => {
    mergeForm.courseYear = newYear;
});

// 提供一个方法来获取表单数据
const getFormData = () => {
    return { ...mergeForm };
}

// 定义一个响应式变量来控制组件的显示状态
const isVisible = ref(true);

// 处理关闭事件的方法
const handleClose = () => {
    isVisible.value = false;
    // 通知父组件移除该项
    emit('remove');
}

// 定义 emit 事件
const emit = defineEmits<{
    (e: 'remove'): void;
}>();

// 暴露方法
defineExpose({
    getFormData
})
</script>

<style scoped>
.custom-icon {
    height: 35px;
}

.custom-icon-size {
    font-size: 22px !important;
    display: flex !important;
    /* 修改为 flex 以确保图标默认显示 */
    align-items: center !important;
    justify-content: center !important;
    transition: opacity 0.3s ease !important;
    /* 添加过渡效果 */
    opacity: 0.1 !important;
    /* 初始透明度 */
    margin: 0;
    padding: 0;
    color: rgb(179, 179, 179);
}

.custom-icon-size:hover {
    opacity: 0.7 !important;
    /* 鼠标悬停时的透明度 */
    color: rgb(211, 62, 62);
}
</style>
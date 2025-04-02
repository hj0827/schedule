<template>
    <sys-dialog :title="dialog.title" :height="dialog.height" :visible="dialog.visible" :width="dialog.width"
        @onClose="handleClose" @onConfirm="onConfirm">
        <template #content>
            <!-- 新增表单 -->
            <a-form labelAlign="right">
                <a-form-item v-bind="validateInfos.courseName" :labelCol="{ style: 'width:80px;' }" label="课程名称">
                    <a-input v-model:value="addModel.courseName" placeholder="请输入课程名称"></a-input>
                </a-form-item>
                <a-row>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.courseYear" :labelCol="{ style: 'width:80px;' }" label="学年">
                            <a-date-picker placeholder="请选择学年" class="date-year" v-model:value="inputYear" picker="year"
                                @openChange="openChange"></a-date-picker>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.courseType" :labelCol="{ style: 'width:80px;' }" label="考期">
                            <a-input v-model:value="addModel.courseType" placeholder="请输入考期"></a-input>
                        </a-form-item>
                    </a-col>
                </a-row>

                <!-- 颜色盘 -->
                <a-row>
                    <a-form-item v-bind="validateInfos.courseColor" :labelCol="{ style: 'width:80px;' }" label="颜色">
                        <a-row :span="12">
                            <a-col :span="6">
                                <a-input type="color" v-model:value="addModel.courseColor" placeholder="请输入颜色代码"
                                    @input="updateColor"></a-input>
                            </a-col>
                            <a-col :span="18">
                                <a-input v-model:value="addModel.courseColor" placeholder="请输入颜色代码"></a-input>
                            </a-col>
                        </a-row>
                    </a-form-item>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.majorName" :labelCol="{ style: 'width:80px;' }" label="专业名称">
                            <a-input v-model:value="addModel.majorName" placeholder="请输入专业名称"></a-input>
                        </a-form-item>
                    </a-col>
                </a-row>
                <!-- 动态渲染合并课程表单 -->
                <a-row v-for="(mergeForm, index) in mergeForms" :key="index">
                    <a-col :span="24">
                        <merge-course-form ref="mergeCourseForms" :courseColor="addModel.courseColor"
                            :courseYear="addModel.courseYear" @remove="removeMergeForm(index)"/>
                    </a-col>
                </a-row>

                <a-row>
                    <a-col :span="12" v-if="dialog.title != '编辑'">
                        <a-form-item class="icon-merge">
                            是否合并：
                            <plus-square-outlined class="icon-merge-plus" @click="MergeClass" v-model:value="addModel.isMergeClasses" />
                        </a-form-item>
                    </a-col>
                </a-row>

            </a-form>

        </template>

    </sys-dialog>
</template>
<script setup lang="ts">
import MergeCourseForm from './MergeCourseForm.vue';
import { reactive, ref } from 'vue'
// 引入弹窗组件
import SysDialog from '../../components/SysDialog.vue';
import dayjs, { Dayjs } from 'dayjs'
import { useForm } from 'ant-design-vue/lib/form';
import { CourseModel } from '../../api/course/BaseCourse';
import { EditType, Title } from '../../type/BaseEnum';
import useDialog from '../../hooks/useDialog';
// 用于回显
import useInstance from '../../hooks/useInstance';

// 定义合并课程表单的类型
interface MergeForm {
    courseId: string;
    courseName: string;
    majorName: string;
    courseType: string;
    courseYear: string;
    courseColor: string;
    type: string;
    isMergeClasses: string;
}

// 初始化 mergeForms
const mergeForms = ref<MergeForm[]>([]);
// 初始化 addModels 数组
const addModels = ref<CourseModel[]>([]);

// 在 script setup 中添加 removeMergeForm 方法
const removeMergeForm = (index: number) => {
    mergeForms.value.splice(index, 1);
    addModel.isMergeClasses = '0';
}

// 修改 MergeClass 方法
const MergeClass = () => {
    // 合并上课
    addModel.isMergeClasses = '1';
    dialog.height = 400
    mergeForms.value.push({
        courseId: '',
        courseName: '',
        majorName: '',
        courseType: '',
        courseYear: addModel.courseYear, // 使用父组件的 courseYear
        courseColor: addModel.courseColor, // 使用父组件的 courseColor
        type: '',
        isMergeClasses: ''
    });
}

// 表单绑定的数据
const addModel = reactive<CourseModel>({
    courseId: '',
    majorName: '',
    courseName: '',
    courseType: '',
    courseYear: '',
    courseColor: '#ff0000',
    type: '',
    isMergeClasses: '0'
})

// 获取全局属性
const { global } = useInstance()
interface Result<T = any> {
    code: number;
    msg: string;
    data: T;
}

// 年份数据
const inputYear = ref<Dayjs>()
// 弹窗属性
const { dialog, onShow } = useDialog()

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
const { resetFields, validate, validateInfos } = useForm(addModel, rules)
// 注册事件
const emits = defineEmits(['save'])

// 引用子组件实例
const mergeCourseForms = ref<any[]>([]);

const onConfirm = () => {
    // 提交表单
    validate().then(async () => {
        console.log("父级组件 AddCourse.vue 的数据:");
        console.log("addModel:", addModel);

        // 获取子组件数据
        const mergeFormData = mergeCourseForms.value.map(form => form.getFormData());
        console.log("mergeForms:", mergeFormData);

        // 将 addModel 转换为数组
        const addModelArray = [{ ...addModel }];

        // 将子组件数据与父组件数据合并
        const payload = [...addModelArray, ...mergeFormData];

        console.log("合并后的数据:", payload);

        // 发送 payload
        emits('save', payload);

        // 将父组件的数据添加进 addModels
        addModels.value.push({ ...addModel });

        // 清空 mergeForms
        mergeForms.value = [];

        // 关闭弹窗
        handleClose();
    }).catch(error => {
        console.error("表单验证失败:", error);
    });
}

const handleClose = () => {
    // 关闭弹窗
    dialog.visible = false;
    // 清空 mergeForms
    mergeForms.value = [];
    dialog.height = 250
}

// 日历事件
const openChange = () => {
    addModel.courseYear = dayjs(inputYear.value).format('YYYY')
}

// 更新颜色
const updateColor = (event: Event) => {
    const target = event.target as HTMLInputElement;
    addModel.courseColor = target.value;
}

// 显示弹窗 
const show = (type: string, row: CourseModel) => {
    // 清空表单
    resetFields()
    // 判断是新增还是编辑
    if (type === EditType.ADD) {
        inputYear.value = undefined;
        dialog.title = Title.ADD
    } else {
        dialog.title = Title.EDIT
        // 编辑回显，把当前编辑的数据，设置到表单绑定的数据对象addModel中
        global.$objCoppy(row, addModel)
        // 设置回显时间
        inputYear.value = dayjs(row.courseYear)
    }
    // 设置编辑的类型
    addModel.type = type
    // 弹窗显示
    onShow();
}

// 暴露出方法
defineExpose({
    show
})

</script>
<style lang="scss" scoped>
.date-year {
    width: 100%;
}

.color-preview {
    width: 30px;
    height: 30px;
    border: 1px solid #000;
    // margin-top: 10px;
}

.icon-merge{
    height: 40px;
    display: flex;
    // justify-content: center;
    align-items: center;
}

.icon-merge-plus{
    font-size: 20px !important;
    opacity: 0.7 !important;
    color: #0f0f0f!important;
}

</style>
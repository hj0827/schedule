<template>
    <sys-dialog :title="dialog.title" :height="dialog.height" :width="dialog.width" :visible="dialog.visible"
        @onClose="onClose" @onConfirm="onConfirm" :destroyOnClose="true">
        <template #content>
            <a-form :model="addParm" :rules="rules" ref="formRef">
                <a-row>
                    <a-col :span="24">
                        <a-form-item label="课程名称" name="courseName">
                            <a-input v-model:value="addParm.courseName" placeholder="请输入课程名称" disabled></a-input>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.roomId" :labelCol="{ style: 'width:80px;' }" label="教室">
                            <a-select v-model:value="addParm.roomId" show-search placeholder="请选择教室"
                                style="width: 200px;" :options="roomOptions" :filter-option="filterRoomOption">
                            </a-select>
                        </a-form-item>
                    </a-col>

                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.teacherId" :labelCol="{ style: 'width:80px;' }" label="教师">
                            <a-select v-model:value="addParm.teacherId" show-search placeholder="请选择教师"
                                style="width: 200px;" :options="teacherOptions" :filter-option="filterTeacheroption">
                            </a-select>
                        </a-form-item>
                    </a-col>

                </a-row>
                <a-row>
                    <a-col :span="12">
                        <a-form-item label="考期" name="courseType" :labelCol="{ style: 'width:80px;' }">
                            <a-input style="width: 200px;" v-model:value="addParm.courseType" disabled
                                placeholder="请输入考期"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="上课日期" name="dateTime" :labelCol="{ style: 'width:80px;' }">
                            <a-date-picker style="width: 200px;" v-model:value="addParm.dateTime" format="YYYY-MM-DD"
                                value-format="YYYY-MM-DD" placeholder="请选择上课日期" show-time-picker="false" />
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-row>
                    <a-col :span="8">
                        <a-form-item v-bind="validateInfos.beginTime" label="上课时间">
                            <a-time-picker :minuteStep="5" @change="beginTimeChange" v-model:value="beginTime1"
                                format="HH:mm"></a-time-picker>
                        </a-form-item>
                    </a-col>
                    <a-col :span="8">
                        <a-form-item label="课程时长">
                            <a-input-number v-model:value="inputNumber" @change="getEndTime"></a-input-number>
                        </a-form-item>
                    </a-col>
                    <a-col :span="8">
                        <a-form-item label="下课时间">
                            <a-time-picker v-model:value="endTime1" format="HH:mm"
                                @change="endTimeChange"></a-time-picker>
                        </a-form-item>
                    </a-col>
                </a-row>
            </a-form>
        </template>
    </sys-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import SysDialog from '../../components/SysDialog.vue';
import useDialog from '../../hooks/useDialog';
import dayjs, { Dayjs } from 'dayjs'
import { message } from 'ant-design-vue';
import { useForm } from 'ant-design-vue/lib/form';
import useselect from '../../composable/schedule/useSelect';
import {updateCalendarListApi,updateCalendarApi} from '../../api/schedule/schedule'
import useTable from '@/composable/schedule/useTable';

const { rolePage, tableList, columns, tableHeight, listParm, getList, loading } = useTable();

const { roomOptions, teacherOptions, courseOptions, courseOptionsPaike, filterCourseOption, filterRoomOption, filterTeacheroption } = useselect(getList,listParm)
// 获取弹窗属性
const { dialog, onShow, onClose } = useDialog()

// 定义弹窗显示的方法
const show = (type: string, row?: any) => {
    // 设置弹窗属性
    dialog.height = 300;
    dialog.width = 700;
    // 显示弹窗
    onShow();
    dialog.title = '编辑'

    // 如果是编辑，则填充表单数据
    if (row) {
        Object.assign(addParm, row);
        beginTime1.value = dayjs(row.beginTime, "HH:mm");
        inputNumber.value = row.duration;
        endTime1.value = dayjs(row.endTime, "HH:mm");
    }
}

defineExpose({
    show
})

// 表单绑定的对象
const addParm = reactive({
    id: 0,
    roomId: 0,
    courseId: '',
    teacherId: '',
    dateTime: '',
    beginTime: '',
    duration: 0,
    endTime: '',

    courseType: '',
    majorName: '',
    courseYear: '',
    teacherName: '',
    courseName: '',
    roomName: '',
    courseColor: '',
    roomAddress: '',
    startDate: '', // 开课日期
    endDate: '', // 结课日期
    delId: 0,
})

// 表单验证
const rules = reactive({
    roomId: [{
        required: true,
        message: '请选择教室',
        trigger: 'change'
    }],
    teacherId: [{
        required: true,
        message: '请选择教师',
        trigger: 'change'
    }],
    courseId: [{
        required: true,
        message: '请选择课程',
        trigger: 'change'
    }],
    dateTime: [{
        required: true,
        message: '请选择上课日期',
        trigger: 'change'
    }],
    duration: [{
        required: true,
        type: 'number',
        min: 1,
        message: '请输入有效的课程时长',
        trigger: 'change'
    }]
})
const { resetFields, validate, validateInfos } = useForm(addParm, rules);
// 上课日期选择
const selectTime = ref<Dayjs>()
// 上课时间点击事件
const beginTimeChange = () => {
    getEndTime()
}
// 下课时间
const endTime1 = ref<Dayjs>()
// 上课时间
const beginTime1 = ref<Dayjs>(dayjs('08:00', 'HH:mm'))
// 课程时长
const inputNumber = ref<number>()

// 下课时间变化事件处理函数
const endTimeChange = (time: Dayjs | null) => {
    if (time && beginTime1.value) {
        const beginTime = dayjs(beginTime1.value, "HH:mm");
        const endTime = dayjs(time, "HH:mm");
        if (endTime.isBefore(beginTime)) {
            message.error('下课时间不能早于上课时间');
            endTime1.value = beginTime.add(inputNumber.value || 0, 'minute'); // 设置下课时间为上课时间加上上课时长
        } else {
            const durationInMinutes = endTime.diff(beginTime, 'minutes');
            inputNumber.value = durationInMinutes;
        }
    }
};
// 设置时间
const getEndTime = () => {
    const duration = inputNumber.value ?? 0; // 使用默认值 0
    endTime1.value = dayjs(beginTime1.value, "HH:mm").add(duration, 'minute');
    addParm.beginTime = dayjs(beginTime1.value, "HH:mm").format('HH:mm');
    addParm.endTime = dayjs(endTime1.value, "HH:mm").format('HH:mm');
    addParm.duration = duration;
}
// 注册事件
const emit = defineEmits(['refreshList'])

// 弹窗确认
const onConfirm = async () => {
    try {
        // 表单验证
        await validate();
        
        // 确保时间数据正确
        if (beginTime1.value && endTime1.value) {
            addParm.beginTime = beginTime1.value.format('HH:mm');
            addParm.endTime = endTime1.value.format('HH:mm');
            addParm.duration = inputNumber.value || 0;
        }

        // 发送请求到服务器
        // Create a payload with only the necessary fields for the backend
        const updatePayload = {
            id: addParm.id,
            roomId: addParm.roomId,
            teacherId: addParm.teacherId,
            dateTime: addParm.dateTime,
            beginTime: addParm.beginTime,
            duration: addParm.duration,
            endTime: addParm.endTime,
            // Include fields from the database schema that are part of the record structure,
            // even if not directly editable in this dialog.
            courseId: addParm.courseId, // course_id exists in db
            courseType: addParm.courseType, // course_type exists in db
            delId: addParm.delId, // del_id exists in db
        };

        // Send update request for the parent/single record
        const res = await updateCalendarApi(updatePayload) as { code: number; msg: string; };

        if (res && res.code === 200) {
            message.success(res.msg || '编辑成功');
            // 关闭弹窗
            onClose();
            // 刷新列表
            emit('refreshList');
        } else {
            message.error(res.msg || '编辑失败');
        }
    } catch (error: any) {
        console.error('保存失败:', error);
        if (error.response?.data?.msg) {
            message.error(error.response.data.msg);
        } else if (error.errorFields) {
            message.error('请检查表单填写是否完整');
        } else {
            message.error('保存失败，请稍后重试');
        }
    }
};

const openChange = (data: string, dateString: string) => {
    addParm.startDate = dateString[0]
    addParm.endDate = dateString[1]
}

const formRef = ref();

onMounted(() => {

})
</script>
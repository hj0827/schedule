<template>
    <SysDialog :title="dialog.title" :width="dialog.width" :height="dialog.height" :visible="dialog.visible"
        @onClose="onClose" @onConfirm="onConfirm" @onDelete="onDelete" :mode="editType" :destroyOnClose="true">
        <template #content>
            <a-form style="margin-right: 30px;">   
             <a-row>
                    <a-col :span="24">
                        <a-form-item v-bind="validateInfos.courseId" :labelCol="{ style: 'width:80px;' }" label="课程">
                            <a-select v-model:value="addModel.courseId" show-search placeholder="请选择课程"
                                style="width: 100%;" :options="courseOptions"
                                :filter-option="filterCourseOption"></a-select>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row v-if="editType === 'edit'">
                    <a-col :span="24">
                        <a-form-item :labelCol="{ style: 'width:80px;' }" label="课时">
                            <a-input v-model:value="addModel.lessonNumber" disabled></a-input>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.dateTime" :labelCol="{ style: 'width:80px;' }" label="日期">
                            <a-date-picker style="width: 100%;" format="YYYY-MM-DD" @change="openChange"
                                v-model:value="dataTime" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.roomId" :labelCol="{ style: 'width:80px;' }" label="教室">
                            <a-select v-model:value="addModel.roomId" show-search placeholder="请选择教室"
                                style="width: 100%;" :options="roomOptions" :filter-option="filterRoomOption">
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.teacherId" :labelCol="{ style: 'width:80px;' }" label="教师">
                            <a-select v-model:value="addModel.teacherId" show-search placeholder="请选择教师"
                                style="width: 100%;" :options="teacherOptions" :filter-option="filterTeacheroption">
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12" v-if="editType === 'edit'">
                        <a-form-item v-bind="validateInfos.course_type" :labelCol="{ style: 'width:80px;' }" label="考期">
                            <a-input v-model:value="addModel.courseType" disabled></a-input>
                        </a-form-item>
                    </a-col>

                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.beginTime" :labelCol="{ style: 'width:80px;' }" label="上课时间">
                            <a-time-picker style="width: 100%;" :minuteStep="5" @change="beginTimeChange"
                                v-model:value="beginTime1" format="HH:mm"></a-time-picker>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item v-bind="validateInfos.duration" :labelCol="{ style: 'width:80px;' }" label="课程时长">
                            <a-input-number style="width: 100%;" v-model:value="addModel.duration"></a-input-number>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="下课时间" :labelCol="{ style: 'width:80px;' }">
                            <a-time-picker style="width: 100%;" :minuteStep="5" v-model:value="endTime1" format="HH:mm"
                                @change="endTimeChange"></a-time-picker>
                        </a-form-item>
                    </a-col>
                </a-row>
            </a-form>
        </template>
    </SysDialog>
</template>

<script setup lang="ts">
// 引入弹窗组件
import { reactive, ref, watch } from 'vue'
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { EditModel } from '@/api/schedule/ScheduleType';
import { EditType, Title } from '@/type/BaseEnum';
import useselect from '../../composable/schedule/useSelect';
import dayjs, { Dayjs } from 'dayjs'
import useInstance from '@/hooks/useInstance';
import { addCalendarApi, updateCalendarApi, deleteCalendarApi } from '@/api/schedule/schedule'
import { useForm } from 'ant-design-vue/lib/form';
import { Modal, message } from 'ant-design-vue';
const { global } = useInstance()
// 弹窗属性
const { dialog, onClose, onShow } = useDialog()
import useTable from '@/composable/schedule/useTable';

const { rolePage, tableList, columns, tableHeight, listParm, getList } = useTable();
//  下拉属性
const { roomOptions, teacherOptions, courseOptions, filterCourseOption, filterRoomOption, filterTeacheroption } = useselect(getList,listParm)

// 设置编辑或新增
const editType = ref<string>('add')
// 定义方法给外部组件使用
const editCalender = (type: string, data: EditModel) => {
    // 清空表单
    resetFields()
    dataTime.value = undefined;
    beginTime1.value = undefined;
    endTime1.value = undefined;

    dialog.width = 700
    dialog.height = 350
    if (type === EditType.ADD) {
        dialog.title = Title.ADD
        editType.value = 'add'
        // 设置新增时的默认值
        addModel.duration = 60 // 默认时长60分钟
        if (data?.dateTime) {
            dataTime.value = dayjs(data.dateTime)
            addModel.dateTime = data.dateTime
        }
        if (data?.beginTime) {
            beginTime1.value = dayjs(data.beginTime, 'HH:mm')
            addModel.beginTime = data.beginTime
            // 如果有开始时间，自动计算结束时间
            if (beginTime1.value) {
                endTime1.value = dayjs(beginTime1.value).add(60, 'minute')
                addModel.endTime = endTime1.value.format('HH:mm')
            }
        }
    } else {
        dialog.title = Title.EDIT
        editType.value = 'edit'
        // 编辑数据回显
        global.$objCoppy(data, addModel)
        dataTime.value = dayjs(data?.dateTime, 'YYYY-MM-DD')
        beginTime1.value = dayjs(data?.beginTime, 'HH:mm')
        endTime1.value = dayjs(data?.endTime, 'HH:mm')
    }
    onShow()
}

// 表单绑定属性
const addModel = reactive({
    id: '',
    dateTime: '',
    roomId: '',
    teacherId: '',
    courseId: '',
    beginTime: '',
    endTime: '',
    duration: 0,
    courseType: "",
    lessonNumber: "",
})


// 表单验证
const rules = reactive({
    dateTime: [{
        required: true,
        message: '请选择日期',
        trigger: 'change'
    }],
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
    duration: [{
        required: true,
        message: '请选择课程时长',
        trigger: 'change'
    }],
    beginTime: [{
        required: true,
        message: '请选择上课时间',
        trigger: 'change'
    }]
})

const { resetFields, validate, validateInfos } = useForm(addModel, rules);

// 日期数据
const dataTime = ref<Dayjs>()
// 日期选择事件
const openChange = (date: string, dateString: string) => {
    console.log(date)
    console.log(dateString)

    addModel.dateTime = dateString
}


// 上课时间
const beginTime1 = ref<Dayjs>()

// 上课时间点击事件
const beginTimeChange = () => {
    if (addModel.duration) {
        getEndTime();
    }
    // 确保下课时间不早于上课时间
    if (endTime1.value && beginTime1.value && endTime1.value.isBefore(beginTime1.value)) {
        endTime1.value = dayjs(beginTime1.value).add(addModel.duration, 'minute');
        addModel.endTime = endTime1.value.format('HH:mm');
    }
}


// 课程时长
const inputNumber = ref<number>(0)

// 下课时间
const endTime1 = ref<Dayjs>();

// 设置时间
const getEndTime = () => {
    if (beginTime1.value && addModel.duration) {
        endTime1.value = dayjs(beginTime1.value).add(addModel.duration, 'minute');
        addModel.endTime = endTime1.value.format('HH:mm');
    }
};

// 暴露给外部使用
defineExpose({
    editCalender
})

// 注册事件
const emit = defineEmits(['upSuccess'])

// 监听 duration 变化
watch(
  () => addModel.duration,
  (newDuration) => {
    if (beginTime1.value && newDuration) {
      endTime1.value = dayjs(beginTime1.value).add(newDuration, 'minute');
      addModel.endTime = endTime1.value.format('HH:mm');
    }
  }
);

// 监听 endTime1 变化
watch(
  endTime1,
  (newEndTime) => {
    if (beginTime1.value && newEndTime) {
      const durationInMinutes = newEndTime.diff(beginTime1.value, 'minute');
      addModel.duration = durationInMinutes;
    }
  }
);


// 监听 beginTime1 变化
watch(
  beginTime1,
  (newBeginTime) => {
    if (newBeginTime) {
      addModel.beginTime = newBeginTime.format('HH:mm');
      // 确保下课时间不早于上课时间
      if (endTime1.value && newBeginTime.isAfter(endTime1.value)) {
        endTime1.value = dayjs(newBeginTime).add(addModel.duration, 'minute');
        addModel.endTime = endTime1.value.format('HH:mm');
      }
      // 如果有课程时长，重新计算下课时间
      if (addModel.duration) {
        getEndTime();
      }
    }
  }
);

// 下课时间变化事件
const endTimeChange = () => {
    if (beginTime1.value && endTime1.value) {
        const durationInMinutes = endTime1.value.diff(beginTime1.value, 'minute');
        addModel.duration = durationInMinutes;
    }
};

// 定义API响应类型
interface ApiResponse {
    code: number;
    msg?: string;
    data?: any;
}

// 弹窗确认
const onConfirm = () => {
    validate().then(async () => {
        try {
            let res: ApiResponse | null = null;
            if (editType.value === 'add') {
                // 新增模式：直接调用新增API
                res = await addCalendarApi(addModel as any) as ApiResponse;
                if (res && res.code == 200) {
                    message.success('新增成功');
                    emit('upSuccess');
                    onClose();
                } else {
                    message.error(res?.msg || '新增失败');
                }
            } else {
                // 编辑模式：显示确认对话框
                Modal.confirm({
                    title: '确认修改',
                    content: '确定要修改这条课程排程吗？',
                    okText: '是',
                    cancelText: '否',
                    async onOk() {
                        res = await updateCalendarApi(addModel as any) as ApiResponse;
                        if (res && res.code == 200) {
                            message.success('修改成功');
                            emit('upSuccess');
                            onClose();
                        } else {
                            message.error(res?.msg || '修改失败');
                        }
                    }
                });
            }
        } catch (error) {
            console.error('操作失败', error);
            message.error('请求出错，请重试');
        }
    })
}

// 删除事件
const onDelete = () => {
    Modal.confirm({
        title: '确认删除',
        content: '确定要删除这条课程排程吗？',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        async onOk() {
            try {
                const res = await deleteCalendarApi(String(addModel.id)) as ApiResponse;
                if (res && res.code === 200) {
                    message.success('删除成功');
                    emit('upSuccess');
                    onClose();
                } else {
                    message.error(res.msg || '删除失败');
                }
            } catch (error) {
                console.error('删除课程排程失败', error);
                message.error('请求出错，请重试');
            }
        }
    });
};
</script>
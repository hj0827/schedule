<template>
    <SysDialog :title="dialog.title" :width="dialog.width" :height="dialog.height" :visible="dialog.visible"
        @onClose="onClose" @onConfirm="onConfirm" :destroyOnClose="true">
        <template #content>
            编辑
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
                    <a-col :span="12">
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
import { addCalendarApi, updateCalendarApi } from '@/api/schedule/schedule'
import { useForm } from 'ant-design-vue/lib/form';
const { global } = useInstance()
// 弹窗属性
const { dialog, onClose, onShow } = useDialog()
import useTable from '@/composable/schedule/useTable';

const { rolePage, tableList, columns, tableHeight, listParm, getList } = useTable();
//  下拉属性
const { roomOptions, teacherOptions, courseOptions, filterCourseOption, filterRoomOption, filterTeacheroption } = useselect(getList,listParm)

// 设置编辑或新增
const editType = ref('')
// 定义方法给外部组件使用
const editCalender = (type: string, data: EditModel) => {
    // console.log(type)
    // console.log(data)
    // 清空表单
    resetFields()
    dataTime.value = undefined;
    beginTime1.value = undefined;
    endTime1.value = undefined;

    dialog.width = 700
    dialog.height = 350
    if (type == EditType.ADD) {
        dialog.title = Title.ADD
    } else {
        dialog.title = Title.EDIT
        // 编辑数据回显
        global.$objCoppy(data, addModel)
        dataTime.value = dayjs(data?.dateTime, 'YYYY-MM-DD')
        beginTime1.value = dayjs(data?.beginTime, 'HH:mm')
        endTime1.value = dayjs(data?.endTime, 'HH:mm')
    }
    editType.value = type
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

// 弹窗确认
const onConfirm = () => {
    validate().then(async () => {
        let res = null as any;
        if (editType.value == EditType.ADD) {
            res = await addCalendarApi(addModel as any)
        } else {
            // res = await updateCalendarApi(addModel)
            res = await updateCalendarApi(addModel as any)
        }
        if (res && res.code == 200) {
            // 刷新表格数据
            emit('upSuccess')
            onClose()
        }
    })
}

</script>
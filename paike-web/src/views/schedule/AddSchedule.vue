<template>
    <sys-dialog :title="dialog.title" :height="dialog.height" :width="dialog.width" :visible="dialog.visible"
        @onClose="onClose" @onConfirm="onConfirm" :destroyOnClose="true">
        <template #content>
            <!-- 加载动画 -->
            <loading-overlay :visible="loading" />
            <a-form>
                <div class="form-item-margin">
                    <div class="left-part">

                        <a-row>
                            <a-col :span="24">
                                <a-form-item v-bind="validateInfos.courseId" label="课程">
                                    <a-select v-model:value="addParm.courseId" show-search placeholder="请选择课程"
                                        style="width: 100%;" :options="courseOptionsPaike"
                                        :filter-option="filterCourseOption" allowClear @change="handleCourseChange">
                                        <template #option="{ courseType, label, isMergeClasses }">
                                            <a-tag v-if="isMergeClasses == 1" color="red">合并</a-tag>
                                            <span>{{ label }}</span>
                                            <!-- <span>{{ courseType }}</span> -->
                                        </template>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row>
                            <a-col :span="12">
                                <a-form-item v-bind="validateInfos.roomId" :labelCol="{ style: 'width:80px;' }"
                                    label="教室">
                                    <a-select v-model:value="addParm.roomId" show-search placeholder="请选择教室"
                                        style="width: 200px;" :options="roomOptions" :filter-option="filterRoomOption">
                                    </a-select>
                                </a-form-item>
                            </a-col>

                            <a-col :span="12">
                                <a-form-item v-bind="validateInfos.teacherId" :labelCol="{ style: 'width:80px;' }"
                                    label="教师">
                                    <a-select v-model:value="addParm.teacherId" show-search placeholder="请选择教师"
                                        style="width: 200px;" :options="teacherOptions"
                                        :filter-option="filterTeacheroption">
                                    </a-select>
                                </a-form-item>
                            </a-col>
                        </a-row>

                        <a-form-item v-bind="validateInfos.weeks" :labelCol="{ style: 'width:80px;' }" label="星期">
                            <a-checkbox-group :options="weekOptions" v-model:value="addParm.weeks"></a-checkbox-group>
                        </a-form-item>
                        <a-form-item v-bind="validateInfos.startDate" :labelCol="{ style: 'width:80px;' }" label="上课日期">
                            <a-range-picker v-model:value="selectTime" format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']" @change="openChange"></a-range-picker>
                        </a-form-item>
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
                    </div>
                    <div class="right-part">
                        <!-- 展示课程排程数据 -->
                        <div v-if="courseScheduleData.length > 0">
                            <h3>该课程已排时间</h3>
                            <ul>
                                <li v-for="(schedule, index) in courseScheduleData" :key="index">
                                    课时{{ (index + 1).toString().padStart(2, '0') }}：{{ schedule.dateTime }}【{{
                                    schedule.beginTime.substring(0, 5) }} ~
                                    {{ schedule.endTime.substring(0, 5) }}】
                                    <!-- {{ schedule.id }} -->
                                    <span type="" @click="deleteSchedule(schedule.id)">x</span>
                                </li>
                            </ul>
                        </div>
                        <div v-else>
                            <p>暂无课程排程信息</p>
                        </div>
                    </div>
                </div>
            </a-form>
        </template>
    </sys-dialog>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import loadingOverlay from '@/components/loadingOverlay.vue';
import SysDialog from '../../components/SysDialog.vue';
import useDialog from '../../hooks/useDialog';
import useselect from '../../composable/schedule/useSelect';
import dayjs, { Dayjs } from 'dayjs'
import { saveScheduleApi, getScheduleListApi, deleteCalendarListApi } from '../../api/schedule/schedule'
import { message } from 'ant-design-vue';
import { useForm } from 'ant-design-vue/lib/form';
import useTable from '@/composable/schedule/useTable';

const { rolePage, tableList, columns, tableHeight, listParm, getList } = useTable();
//  下拉属性
const { roomOptions, teacherOptions, courseOptions, courseOptionsPaike, filterCourseOption, filterRoomOption, filterTeacheroption } = useselect(getList, listParm)
// 获取弹窗属性
const { dialog, onShow, onClose } = useDialog()

// 定义弹窗显示的方法
const show = () => {
    resetFields()
    selectTime.value = undefined
    addParm.beginTime = dayjs(beginTime1.value, "HH:mm").format('HH:mm')
    addParm.endTime = dayjs(endTime1.value, "HH:mm").format('HH:mm')
    addParm.duration = inputNumber.value
    // 设置弹窗属性
    dialog.title = '排课'
    dialog.height = 500
    dialog.width = 1000
    // 显示弹窗
    onShow()
}

defineExpose({
    show
})

// 表单绑定的对象
const addParm = reactive({
    courseId: '',
    teacherId: '',
    roomId: '',
    startDate: '', // 开课日期
    endDate: '', // 结课日期
    weeks: [1, 2, 3, 4, 5, 6, 7],
    beginTime: '',//上课时间
    endTime: '', // 下课时间
    duration: 0,
    isMergeClasses: 0 // 是否合并课程
})

// 表单验证
const rules = reactive({
    weeks: [{
        required: true,
        message: '请选择星期几',
        trigger: 'change'

    }],
    startDate: [{
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

const { resetFields, validate, validateInfos } = useForm(addParm, rules);

// 上课日期选择
const selectTime = ref<Dayjs>()
// 日期选择事件回调
const openChange = (data: string, dateString: string) => {
    addParm.startDate = dateString[0]
    addParm.endDate = dateString[1]
}

const weekOptions = [
    { label: '星期一', value: 1 },
    { label: '星期二', value: 2 },
    { label: '星期三', value: 3 },
    { label: '星期四', value: 4 },
    { label: '星期五', value: 5 },
    { label: '星期六', value: 6 },
    { label: '星期日', value: 7 }
]

// 上课时间
const beginTime1 = ref<Dayjs>(dayjs('08:00', 'HH:mm'))

// 上课时间点击事件
const beginTimeChange = () => {
    getEndTime()
}

// 课程时长
const inputNumber = ref<number>(60)

// 下课时间
const endTime1 = ref<Dayjs>()


// 下课时间变化事件处理函数
const endTimeChange = (time: Dayjs | null) => {
    if (time && beginTime1.value) {
        const beginTime = dayjs(beginTime1.value, "HH:mm");
        const endTime = dayjs(time, "HH:mm");
        if (endTime.isBefore(beginTime)) {
            message.error('下课时间不能早于上课时间');
            endTime1.value = beginTime.add(inputNumber.value, 'minute'); // 设置下课时间为上课时间加上上课时长
        } else {
            const durationInMinutes = endTime.diff(beginTime, 'minutes');
            inputNumber.value = durationInMinutes;
        }
    }
};

// 设置时间
const getEndTime = () => {
    endTime1.value = dayjs(beginTime1.value, "HH:mm").add(inputNumber.value, 'minute')
    addParm.beginTime = dayjs(beginTime1.value, "HH:mm").format('HH:mm')
    addParm.endTime = dayjs(endTime1.value, "HH:mm").format('HH:mm')
    addParm.duration = inputNumber.value
}

// 注册事件
const emit = defineEmits(['refreshList'])
const loading = ref(false)

// 添加一个响应式变量来存储 handleCourseChange 的结果
const courseScheduleData = ref<any[]>([]);

// 修改 handleCourseChange 函数
const handleCourseChange = async (params: any) => {
    console.log(`selected ${params}`);
    listParm.courseId = params;
    try {
        if (params !== '' && params !== null && params !== undefined) {
            const res = await getScheduleListApi(listParm) as any;
            if (res && res.code === 200) {
                // 将查询到的数据赋值给 courseScheduleData
                courseScheduleData.value = res.data;
            }
        } else {
            courseScheduleData.value = [];
        }
    } catch (error) {
        console.error('获取课程排程信息失败', error);
    }
};
const onConfirm = async () => {
    validate().then(async () => {
        try {
            loading.value = true; // 请求开始前设置 loading 为 true
            const res = await saveScheduleApi(addParm) as any;
            if (res && res.code == 200) {
                emit('refreshList');
                message.success(res.msg);
                // onClose();
                if (addParm.courseId !== null && addParm.courseId !== '') {
                    await handleCourseChange(addParm.courseId);
                }

            } else {
                // message.error(res.msg || '保存失败');
            }
        } catch (err) {
            console.log(err);
            message.error('请求出错，请重试');
        } finally {
            loading.value = false; // 请求结束后设置 loading 为 false
        }
    }).catch((err) => {
        console.log(err);
        message.error('表单验证失败');
    });
}

// deleteSchedule
const deleteSchedule = async (id: number) => {
    console.log("点击了" + id)

    let res = await deleteCalendarListApi(id) as any
}



// 弹窗取消
onMounted(() => {
    getEndTime()
})

</script>

<style scoped>
.form-item-margin {
    display: flex;
    justify-content: space-between;
    /* 或者使用 space-around, space-evenly 根据需要 */
}

/* .left-part, .right-part {
  flex: 1; 
  margin: 0 10px; 
}  */

.right-part {
    width: 300px;
    border-left: 1px solid #ccc;
    padding-left: 5px;
}

ul {
    list-style-type: none;
    /* 移除项目符号 */
    padding: 0;
    /* 移除内边距 */
    margin: 0;
    /* 移除外边距 */
}

li {
    list-style-type: none;
    /* 确保项目符号被移除 */
    margin: 0;
    /* 移除外边距 */
    padding: 0;
    /* 移除内边距 */
}
</style>

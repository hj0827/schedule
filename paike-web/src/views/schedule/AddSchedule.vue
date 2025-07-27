<template>
    <sys-dialog :title="dialog.title" :height="dialog.height" :width="dialog.width" :visible="dialog.visible"
        @onClose="onClose" @onConfirm="onConfirm" :destroyOnClose="true" :top="10">
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
                                        <template #option="{ value: courseId, courseType, label, isMergeClasses }">
                                            <div @mouseenter="onCourseMouseEnter(courseId)" class="course-option">
                                                <a-tag v-if="isMergeClasses == 1" color="red">合并</a-tag>
                                                <span>{{ label }}</span>
                                                <a-tooltip v-if="isMergeClasses == 1" placement="right">
                                                    <template #title>
                                                        <div><strong>合并课程考期：</strong></div>
                                                        <div v-for="(item, index) in hoveredCourseTerms" :key="index"
                                                            class="term-list-item">
                                                            {{ item.courseType }}
                                                        </div>
                                                        <div
                                                            v-if="!hoveredCourseTerms || hoveredCourseTerms.length === 0">
                                                            正在加载考期信息...</div>
                                                    </template>
                                                    <info-circle-outlined style="margin-left: 5px; color: #1890ff;" />
                                                </a-tooltip>
                                            </div>
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
                                        style="width: 200px;" :options="roomOptions" allowClear
                                        :filter-option="filterRoomOption" @change="handleClassRoomChange">
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :span="12">
                                <a-form-item v-bind="validateInfos.teacherId" :labelCol="{ style: 'width:80px;' }"
                                    label="教师">
                                    <a-select v-model:value="addParm.teacherId" show-search placeholder="请选择教师"
                                        style="width: 200px;" :options="teacherOptions" allowClear
                                        :filter-option="filterTeacheroption" @change="handleTeaChange">
                                    </a-select>
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-form-item v-bind="validateInfos.weeks" :labelCol="{ style: 'width:80px;' }" label="星期">
                            <a-checkbox-group :options="weekOptions" v-model:value="addParm.weeks"></a-checkbox-group>
                        </a-form-item>
                        <a-form-item label="时间段组" :labelCol="{ style: 'width:80px;' }">
                            <a-radio-group v-model:value="selectedTimeGroup" @change="handleTimeGroupChange">
                                <!-- <a-radio :value="1">全天班</a-radio> -->
                                <a-radio :value="2">周一至周五晚班</a-radio>
                                <a-radio :value="3">周末班</a-radio>
                            </a-radio-group>
                        </a-form-item>
                        <!-- Add optional stage name field -->
                        <a-row>
                            <a-col :span="24">
                                <a-form-item label="阶段名称" :labelCol="{ style: 'width:80px;' }">
                                    <a-select v-model:value="addParm.stageId" placeholder="请选择阶段 (可选)"
                                        style="width: 200px;" allowClear>
                                        <!-- Dynamically generated options from backend data -->
                                        <a-select-option v-for="stage in stageOptions" :key="stage.id"
                                            :value="stage.id">
                                            {{ stage.stageName }}
                                        </a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-form-item v-bind="validateInfos.startDate" :labelCol="{ style: 'width:80px;' }" label="上课日期">
                            <a-range-picker v-model:value="selectTime" format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']" @change="openChange"></a-range-picker>
                        </a-form-item>
                        <a-row>
                            <a-col>
                                <a-form-item v-bind="validateInfos.beginTime" label="上课时间">
                                    <a-time-picker :minuteStep="5" @change="beginTimeChange" v-model:value="beginTime1"
                                        format="HH:mm"></a-time-picker>
                                </a-form-item>
                            </a-col>
                            <a-col style="margin-left: 5px;">
                                <a-form-item label="课程时长">
                                    <a-input-number v-model:value="inputNumber" @change="getEndTime"></a-input-number>
                                </a-form-item>
                            </a-col>
                            <a-col style="margin-left: 5px;">
                                <a-form-item label="下课时间">
                                    <a-time-picker v-model:value="endTime1" format="HH:mm"
                                        @change="endTimeChange"></a-time-picker>
                                </a-form-item>
                            </a-col>
                            <a-col style="margin-left: 5px;">
                                <a-button type="primary" @click="addSchedule">添加时间段</a-button>
                            </a-col>
                        </a-row>
                        <!-- <a-row>
                            <a-col :span="24">
                                <a-button type="primary" @click="addSchedule">添加时间段</a-button>
                            </a-col>
                        </a-row> -->
                        <a-row>
                            <div class="schedules-tag-total" style="max-width: 700px;">
                                <a-col :span="24">
                                    <div v-for="(schedule, index) in addParm.schedules" :key="index"
                                        class="schedule-tag">
                                        {{ schedule.beginTime }} ~ {{ schedule.endTime }}
                                        <span class="delete-btn" @click="removeSchedule(index)">x</span>
                                    </div>
                                </a-col>
                            </div>
                        </a-row>

                    </div>
                    <div class="schedule-list">
                        <div class="fixed-header">
                            <div class="header-title-row">
                                <h3>推荐课时</h3>
                                <div class="header-actions">
                                    <a-tooltip placement="top" title="复制全部推荐课时">
                                        <copy-outlined class="copy-all-icon" @click="copyAllRecommendedSchedules"
                                            :style="{ color: filteredRecommendedSchedules.length > 0 ? '#1890ff' : '#d9d9d9' }" />
                                    </a-tooltip>
                                </div>
                            </div>
                            <div class="schedule-tip">
                                <a-tooltip placement="right">
                                    <template #title>
                                        <span>选择课时，然后点击“确定”按钮进行批量排课</span>
                                    </template>
                                    <info-circle-outlined style="color: #1890ff; margin-left: 5px;" />
                                </a-tooltip>
                            </div>
                            <a-input-search v-model:value="searchValue" placeholder="搜索课时"
                                style="margin-bottom: 10px; width: 100%;" @search="onSearch" allowClear />
                            <div class="master-checkbox-row">
                                <a-checkbox v-model:checked="allSelected" @change="toggleAllSelection"
                                    :indeterminate="isIndeterminate">
                                    全选
                                </a-checkbox>
                                <div class="selection-count" :class="{ 'has-selected': selectedCount > 0 }">
                                    已选择 {{ selectedCount }} 节课时
                                </div>
                            </div>
                        </div>
                        <div class="scrollable-content">
                            <a-form-item label="">
                                <div v-for="(item, index) in filteredRecommendedSchedules" :key="index"
                                    class="recommended-schedule-item" :class="{ 'disabled': item.disabled }">
                                    <a-checkbox :value="item.schedule"
                                        v-model:checked="selectedSchedules[item.schedule]" class="schedule-checkbox"
                                        :disabled="item.disabled"
                                        @click.stop />
                                    <div class="schedule-content" 
                                         @click="item.disabled ? undefined : toggleScheduleItem(item.schedule)"
                                         :title="item.disabledReason">
                                        <div class="schedule-time">
                                            <span class="week-day-tag">{{ getDayOfWeek(item.schedule.split('【')[0])
                                                }}</span>
                                            {{ item.schedule }}
                                        </div>
                                        <div class="conflict-tags">
                                            <a-tag v-if="item.disabled" color="orange">{{ item.disabledReason }}</a-tag>
                                            <a-tag v-if="item.conflicts.classroom" color="red">教室冲突</a-tag>
                                            <a-tag v-if="item.conflicts.teacher" color="red">教师冲突</a-tag>
                                            <a-tag v-if="item.conflicts.courseTerm" color="red"
                                                class="course-term-conflict">考期冲突</a-tag>
                                        </div>
                                    </div>
                                </div>
                                <div v-if="filteredRecommendedSchedules.length === 0" class="no-data">
                                    {{ searchValue ? '无匹配结果' : '暂无推荐课时' }}
                                </div>
                            </a-form-item>
                        </div>
                    </div>
                    <div class="right-part">
                        <!-- 展示课程排程数据 -->
                        <div class="schedule-container">
                            <div class="schedule-header-fixed">
                                <h3>该课程已排时间</h3>
                                <div class="batch-actions-row">
                                    <a-checkbox v-model:checked="selectAll" @change="handleSelectAll"
                                        :indeterminate="indeterminate">
                                        全选
                                    </a-checkbox>
                                    <a-button type="primary" danger size="small"
                                        :disabled="selectedScheduleIds.length === 0" @click="confirmBatchDelete">
                                        批量删除 ({{ selectedScheduleIds.length }})
                                    </a-button>
                                </div>
                            </div>
                            <div class="schedule-content-scrollable">
                                <ul v-if="Object.keys(groupedCourseScheduleData).length > 0">
                                    <li v-for="(schedules, stage) in groupedCourseScheduleData" :key="stage">
                                        <h4 class="stage-header">
                                            {{ stage }} ({{ schedules.length }})
                                            <span class="copy-btn" @click="copyStageSchedules(stage, schedules)"
                                                title="复制整个阶段">
                                                <copy-outlined />
                                            </span>
                                        </h4>
                                        <ul>
                                            <li v-for="(schedule, index) in schedules" :key="schedule.delId"
                                                class="schedule-item">
                                                <a-checkbox
                                                    :checked="selectedScheduleIds.includes(String(schedule.delId))"
                                                    @change="(e) => handleScheduleSelect(String(schedule.delId), e.target.checked)">
                                                </a-checkbox>
                                                <span class="schedule-content">
                                                    <span class="week-day-tag">
                                                        {{ getDayOfWeek(schedule.dateTime) }}
                                                    </span>
                                                    {{ formatLessonName(schedule.lessonName) }}：{{ schedule.dateTime
                                                    }}【{{
                                                        schedule.beginTime.substring(0, 5) }} ~
                                                    {{ schedule.endTime.substring(0, 5) }}】
                                                </span>
                                                <span class="delete-btn" @click="confirmDelete(schedule.delId)">x</span>
                                            </li>
                                        </ul>
                                    </li>
                                </ul>
                                <div v-else class="no-data">
                                    暂无课程已排课时
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- <div class="right-part">
                        <div v-if="CourseTypeData.length > 0">
                            <h3>考期</h3>
                            <ul>
                                <li v-for="(schedule, index) in CourseTypeData" :key="index">
                                    {{ schedule.dateTime }}【{{
                                        schedule.beginTime.substring(0, 5) }} ~
                                    {{ schedule.endTime.substring(0, 5) }}】
                                </li>
                            </ul>
                        </div>
                        <div v-else>
                            <p>暂无考期信息</p>
                        </div>
                    </div> -->

                    <!-- <div class="right-part">
                        <div v-if="roomScheduleData.length > 0">
                            <h3>教室已排时间</h3>
                            <ul>
                                <li v-for="(schedule, index) in roomScheduleData" :key="index">
                                    {{ schedule.dateTime }}【{{
                                        schedule.beginTime.substring(0, 5) }} ~
                                    {{ schedule.endTime.substring(0, 5) }}】
                                </li>
                            </ul>
                        </div>
                        <div v-else>
                            <p>暂无教室信息</p>
                        </div>
                    </div> -->

                    <!-- <div class="right-part">
                        <div v-if="Object.keys(groupedTermScheduleData).length > 0">
                            <h3>考期已排时间</h3>
                            <div v-for="(schedules, courseType) in groupedTermScheduleData" :key="courseType" class="term-group">
                                <h4 class="term-type-title">{{ courseType }} ({{ schedules.length }})</h4>
                                <ul>
                                    <li v-for="(schedule, index) in schedules" :key="index">
                                        {{ schedule.dateTime }}【{{
                                            schedule.beginTime.substring(0, 5) }} ~
                                        {{ schedule.endTime.substring(0, 5) }}】
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div v-else>
                            <p>暂无考期排程信息</p>
                        </div>
                    </div> -->

                    <div class="right-part">
                        <div class="teacher-schedule-container">
                            <div class="teacher-schedule-header-fixed">
                                <h3>教师已排时间</h3>
                            </div>
                            <div class="teacher-schedule-content-scrollable">
                                <ul v-if="teacherScheduleData.length > 0">
                                    <li v-for="(schedule, index) in teacherScheduleData" :key="index"
                                        class="teacher-schedule-item">
                                        <span class="week-day-tag">
                                            {{ getDayOfWeek(schedule.dateTime) }}
                                        </span>
                                        {{ formatLessonName(schedule.lessonName) }}：{{ schedule.dateTime }}【{{
                                            schedule.beginTime.substring(0, 5) }} ~
                                        {{ schedule.endTime.substring(0, 5) }}】
                                    </li>
                                </ul>
                                <div v-else class="no-data">
                                    暂无教师已排课时
                                </div>
                            </div>
                        </div>

                    </div>

                </div>
            </a-form>
        </template>
    </sys-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed, watch, nextTick } from 'vue';
import loadingOverlay from '@/components/loadingOverlay.vue';
import SysDialog from '../../components/SysDialog.vue';
import useDialog from '../../hooks/useDialog';
import useselect from '../../composable/schedule/useSelect';
import dayjs, { Dayjs } from 'dayjs';
import { saveScheduleApi, getScheduleListApi, deleteCalendarListApi, batchDeleteCalendarListApi, getClassRoomListApi, getTeaListApi, getCourseTypeListApi, getMergedCourseTermsApi, getAllTermSchedulesApi, getStageListApi } from '../../api/schedule/schedule';
import { message } from 'ant-design-vue';
import { useForm } from 'ant-design-vue/lib/form';
import useTable from '@/composable/schedule/useTable';
import { Modal } from 'ant-design-vue';
import { type Stage } from '@/api/course/Stage';
import { InfoCircleOutlined, CopyOutlined } from '@ant-design/icons-vue';

const { rolePage, tableList, columns, tableHeight, listParm, getList } = useTable();
// 下拉属性
const { roomOptions, teacherOptions, courseOptions, courseOptionsPaike, filterCourseOption, filterRoomOption, filterTeacheroption } = useselect(getList, listParm);
// 获取弹窗属性
const { dialog, onShow, onClose } = useDialog();

// 添加时间段组选择
const selectedTimeGroup = ref<1 | 2 | 3>(1);

// 定义时间段组
interface TimeSlot {
    beginTime: string;
    endTime: string;
    duration: number;
}

const timeGroups: Record<1 | 2 | 3, TimeSlot[]> = {
    1: [
        { beginTime: '09:00', endTime: '10:00', duration: 60 },
        { beginTime: '10:30', endTime: '11:30', duration: 60 },
        { beginTime: '14:00', endTime: '15:00', duration: 60 },
        { beginTime: '15:30', endTime: '16:30', duration: 60 },
        { beginTime: '17:00', endTime: '18:00', duration: 60 },
        { beginTime: '19:00', endTime: '20:00', duration: 60 },
        { beginTime: '20:30', endTime: '21:30', duration: 60 }
    ],
    2: [
        { beginTime: '13:00', endTime: '14:00', duration: 60 },
        { beginTime: '19:00', endTime: '20:00', duration: 60 },
        { beginTime: '20:30', endTime: '21:30', duration: 60 }
    ],
    3: [
        { beginTime: '09:00', endTime: '10:00', duration: 60 },
        { beginTime: '10:30', endTime: '11:30', duration: 60 },
        { beginTime: '14:00', endTime: '15:00', duration: 60 },
        { beginTime: '15:30', endTime: '16:30', duration: 60 },
        { beginTime: '17:00', endTime: '18:00', duration: 60 },
        { beginTime: '19:00', endTime: '20:00', duration: 60 },
        { beginTime: '20:30', endTime: '21:30', duration: 60 }
    ]
};

// 处理时间段组变化
const handleTimeGroupChange = () => {
    // 清空现有的时间段
    addParm.schedules = [];
    // 添加新的时间段
    const selectedGroup = timeGroups[selectedTimeGroup.value];
    if (selectedGroup) {
        addParm.schedules = [...selectedGroup];
    }
    // 根据时间段组自动设置星期
    if (selectedTimeGroup.value === 1) {
        // 全天班，设置周一至周日
        addParm.weeks = [1, 2, 3, 4, 5, 6, 7];
    } else if (selectedTimeGroup.value === 2) {
        // 周一至周五晚班，设置周一至周五
        addParm.weeks = [1, 2, 3, 4, 5];
    } else if (selectedTimeGroup.value === 3) {
        // 周末班，设置周六和周日
        addParm.weeks = [6, 7];
    }
};

// 修改 show 方法，设置默认时间段组为全天班
const show = () => {
    resetFields();
    selectTime.value = undefined;
    addParm.beginTime = dayjs(beginTime1.value, "HH:mm").format('HH:mm');
    addParm.endTime = dayjs(endTime1.value, "HH:mm").format('HH:mm');
    addParm.duration = inputNumber.value;
    // 设置默认时间段（全天班）
    addParm.schedules = [...timeGroups[1]];
    // 设置默认星期（周一至周日）
    addParm.weeks = [1, 2, 3, 4, 5, 6, 7];
    // 重置阶段ID为null（保持可选状态）
    addParm.stageId = null;
    // 设置弹窗属性
    dialog.title = '排课';
    dialog.height = 530;
    dialog.width = 1700;
    // 显示弹窗
    onShow();
}

defineExpose({
    show
});

// 表单绑定的对象
const addParm = reactive({
    courseId: '',
    teacherId: '',
    roomId: '',
    startDate: '', // 开课日期
    endDate: '', // 结课日期
    weeks: [1, 2, 3, 4, 5, 6, 7],
    beginTime: '', // 上课时间
    endTime: '', // 下课时间
    duration: 0,
    isMergeClasses: 0, // 是否合并课程
    schedules: [] as { beginTime: string, endTime: string, duration: number }[], // 存储多个时间段
    lessonName: '', // Add lessonName property
    stageId: null as number | null // Add optional stageId property
});

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
});

const { resetFields, validate, validateInfos } = useForm(addParm, rules);

// 上课日期选择
const selectTime = ref<[Dayjs, Dayjs]>();

// 日期选择事件回调
const openChange = (data: string, dateString: string[]) => {
    console.log("日期：" + data, dateString);
    addParm.startDate = dateString[0];
    addParm.endDate = dateString[1];
};

const weekOptions = [
    { label: '星期一', value: 1 },
    { label: '星期二', value: 2 },
    { label: '星期三', value: 3 },
    { label: '星期四', value: 4 },
    { label: '星期五', value: 5 },
    { label: '星期六', value: 6 },
    { label: '星期日', value: 7 }
];

// 上课时间
const beginTime1 = ref<Dayjs>(dayjs('08:00', 'HH:mm'));

// 上课时间点击事件
const beginTimeChange = () => {
    getEndTime();
};

// 课程时长
const inputNumber = ref<number>(60);

// 在定义 endTime1 时设置默认值
const endTime1 = ref<Dayjs | null>(null);

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
    if (beginTime1.value) {
        endTime1.value = dayjs(beginTime1.value, "HH:mm").add(inputNumber.value, 'minute');
        addParm.beginTime = dayjs(beginTime1.value, "HH:mm").format('HH:mm');
        addParm.endTime = endTime1.value ? endTime1.value.format('HH:mm') : '';
        addParm.duration = inputNumber.value;
    }
};

// 注册事件
const emit = defineEmits(['refreshList']);
const loading = ref(false);

// 添加一个响应式变量来存储 handleCourseChange 的结果
const courseScheduleData = ref<any[]>([]);
const roomScheduleData = ref<any[]>([]);
const teacherScheduleData = ref<any[]>([]);
const CourseTypeData = ref<any[]>([]);
const termScheduleData = ref<any[]>([]);

// 批量删除相关的响应式变量
const selectedScheduleIds = ref<string[]>([]);
const selectAll = ref(false);
const indeterminate = ref(false);

// 计算属性：根据阶段名称分组课程排程数据
const groupedCourseScheduleData = computed(() => {
    const grouped: Record<string, any[]> = {};
    if (!courseScheduleData.value) return grouped;

    courseScheduleData.value.forEach(schedule => {
        let stage = '无阶段'; // Default stage

        // Extract stage from lessonName
        if (schedule.lessonName.includes('精讲')) {
            stage = '精讲';
        } else if (schedule.lessonName.includes('密训')) {
            stage = '密训';
        } else if (schedule.lessonName.includes('真题')) {
            stage = '真题';
        } else if (schedule.lessonName.includes('考点')) {
            stage = '考点';
        } else if (schedule.lessonName.includes('其他')) {
            stage = '其他';
        }

        if (!grouped[stage]) {
            grouped[stage] = [];
        }
        grouped[stage].push(schedule);
    });

    return grouped;
});

// 修改 handleCourseChange 函数
const handleCourseChange = async (params: any) => {
    console.log(`selected ${params}`);
    // Use existing listParm and only update courseId
    const courseParams = { ...listParm, courseId: params };

    // 清空已选择的课时
    Object.keys(selectedSchedules).forEach(key => {
        selectedSchedules[key] = false;
    });

    // 重置全选状态
    allSelected.value = false;
    isIndeterminate.value = false;

    try {
        if (params !== '' && params !== null && params !== undefined) {
            // 获取当前课程的排课信息
            const res = await getScheduleListApi(courseParams) as any;
            if (res && res.code === 200) {
                // 将查询到的数据赋值给 courseScheduleData
                console.log("该课程已排时间:" + JSON.stringify(res.data));
                courseScheduleData.value = res.data;
            }

            // 获取合并课程的考期信息
            await fetchMergedCourseTerms(params.toString());

            // 获取考期已排时间
            await getTermScheduleData(params.toString());

            // 获取所有相关考期的排课数据（用于检查冲突）
            const allTermData = await getAllTermSchedules(params.toString());
            allTermSchedules.value = allTermData;

            // 更新推荐课时（触发计算属性重新计算）
            console.log("触发推荐课时重新计算");
        } else {
            courseScheduleData.value = [];
            mergedCourseTerms.value = [];
            allTermSchedules.value = [];
            termScheduleData.value = [];
        }

        // For course type query
        if (params !== '' && params !== null && params !== undefined) {
            const courseTypeParams = { ...listParm, courseType: params };
            const res = await getCourseTypeListApi(courseTypeParams) as any;
            if (res && res.code === 200) {
                console.log("考期:" + JSON.stringify(res.data));
                CourseTypeData.value = res.data;
            }
        } else {
            CourseTypeData.value = [];
        }
    } catch (error) {
        console.error('获取课程排程信息失败', error);
    }
};

const handleClassRoomChange = async (params: any) => {
    console.log("教室")
    console.log(`selected ${params}`);
    // Use spread operator with listParm
    const roomParams = { ...listParm, roomId: params };

    try {
        if (params !== '' && params !== null && params !== undefined) {
            const res = await getClassRoomListApi(roomParams) as any;
            if (res && res.code === 200) {
                // 新的去重逻辑：基于 dateTime, beginTime, endTime 组合去重
                const uniqueSchedulesMap = new Map();
                res.data.forEach((item: any) => {
                    const key = `${item.dateTime}-${item.beginTime}-${item.endTime}`;
                    if (!uniqueSchedulesMap.has(key)) {
                        uniqueSchedulesMap.set(key, item);
                    }
                });
                const uniqueData = Array.from(uniqueSchedulesMap.values());

                // 对去重后的数据进行排序，假设根据 dateTime 字段排序
                uniqueData.sort((a: any, b: any) => new Date(a.dateTime).getTime() - new Date(b.dateTime).getTime());
                // 将去重并排序后的数据赋值给 roomScheduleData
                roomScheduleData.value = uniqueData;
            }
        } else {
            roomScheduleData.value = [];
        }
    } catch (error) {
        console.error('获取课程排程信息失败', error);
    }
};

// 还得添加一个考期剔除

const handleTeaChange = async (params: any) => {
    console.log("教师")
    console.log(`selected ${params}`);
    // Use spread operator with listParm
    const teacherParams = { ...listParm, teacherId: params };

    try {
        if (params !== '' && params !== null && params !== undefined) {
            const res = await getTeaListApi(teacherParams) as any;
            if (res && res.code === 200) {
                // 新的去重逻辑：基于 dateTime, beginTime, endTime 组合去重
                const uniqueSchedulesMap = new Map();
                res.data.forEach((item: any) => {
                    const key = `${item.dateTime}-${item.beginTime}-${item.endTime}`;
                    if (!uniqueSchedulesMap.has(key)) {
                        uniqueSchedulesMap.set(key, item);
                    }
                });
                const uniqueData = Array.from(uniqueSchedulesMap.values());

                // 对去重后的数据进行排序，假设根据 dateTime 字段排序
                uniqueData.sort((a: any, b: any) => new Date(a.dateTime).getTime() - new Date(b.dateTime).getTime());
                // 将去重并排序后的数据赋值给 teacherScheduleData
                teacherScheduleData.value = uniqueData;
            }
        } else {
            teacherScheduleData.value = [];
        }
    } catch (error) {
        console.error('获取课程排程信息失败', error);
    }
};

// 添加时间段
const addSchedule = () => {
    if (beginTime1.value && endTime1.value) {
        const newBeginTime = beginTime1.value.format('HH:mm');
        const newEndTime = endTime1.value.format('HH:mm');

        // 验证新时间段是否与已有的时间段重叠
        for (const schedule of addParm.schedules) {
            const existingBeginTime = schedule.beginTime;
            const existingEndTime = schedule.endTime;

            if (
                (newBeginTime < existingEndTime && newEndTime > existingBeginTime) ||
                (newBeginTime <= existingBeginTime && newEndTime >= existingEndTime)
            ) {
                message.error('时间段重叠，请选择其他时间');
                return;
            }
        }

        // 如果没有重叠，添加新时间段
        addParm.schedules.push({
            beginTime: newBeginTime,
            endTime: newEndTime,
            duration: inputNumber.value
        });

        // 对时间段进行排序
        addParm.schedules.sort((a, b) => {
            return a.beginTime.localeCompare(b.beginTime);
        });

        getEndTime();
    } else {
        message.error('请先选择上课时间和下课时间');
    }
};

// 删除时间段
const removeSchedule = (index: number) => {
    addParm.schedules.splice(index, 1);

};

const onConfirm = async () => {
    validate().then(async () => {
        try {
            // 检查是否有选中的推荐课时
            const selectedSchedules = getSelectedSchedules();
            if (selectedSchedules.length > 0) {
                // 如果有选中的推荐课时，则执行批量排课
                await batchSaveSelectedSchedules();
                emit('refreshList');
                return;
            }

            // 如果没有选中的推荐课时，提示用户需要先选择课时
            message.warning('请先选择要排课的课时');

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
};

// 删除确认
const confirmDelete = async (id: number) => {
    Modal.confirm({
        title: '确认删除',
        content: '确定要删除这条课程排程吗？',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: async () => {
            try {
                const res = await deleteCalendarListApi(String(id)) as any;
                if (res && res.code === 200) {
                    message.success('删除成功');
                    await handleCourseChange(addParm.courseId);
                    await handleTeaChange(addParm.teacherId);
                    await handleClassRoomChange(addParm.roomId);
                } else {
                    message.error('删除失败');
                }
            } catch (error) {
                console.error('删除课程排程失败', error);
                message.error('请求出错，请重试');
            }
        },
        onCancel() {
            console.log('取消删除');
        },
    });
};

// 批量删除相关函数
// 处理全选/取消全选
const handleSelectAll = (e: any) => {
    const checked = e.target.checked;

    if (checked) {
        // 获取所有课程的delId
        const allDelIds: string[] = [];
        courseScheduleData.value.forEach(schedule => {
            allDelIds.push(schedule.delId);
        });
        selectedScheduleIds.value = allDelIds;
    } else {
        selectedScheduleIds.value = [];
    }
    updateSelectAllState();
};

// 处理单个课程选择
const handleScheduleSelect = (delId: string, checked: boolean) => {
    if (checked) {
        // 添加到选中列表
        if (!selectedScheduleIds.value.includes(delId)) {
            selectedScheduleIds.value.push(delId);
        }
    } else {
        // 从选中列表中移除
        const index = selectedScheduleIds.value.indexOf(delId);
        if (index > -1) {
            selectedScheduleIds.value.splice(index, 1);
        }
    }
    updateSelectAllState();
};

// 更新全选状态
const updateSelectAllState = () => {
    const totalCount = courseScheduleData.value.length;
    const selectedCount = selectedScheduleIds.value.length;

    if (selectedCount === 0) {
        selectAll.value = false;
        indeterminate.value = false;
    } else if (selectedCount === totalCount) {
        selectAll.value = true;
        indeterminate.value = false;
    } else {
        selectAll.value = false;
        indeterminate.value = true;
    }
};

// 批量删除确认
const confirmBatchDelete = async () => {
    if (selectedScheduleIds.value.length === 0) {
        message.warning('请选择要删除的课程排程');
        return;
    }

    Modal.confirm({
        title: '确认批量删除',
        content: `确定要删除选中的 ${selectedScheduleIds.value.length} 条课程排程吗？`,
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: async () => {
            try {
                loading.value = true;

                // 使用批量删除API
                const res = await batchDeleteCalendarListApi(selectedScheduleIds.value) as any;

                if (res && res.code === 200) {
                    message.success(`批量删除成功，共删除 ${selectedScheduleIds.value.length} 条记录`);

                    // 清空选择
                    selectedScheduleIds.value = [];
                    updateSelectAllState();

                    // 刷新数据
                    await handleCourseChange(addParm.courseId);
                    await handleTeaChange(addParm.teacherId);
                    await handleClassRoomChange(addParm.roomId);
                } else {
                    message.error(res.msg || '批量删除失败');
                }
            } catch (error) {
                console.error('批量删除失败', error);
                message.error('批量删除失败，请重试');
            } finally {
                loading.value = false;
            }
        },
        onCancel() {
            console.log('取消批量删除');
        },
    });
};

// 监听课程数据变化，重置选择状态
watch(courseScheduleData, () => {
    selectedScheduleIds.value = [];
    updateSelectAllState();
}, { deep: true });

// 计算推荐课时
const recommendedSchedules = computed(() => {
    if (!selectTime.value || addParm.schedules.length === 0 || addParm.weeks.length === 0) return [];
    const startDate = selectTime.value[0];
    const endDate = selectTime.value[1];

    const recommendedSchedules: {
        schedule: string,
        conflicts: {
            classroom: boolean,
            teacher: boolean,
            courseTerm: boolean
        },
        disabled?: boolean,
        disabledReason?: string
    }[] = [];
    let currentDate = dayjs(startDate);
    const end = dayjs(endDate);

    // 存储每天的课时数量（包括已排课时、已选中的推荐课时和考期冲突的课时）
    const dailyScheduleCount: { [key: string]: number } = {};
    const dailyTermConflictCount: { [key: string]: number } = {};

    // 先统计现有课程每天的课时数量
    courseScheduleData.value.forEach(schedule => {
        const date = schedule.dateTime;
        dailyScheduleCount[date] = (dailyScheduleCount[date] || 0) + 1;
    });

    // 统计已选中的推荐课时
    Object.entries(selectedSchedules).forEach(([schedule, isSelected]) => {
        if (isSelected) {
            const date = schedule.split('【')[0];
            dailyScheduleCount[date] = (dailyScheduleCount[date] || 0) + 1;
        }
    });
    
    // 统计每天的考期冲突课时数
    termScheduleData.value.forEach(schedule => {
        const date = schedule.dateTime;
        dailyTermConflictCount[date] = (dailyTermConflictCount[date] || 0) + 1;
    });

    // 获取当前课程的所有相关考期数据
    const termSchedules = allTermSchedules.value;

    while (currentDate.isSame(end) || currentDate.isBefore(end)) {
        const currentDateTime = currentDate.format('YYYY-MM-DD');
        let currentDayOfWeek = currentDate.day() === 0 ? 7 : currentDate.day();

        if (addParm.weeks.includes(currentDayOfWeek)) {
            const isWeekend = currentDayOfWeek === 6 || currentDayOfWeek === 7;
            const maxDailySchedules = isWeekend ? 5 : Infinity; // 周末最多5个时间段

            // 计算该天已经选择和排课的总数
            const existingCount = dailyScheduleCount[currentDateTime] || 0;

            for (const schedule of addParm.schedules) {
                const newBeginTime = dayjs(currentDateTime + ' ' + schedule.beginTime, 'YYYY-MM-DD HH:mm');
                const newEndTime = dayjs(currentDateTime + ' ' + schedule.endTime, 'YYYY-MM-DD HH:mm');
                
                // 获取当前时间段的状态
                const currentScheduleKey = `${currentDateTime}【${schedule.beginTime} ~ ${schedule.endTime}】`;
                const isCurrentSelected = selectedSchedules[currentScheduleKey];

                // 计算有效课时数（不包括当前时间段如果已被选中的情况）
                const effectiveCount = isCurrentSelected ? existingCount - 1 : existingCount;

                let classroomConflict = false;
                let teacherConflict = false;
                let courseConflict = false;
                let courseTermConflict = false;
                // 只有未选中的时间段，且当天其他课时达到上限时才禁用
                // 只有未选中的时间段，且当天其他课时达到上限时才禁用
                // 计算当天的总课时数（包括已排课时、已选课时和考期冲突课时）
                const totalDailyCount = (dailyScheduleCount[currentDateTime] || 0) + (dailyTermConflictCount[currentDateTime] || 0);
                let exceedMaxSchedules = !isCurrentSelected && (effectiveCount >= maxDailySchedules || totalDailyCount >= maxDailySchedules);



                // Check course schedule conflicts
                for (const existingSchedule of courseScheduleData.value) {
                    const existingBeginTime = dayjs(existingSchedule.dateTime + ' ' + existingSchedule.beginTime, 'YYYY-MM-DD HH:mm');
                    const existingEndTime = dayjs(existingSchedule.dateTime + ' ' + existingSchedule.endTime, 'YYYY-MM-DD HH:mm');

                    if (
                        (newBeginTime.isSame(existingBeginTime) && newEndTime.isSame(existingEndTime)) ||
                        (newBeginTime.isBefore(existingEndTime) && newEndTime.isAfter(existingBeginTime)) ||
                        (newBeginTime.isAfter(existingBeginTime) && newBeginTime.isBefore(existingEndTime))
                    ) {
                        courseConflict = true;
                        break;
                    }
                }

                // 检查与其他考期的冲突
                for (const termCourse of termSchedules) {
                    // 只检查来自其他考期的数据（不是当前课程的）
                    if (termCourse.isTermCourse === true) {
                        try {
                            // 获取日期和时间
                            const termDateStr = typeof termCourse.dateTime === 'object' ?
                                dayjs(termCourse.dateTime).format('YYYY-MM-DD') :
                                String(termCourse.dateTime);

                            const beginTimeStr = typeof termCourse.beginTime === 'object' ?
                                dayjs(termCourse.beginTime).format('HH:mm:ss') :
                                String(termCourse.beginTime);

                            const endTimeStr = typeof termCourse.endTime === 'object' ?
                                dayjs(termCourse.endTime).format('HH:mm:ss') :
                                String(termCourse.endTime);

                            // 创建完整的日期时间
                            const termBeginTime = dayjs(`${termDateStr} ${beginTimeStr}`);
                            const termEndTime = dayjs(`${termDateStr} ${endTimeStr}`);

                            // 检查时间冲突
                            if (newBeginTime.isBefore(termEndTime) && newEndTime.isAfter(termBeginTime)) {
                                console.log(`检测到考期冲突: ${newBeginTime.format('YYYY-MM-DD HH:mm')} - ${newEndTime.format('HH:mm')} 与 ${termCourse.termName || '未知考期'} 课程冲突: ${termDateStr} ${beginTimeStr} - ${endTimeStr}`);
                                courseTermConflict = true;
                                break;
                            }
                        } catch (err) {
                            console.error('处理考期日期时间出错:', err, termCourse);
                        }
                    }
                }

                // 检查与考期已排时间的冲突
                for (const termSchedule of termScheduleData.value) {
                    try {
                        const termDateStr = termSchedule.dateTime;
                        const beginTimeStr = termSchedule.beginTime;
                        const endTimeStr = termSchedule.endTime;

                        const termBeginTime = dayjs(`${termDateStr} ${beginTimeStr}`, 'YYYY-MM-DD HH:mm');
                        const termEndTime = dayjs(`${termDateStr} ${endTimeStr}`, 'YYYY-MM-DD HH:mm');

                        // 检查时间冲突
                        if (newBeginTime.isBefore(termEndTime) && newEndTime.isAfter(termBeginTime)) {
                            console.log(`检测到考期冲突: ${newBeginTime.format('YYYY-MM-DD HH:mm')} - ${newEndTime.format('HH:mm')} 与 ${termSchedule.courseType || '未知考期'} 课程冲突: ${termDateStr} ${beginTimeStr} - ${endTimeStr}`);
                            courseTermConflict = true;
                            break;
                        }
                    } catch (err) {
                        console.error('处理考期已排时间冲突检测出错:', err, termSchedule);
                    }
                }

                // Check classroom conflicts
                for (const roomSchedule of roomScheduleData.value) {
                    const roomBeginTime = dayjs(roomSchedule.dateTime + ' ' + roomSchedule.beginTime, 'YYYY-MM-DD HH:mm');
                    const roomEndTime = dayjs(roomSchedule.dateTime + ' ' + roomSchedule.endTime, 'YYYY-MM-DD HH:mm');

                    if (
                        (newBeginTime.isSame(roomBeginTime) && newEndTime.isSame(roomEndTime)) ||
                        (newBeginTime.isBefore(roomEndTime) && newEndTime.isAfter(roomBeginTime)) ||
                        (newBeginTime.isAfter(roomBeginTime) && newBeginTime.isBefore(roomEndTime))
                    ) {
                        classroomConflict = true;
                        break;
                    }
                }

                // Check teacher conflicts
                for (const teacherSchedule of teacherScheduleData.value) {
                    const teacherBeginTime = dayjs(teacherSchedule.dateTime + ' ' + teacherSchedule.beginTime, 'YYYY-MM-DD HH:mm');
                    const teacherEndTime = dayjs(teacherSchedule.dateTime + ' ' + teacherSchedule.endTime, 'YYYY-MM-DD HH:mm');

                    if (
                        (newBeginTime.isSame(teacherBeginTime) && newEndTime.isSame(teacherEndTime)) ||
                        (newBeginTime.isBefore(teacherEndTime) && newEndTime.isAfter(teacherBeginTime)) ||
                        (newBeginTime.isAfter(teacherBeginTime) && newBeginTime.isBefore(teacherEndTime))
                    ) {
                        teacherConflict = true;
                        break;
                    }
                }

                // Only add to recommended schedules if there's no course conflict
                if (!courseConflict) {
                recommendedSchedules.push({
                    schedule: `${currentDateTime}【${schedule.beginTime} ~ ${schedule.endTime}】`,
                    conflicts: {
                        classroom: classroomConflict,
                        teacher: teacherConflict,
                        courseTerm: courseTermConflict
                    },
                    disabled: exceedMaxSchedules,
                    disabledReason: exceedMaxSchedules ? '该天已排5个时间段' : undefined
                });                    // 初始化这个课时的选中状态
                    const scheduleKey = `${currentDateTime}【${schedule.beginTime} ~ ${schedule.endTime}】`;
                    if (selectedSchedules[scheduleKey] === undefined) {
                        selectedSchedules[scheduleKey] = false;
                    }
                }
            }
        }

        currentDate = currentDate.add(1, 'day');
    }

    return recommendedSchedules;
});

// 点击推荐课时的处理函数
const handleRecommendedScheduleClick = async (schedule: string) => {
    const [date, timeRange] = schedule.split('【');
    const [beginTime, endTime] = timeRange.replace('】', '').split(' ~ ');

    beginTime1.value = dayjs(beginTime, 'HH:mm');
    endTime1.value = dayjs(endTime, 'HH:mm');
    inputNumber.value = endTime1.value.diff(beginTime1.value, 'minute');

    // 保存单个推荐课时并刷新排程数据
    await saveSingleRecommendedSchedule(date, beginTime, endTime);

    // 手动更新 addParm.schedules，确保其他推荐课时仍然存在
    const existingSchedule = addParm.schedules.find(s => s.beginTime === beginTime && s.endTime === endTime);
    if (!existingSchedule) {
        addParm.schedules.push({
            beginTime: beginTime,
            endTime: endTime,
            duration: inputNumber.value
        });
    }

    // 刷新教师和教室的排程数据
    await handleTeaChange(addParm.teacherId);
    await handleClassRoomChange(addParm.roomId);
};

// 新增保存单个推荐课时的方法
const saveSingleRecommendedSchedule = async (date: string, beginTime: string, endTime: string): Promise<boolean> => {
    try {
        loading.value = true; // 请求开始前设置 loading 为 true
        // await handleTeaChange(addParm.teacherId);
        // await handleClassRoomChange(addParm.roomId);

        // 计算当前课时序号
        // 过滤出与当前阶段名称匹配的已排课程，并计算数量
        const stageScheduleCount = courseScheduleData.value.filter(schedule => {
            // 提取已排课程的阶段名称
            const existingLessonName = schedule.lessonName || '';
            let existingStageName = null;
            if (existingLessonName.includes('精讲')) existingStageName = '精讲';
            else if (existingLessonName.includes('密训')) existingStageName = '密训';
            else if (existingLessonName.includes('真题')) existingStageName = '真题';
            else if (existingLessonName.includes('考点')) existingStageName = '考点';
            else if (existingLessonName.includes('其他')) existingStageName = '其他';

            // 获取当前选择的阶段名称
            const currentStageName = addParm.stageId ?
                stageOptions.value.find(stage => stage.id === addParm.stageId)?.stageName : null;

            // 如果 addParm.stageId 为 null，匹配没有明确阶段名称的课程 (或者只包含 '精讲' 的课程，如果 '精讲' 是默认且不显式包含阶段名的情况)
            if (!currentStageName) {
                // 假设没有明确阶段名称的课程或只包含 '精讲' 的课程属于同一类
                return !existingStageName || existingStageName === '精讲';
            } else {
                // 匹配具有相同阶段名称的课程
                return existingStageName === currentStageName;
            }

        }).length;

        // 计算当前课时序号，基于当前阶段的课程数量 + 1 (因为我们正在添加一个新的课时)
        const paddedLessonIndex = (stageScheduleCount + 1).toString().padStart(2, '0');

        // 构建新的 lessonName: 课程名称 (不包含考期名称) + 阶段名称 (可选) + 序号
        let baseCourseName = addParm.lessonName;
        const hyphenIndex = baseCourseName.indexOf(' - ');
        if (hyphenIndex !== -1) {
            baseCourseName = baseCourseName.substring(0, hyphenIndex);
        }

        let dynamicLessonName = baseCourseName; // 以课程名称开头
        const currentStageName = addParm.stageId ?
            stageOptions.value.find(stage => stage.id === addParm.stageId)?.stageName : null;
        if (currentStageName) {
            dynamicLessonName += `${currentStageName}`; // 添加阶段名称
        }
        dynamicLessonName += `${paddedLessonIndex}`; // 添加序号


        const singleSchedule = {
            courseId: addParm.courseId,
            roomId: addParm.roomId,
            teacherId: addParm.teacherId,
            //开课时间
            startDate: date,
            //结课时间
            endDate: date,
            weeks: addParm.weeks,
            //上课时间
            beginTime: beginTime,
            //下课时间
            endTime: endTime,
            duration: inputNumber.value,
            lessonName: dynamicLessonName,
            stageId: addParm.stageId, // 添加阶段ID
        }
        const res = await saveScheduleApi(singleSchedule) as any;
        if (res && res.code == 200) {
            emit('refreshList');
            if (addParm.courseId !== null && addParm.courseId !== '') {
                // 保存成功后，重新获取课程排程信息以更新列表和序号计算基础
                await handleCourseChange(addParm.courseId);
            }
            // 刷新教师和教室的排程数据
            await handleTeaChange(addParm.teacherId);
            await handleClassRoomChange(addParm.roomId);
            message.success('添加课时成功'); // 添加成功提示
            return true;
        } else {
            message.error(res.msg || '保存失败'); // 显示后端返回的错误信息
            return false;
        }
    } catch (err) {
        console.log(err);
        message.error('请求出错，请重试');
        return false;
    } finally {
        loading.value = false; // 请求结束后设置 loading 为 false
    }
};

// 获取星期几的函数
const getDayOfWeek = (dateStr: string) => {
    const date = dayjs(dateStr);
    const weekDays = ['日', '一', '二', '三', '四', '五', '六'];
    return '周' + weekDays[date.day()];
};

// Method to format lesson name for display
const formatLessonName = (lessonName: string) => {
    if (!lessonName) return '';
    // Assuming the format is CourseNameStageIndex
    // Extracting the part after the course name which should be StageIndex
    const stageNames = ['精讲', '密训', '真题', '考点', '其他'];
    for (const stage of stageNames) {
        const index = lessonName.indexOf(stage);
        if (index !== -1) {
            return lessonName.substring(index);
        }
    }
    // Fallback if no known stage name is found
    return lessonName;
};

// 复制整个阶段的课程时间信息
const copyStageSchedules = async (stageName: string, schedules: any[]) => {
    try {
        // 按日期排序
        const sortedSchedules = [...schedules].sort((a, b) => {
            const dateA = new Date(a.dateTime);
            const dateB = new Date(b.dateTime);
            return dateA.getTime() - dateB.getTime();
        });

        // 生成复制文本
        const copyLines = sortedSchedules.map(schedule => {
            const lessonName = formatLessonName(schedule.lessonName);
            return `${lessonName}：${schedule.dateTime}【${schedule.beginTime.substring(0, 5)} ~ ${schedule.endTime.substring(0, 5)}】`;
        });

        const copyText = copyLines.join('\n');

        // 使用现代的 Clipboard API
        if (navigator.clipboard && window.isSecureContext) {
            await navigator.clipboard.writeText(copyText);
        } else {
            // 降级方案：使用传统的 document.execCommand
            const textArea = document.createElement('textarea');
            textArea.value = copyText;
            textArea.style.position = 'fixed';
            textArea.style.left = '-999999px';
            textArea.style.top = '-999999px';
            document.body.appendChild(textArea);
            textArea.focus();
            textArea.select();
            document.execCommand('copy');
            textArea.remove();
        }

        message.success(`已复制${stageName}阶段的${schedules.length}个课时`);
    } catch (err) {
        console.error('复制失败:', err);
        message.error('复制失败');
    }
};

// 弹窗取消
onMounted(() => {
    getEndTime();
    fetchStageOptions();

    // 初始化选择状态
    nextTick(() => {
        updateSelectionStatus();
    });
});

// 定义一个响应式变量来存储阶段列表数据
const stageOptions = ref<Stage[]>([]);

// 定义 API 返回的数据结构 (假设和 StageTable 中一致)
interface ApiResponse<T> {
    code: number;
    data?: T;
    message?: string;
}

// 获取阶段列表数据
const fetchStageOptions = async () => {
    try {
        const res = await getStageListApi() as ApiResponse<Stage[]>;
        if (res && res.code === 200 && res.data) {
            stageOptions.value = res.data;
        } else {
            console.error('Failed to fetch stage options:', res.message);
            stageOptions.value = []; // 获取失败时清空选项
        }
    } catch (error) {
        console.error('Error fetching stage options:', error);
        stageOptions.value = []; // 获取失败时清空选项
    }
};

// 在script部分添加搜索相关的变量和函数
// 搜索框内容
const searchValue = ref('');

// 创建一个新的响应式对象存储选中状态
const selectedSchedules = reactive<Record<string, boolean>>({});

// 全选状态
const allSelected = ref(false);
// 不确定状态（部分选中）
const isIndeterminate = ref(false);

// 用于存储每天考期冲突的课时数量
const dailyTermConflictCount = reactive<Record<string, number>>({});

// 搜索函数
const onSearch = (value: string) => {
    console.log('搜索内容:', value);
    searchValue.value = value;

    // 更新搜索后的选择状态
    nextTick(() => {
        updateSelectionStatus();
    });
};

// 切换单个课时的选择状态
const toggleScheduleItem = (schedule: string) => {
    const currentState = selectedSchedules[schedule] || false;
    const date = schedule.split('【')[0];
    
    // 计算该天当前已有的课时数量（包括已排课时和已选中的课时）
    const existingCount = (courseScheduleData.value || [])
        .filter(s => s.dateTime === date).length;
    
    const selectedCount = Object.entries(selectedSchedules)
        .filter(([key, isSelected]) => isSelected && key.startsWith(date))
        .length;
        
    // 计算考期冲突的课时数量
    const termConflictCount = dailyTermConflictCount[date] || 0;
    const totalDailyCount = existingCount + selectedCount + termConflictCount;

    // 检查是否是周末
    const dayOfWeek = dayjs(date).day();
    const isWeekend = dayOfWeek === 6 || dayOfWeek === 0;
    
    // 如果是周末，且当前未选中，且总数（包括考期冲突）已达到5个，则不允许选中
    if (isWeekend && !currentState && totalDailyCount >= 5) {
        message.warning('该天已达到最大课时数量限制（5个课时，包括考期冲突的课时）');
        return;
    }

    // 更新选择状态
    selectedSchedules[schedule] = !currentState;
    updateSelectionStatus();

    console.log(`切换课时 ${schedule} 选中状态为: ${!currentState}`);
};

// 删除不再需要的函数

// 切换全选/全不选
const toggleAllSelection = (e: { target: { checked: boolean } }) => {
    const checked = e.target.checked;

    // 如果是取消全选
    if (!checked) {
        // 清除所有选中状态
        Object.keys(selectedSchedules).forEach(key => {
            selectedSchedules[key] = false;
        });
        console.log('已清空所有选择');
    } else {
        // 如果是全选，需要考虑周末课时限制
        const dailyScheduleCount: { [key: string]: number } = {};
        
        // 统计已有课程的每天课时数量
        courseScheduleData.value.forEach(schedule => {
            const date = schedule.dateTime;
            dailyScheduleCount[date] = (dailyScheduleCount[date] || 0) + 1;
        });

        // 遍历所有可见的推荐课时
        filteredRecommendedSchedules.value.forEach((item) => {
            const date = item.schedule.split('【')[0];
            const dayOfWeek = dayjs(date).day();
            const isWeekend = dayOfWeek === 6 || dayOfWeek === 0;
            
            if (isWeekend) {
                // 如果是周末，检查是否超过限制
                if ((dailyScheduleCount[date] || 0) < 5) {
                    selectedSchedules[item.schedule] = true;
                    dailyScheduleCount[date] = (dailyScheduleCount[date] || 0) + 1;
                }
            } else {
                // 非周末不受限制
                selectedSchedules[item.schedule] = true;
            }
        });

        console.log(`已选中符合条件的课时`);
    }

    // 更新全选状态
    updateSelectionStatus();
};

// 获取选中的课时
const getSelectedSchedules = () => {
    return filteredRecommendedSchedules.value.filter(item => selectedSchedules[item.schedule]);
};

// 计算选中的课时数量
const selectedCount = computed(() => {
    return Object.values(selectedSchedules).filter(Boolean).length;
});

// 改用单独的更新函数，不使用监听器，避免可能的循环依赖
const updateSelectionStatus = () => {
    const count = selectedCount.value;
    const total = filteredRecommendedSchedules.value.length;

    if (count === 0) {
        // 没有选中任何项
        allSelected.value = false;
        isIndeterminate.value = false;
    } else if (count === total && total > 0) {
        // 全部选中
        allSelected.value = true;
        isIndeterminate.value = false;
    } else {
        // 部分选中
        allSelected.value = false;
        isIndeterminate.value = true;
    }
};

// 批量保存选中的课时
const batchSaveSelectedSchedules = async () => {
    const selectedItems = getSelectedSchedules();
    if (selectedItems.length === 0) {
        message.warning('请至少选择一个推荐课时');
        return;
    }

    try {
        loading.value = true;
        let successCount = 0;

        // 顺序处理每个选中的课时
        for (const item of selectedItems) {
            const [date, timeRange] = item.schedule.split('【');
            const [beginTime, endTime] = timeRange.replace('】', '').split(' ~ ');

            // 设置当前处理的时间
            beginTime1.value = dayjs(beginTime, 'HH:mm');
            endTime1.value = dayjs(endTime, 'HH:mm');
            inputNumber.value = endTime1.value.diff(beginTime1.value, 'minute');

            // 保存单个课时
            try {
                const result = await saveSingleRecommendedSchedule(date, beginTime, endTime);
                if (result) {
                    successCount++;
                    // 设置为未选中状态
                    selectedSchedules[item.schedule] = false;
                }
            } catch (err) {
                console.error('排课失败:', err);
            }
        }

        if (successCount > 0) {
            message.success(`成功排课 ${successCount} 节课时`);
            // 刷新教师和教室的排程数据
            await handleTeaChange(addParm.teacherId);
            await handleClassRoomChange(addParm.roomId);

            // 更新全选状态
            allSelected.value = false;
            isIndeterminate.value = false;
            updateSelectionStatus();
        }
    } catch (err) {
        console.error('批量排课失败:', err);
        message.error('批量排课过程中发生错误');
    } finally {
        loading.value = false;
    }
};

// 复制全部推荐课时功能
const copyAllRecommendedSchedules = async () => {
    if (filteredRecommendedSchedules.value.length === 0) {
        message.warning('暂无推荐课时可复制');
        return;
    }

    try {
        // 格式化复制内容
        const copyContent = filteredRecommendedSchedules.value.map(item => {
            // 提取日期和时间
            const schedule = item.schedule;

            // 收集冲突信息
            const conflicts = [];
            if (item.conflicts.teacher) conflicts.push('教师冲突');
            if (item.conflicts.classroom) conflicts.push('教室冲突');
            if (item.conflicts.courseTerm) conflicts.push('考期冲突');

            // 格式化输出：日期【时间】冲突信息（如果有）
            if (conflicts.length > 0) {
                return `${schedule}${conflicts.join('、')}`;
            } else {
                return schedule;
            }
        }).join('\n');

        // 复制到剪贴板
        await navigator.clipboard.writeText(copyContent);
        message.success(`已复制 ${filteredRecommendedSchedules.value.length} 个推荐课时到剪贴板`);
    } catch (err) {
        console.error('复制失败:', err);
        message.error('复制失败，请重试');
    }
};

// 过滤后的推荐课时列表
const filteredRecommendedSchedules = computed(() => {
    console.log('搜索值:', searchValue.value);
    console.log('推荐课时数量:', recommendedSchedules.value.length);

    let result = [];

    if (!searchValue.value.trim()) {
        result = recommendedSchedules.value;
    } else {
        result = recommendedSchedules.value.filter(item =>
            item.schedule.toLowerCase().includes(searchValue.value.toLowerCase())
        );
    }

    // 初始化选择状态
    result.forEach(item => {
        // 如果在selectedSchedules中没有对应的键，就初始化为false
        if (selectedSchedules[item.schedule] === undefined) {
            selectedSchedules[item.schedule] = false;
        }
    });

    console.log('过滤后数量:', result.length);
    return result;
});

// 创建一个响应式引用存储合并课程考期数据
const mergedCourseTerms = ref<any[]>([]);
// 用于存储鼠标悬停时获取的考期数据
const hoveredCourseTerms = ref<{ courseType: string }[]>([]);
// 用于存储所有相关考期的排课数据
const allTermSchedules = ref<any[]>([]);

// 计算属性：按考期类型分组考期已排时间数据
const groupedTermScheduleData = computed(() => {
    const grouped: Record<string, any[]> = {};
    if (!termScheduleData.value) return grouped;

    termScheduleData.value.forEach(schedule => {
        // 使用courseType字段作为分组标识
        let courseType = schedule.courseType || '未知考期';

        if (!grouped[courseType]) {
            grouped[courseType] = [];
        }
        grouped[courseType].push(schedule);
    });

    return grouped;
});

// 处理鼠标悬停在课程选项上的事件
const onCourseMouseEnter = async (courseId: string) => {
    console.log("鼠标悬停课程ID:", courseId);
    try {
        // 只有当有课程ID时才发送请求
        if (courseId) {
            const res = await getMergedCourseTermsApi(courseId) as any;
            console.log("获取合并课程考期响应:", res);

            if (res && res.code === 200) {
                // 即使数据为空也显示日志
                console.log("合并课程考期原始数据:", res.data);

                if (res.data && res.data.length > 0) {
                    // 对考期数据进行去重处理
                    const uniqueCourseTypesMap = new Map();

                    res.data.forEach((item: any) => {
                        console.log("考期项:", item);
                        if (!uniqueCourseTypesMap.has(item.courseType)) {
                            uniqueCourseTypesMap.set(item.courseType, item);
                        }
                    });

                    // 将去重后的数据转换回数组
                    hoveredCourseTerms.value = Array.from(uniqueCourseTypesMap.values());
                    console.log("去重后的考期数据:", hoveredCourseTerms.value);
                } else {
                    console.log("API返回数据为空");
                    hoveredCourseTerms.value = [];
                }
            } else {
                console.warn("API响应错误或无数据:", res);
                hoveredCourseTerms.value = [];
            }
        } else {
            console.log("无课程ID，清空考期数据");
            hoveredCourseTerms.value = [];
        }
    } catch (error) {
        console.error('获取合并课程考期信息失败', error);
        hoveredCourseTerms.value = [];
    }
};

// 获取合并课程的考期 (用于选中课程后)
const fetchMergedCourseTerms = async (courseId: string) => {
    if (!courseId) {
        mergedCourseTerms.value = [];
        return;
    }

    try {
        const res = await getMergedCourseTermsApi(courseId) as any;
        if (res && res.code === 200 && res.data) {
            mergedCourseTerms.value = res.data.map((item: any) => ({
                courseType: item.courseType
            }));
        } else {
            mergedCourseTerms.value = [];
        }
    } catch (error) {
        console.error('获取合并课程考期失败:', error);
        mergedCourseTerms.value = [];
    }
};

// 获取合并课程所有考期的已排课时
const getAllTermSchedules = async (courseId: string) => {
    if (!courseId) return [];

    try {
        // 获取相关考期信息
        const termsRes = await getMergedCourseTermsApi(courseId) as any;
        if (termsRes && termsRes.code === 200 && termsRes.data) {
            const allTermData: any[] = [...courseScheduleData.value]; // 包含当前课程的排课数据
            console.log("获取到合并课程的相关考期数据:", termsRes.data);

            // 对每个考期，获取其已排课时
            const promises = termsRes.data.map(async (term: any) => {
                if (term.courseId) {
                    const termCourseId = term.courseId.toString();
                    console.log("正在获取考期课程数据，课程ID:", termCourseId);
                    const termParams = { ...listParm, courseId: termCourseId };
                    const scheduleRes = await getScheduleListApi(termParams) as any;

                    if (scheduleRes && scheduleRes.code === 200 && scheduleRes.data) {
                        console.log(`考期 ${term.courseType} 的排课数据:`, scheduleRes.data);
                        // 添加标记，表明这些数据来自其他考期
                        const termSchedules = scheduleRes.data.map((item: any) => ({
                            ...item,
                            isTermCourse: true,
                            termName: term.courseType
                        }));
                        return termSchedules;
                    }
                }
                return [];
            });

            try {
                // 等待所有请求完成
                const results = await Promise.all(promises);
                console.log("所有考期数据获取结果:", results);

                // 合并所有考期的排课数据
                results.forEach(result => {
                    if (result && result.length) {
                        allTermData.push(...result);
                    }
                });

                console.log("合并后的所有考期排课数据:", allTermData);
                return allTermData;
            } catch (err) {
                console.error("获取考期排课数据时出错:", err);
                return [...courseScheduleData.value]; // 出错时只返回当前课程数据
            }
        }
    } catch (error) {
        console.error('获取考期排课数据失败:', error);
    }

    return [...courseScheduleData.value]; // 默认只返回当前课程数据
};

// 获取当前课程的考期已排时间
const getTermScheduleData = async (courseId: string) => {
    if (!courseId) {
        termScheduleData.value = [];
        return;
    }

    try {
        const res = await getAllTermSchedulesApi(courseId) as any;
        if (res && res.code === 200) {
            console.log("考期已排时间数据:", res.data);

            // 对考期已排时间数据进行去重处理
            const uniqueSchedulesMap = new Map();
            res.data.forEach((item: any) => {
                const key = `${item.dateTime}-${item.beginTime}-${item.endTime}`;
                if (!uniqueSchedulesMap.has(key)) {
                    uniqueSchedulesMap.set(key, item);
                }
            });

            // 对去重后的数据进行排序
            const uniqueData = Array.from(uniqueSchedulesMap.values());
            uniqueData.sort((a: any, b: any) => new Date(a.dateTime).getTime() - new Date(b.dateTime).getTime());

            // 将去重并排序后的数据赋值给 termScheduleData
            termScheduleData.value = uniqueData;
        } else {
            termScheduleData.value = [];
        }
    } catch (error) {
        console.error('获取考期已排时间失败:', error);
        termScheduleData.value = [];
    }
};
</script>

<style scoped>
.left-part {
    /* width: 750px; */
    margin-right: 5px;
}

.form-item-margin {
    display: flex;
    justify-content: space-between;
}

.right-part {
    width: 310px;
    border-left: 1px solid #e8e8e8;
    padding-left: 8px;
    max-height: 500px;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.course-type-tag {
    display: inline-block;
    background-color: #f0f2f5;
    border-radius: 4px;
    padding: 2px 6px;
    margin-right: 4px;
    font-size: 12px;
    color: #1890ff;
    border: 1px solid #d9d9d9;
}

ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
}

li {
    list-style-type: none;
    margin: 0;
    padding: 0;
}

/* .right-part li:hover {
    background-color: #ccc;
} */

.delete-btn {
    cursor: pointer;
    color: black;
    transition: color 0.3s;
}

.delete-btn:hover {
    color: red;
}

.pointer-cursor {
    cursor: pointer;
}

.pointer-cursor:hover {
    background-color: #ccc;
}


/* 推荐课时区域样式 */
.schedule-list {
    width: 250px;
    border-left: 1px solid #e8e8e8;
    padding: 8px;
    max-height: 500px;
    display: flex;
    flex-direction: column;
    background-color: #fafafa;
    border-radius: 6px;
}

.fixed-header {
    position: sticky;
    top: 0;
    background-color: #fafafa;
    z-index: 10;
    /* padding: 8px 0 6px 0; */
    width: 100%;
    padding-right: 8px;
    border-bottom: 1px solid #e8e8e8;
}

/* 标题行样式 */
.header-title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 8px;
}

.copy-all-icon {
    font-size: 16px;
    cursor: pointer;
    transition: color 0.3s, transform 0.2s;
    padding: 2px;
}

.copy-all-icon:hover {
    transform: scale(1.1);
}

.copy-all-icon:active {
    transform: scale(0.95);
}

.fixed-header h3 {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    color: #262626;
    display: inline-block;
}

.schedule-tip {
    display: inline-block;
    margin-left: 5px;
}

.master-checkbox-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 8px 0 12px;
    width: 100%;
    padding: 4px 8px;
    background-color: #f9f9f9;
    border-radius: 4px;
    border: 1px solid #e8e8e8;
}

.selection-count {
    color: #999;
    font-size: 12px;
    font-weight: normal;
}

.selection-count.has-selected {
    color: #1890ff;
    font-weight: bold;
}

.select-all-actions {
    display: flex;
    gap: 8px;
}

.select-all-actions .ant-btn {
    padding: 0 4px;
    height: 22px;
    line-height: 22px;
}

.scrollable-content {
    overflow-y: auto;
    flex-grow: 1;
    max-height: 420px;
    padding: 4px 0;
}

.schedule-tag {
    display: inline-block;
    background-color: #e6f7ff;
    border: 1px solid #91d5ff;
    color: #096dd9;
    padding: 4px 8px;
    margin: 4px;
    border-radius: 4px;
    position: relative;
    max-width: 700px;
}

.recommended-schedule-item {
    margin-bottom: 4px;
    padding: 6px 8px;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    transition: all 0.2s ease;
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    background-color: #fff;
    font-size: 12px;
}

.schedule-checkbox {
    margin-right: 8px;
    margin-top: 2px;
}

.schedule-content {
    flex: 1;
    cursor: pointer;
}

.schedule-time {
    font-weight: 500;
    margin-bottom: 3px;
    font-size: 12px;
    color: #333;
}

.conflict-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 2px;
    margin-top: 2px;
}

.conflict-tags .ant-tag {
    margin-right: 4px;
}

.right-part li {
    list-style-type: none;
    margin: 0;
    padding: 0px 5px;
    border-radius: 4px;
}

.no-data {
    padding: 20px 10px;
    text-align: center;
    color: #999;
    font-size: 12px;
    background-color: #fff;
    border-radius: 4px;
    border: 1px dashed #e8e8e8;
    margin: 4px 0;
}

.scroll-container {
    max-height: 200px;
    overflow-y: auto;
    /* 添加更多样式 */
}

.schedule-tag {
    display: inline-block;
    background-color: #f5f5f5;
    border-radius: 4px;
    padding: 5px 8px;
    margin-right: 8px;
    margin-bottom: 8px;
    position: relative;
}

.delete-btn {
    margin-left: 5px;
    color: #999;
    cursor: pointer;
}

.delete-btn:hover {
    color: #ff4d4f;
}

.pointer-cursor {
    cursor: pointer;
}

.recommended-schedule-item {
    margin-bottom: 8px;
    padding: 8px;
    border: 1px solid #f0f0f0;
    border-radius: 4px;
    transition: background-color 0.3s;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.info-icon {
    color: #1890ff;
    margin-left: 5px;
    cursor: pointer;
}

.info-icon:hover {
    color: #40a9ff;
}

.term-tooltip {
    max-width: 300px;
}

.term-list-item {
    padding: 4px 0;
    border-bottom: 1px dashed #e8e8e8;
    white-space: nowrap;
    min-width: 120px;
}

.term-list-item:last-child {
    border-bottom: none;
}

.course-option {
    display: flex;
    align-items: center;
    width: 100%;
    padding: 4px 0;
}

.course-option:hover {
    background-color: #f5f5f5;
}

.term-group {
    margin-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 8px;
}

/* 该课程已排时间区域样式 */
.schedule-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    position: relative;
    max-height: 500px;
    background-color: #fafafa;
    border-radius: 6px;
    border: 1px solid #e8e8e8;
}

.schedule-header-fixed {
    position: sticky;
    top: 0;
    background: #fafafa;
    z-index: 10;
    padding: 8px 8px 6px 8px;
    border-bottom: 1px solid #e8e8e8;
    margin-bottom: 0;
}

.schedule-header-fixed h3 {
    margin: 0 0 6px 0;
    font-size: 14px;
    font-weight: 600;
    color: #262626;
}

.batch-actions-row {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 4px 0;
    background: #fafafa;
    border-bottom: 1px solid #e8e8e8;
    font-size: 12px;
}

.schedule-content-scrollable {
    flex: 1;
    overflow-y: auto;
    max-height: 400px;
    padding: 4px 8px;
}

.schedule-item {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 2px;
    padding: 5px;
    background-color: #fff;
    border-radius: 4px;
    border: 1px solid #e8e8e8;
    font-size: 12px;
    transition: all 0.2s ease;
    min-height: 35px;
    line-height: 35px;
}

.schedule-content {
    flex: 1;
}

.term-group:last-child {
    border-bottom: none;
}

.term-type-title {
    font-size: 14px;
    margin: 5px 0;
    color: #1890ff;
    border-left: 3px solid #1890ff;
    padding-left: 8px;
}

.course-term-conflict {
    font-weight: bold;
    border: 2px solid #ff4d4f;
    animation: pulse 1.5s infinite;
}

@keyframes pulse {
    0% {
        opacity: 1;
    }

    50% {
        opacity: 0.7;
    }

    100% {
        opacity: 1;
    }
}

/* 教师已排时间区域样式 */
.teacher-schedule-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    position: relative;
    max-height: 500px;
    background-color: #fafafa;
    border-radius: 6px;
    border: 1px solid #e8e8e8;
}

.teacher-schedule-header-fixed {
    position: sticky;
    top: 0;
    background: #fafafa;
    z-index: 10;
    padding: 8px 8px 6px 8px;
    border-bottom: 1px solid #e8e8e8;
    margin-bottom: 0;
}

.teacher-schedule-header-fixed h3 {
    margin: 0 0 6px 0;
    font-size: 14px;
    font-weight: 600;
    color: #262626;
}

.teacher-schedule-content-scrollable {
    flex: 1;
    overflow-y: auto;
    max-height: 420px;
    padding: 4px 8px;
}

.teacher-schedule-item {
    margin-bottom: 2px;
    padding: 5px;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    background-color: #fff;
    transition: all 0.2s ease;
    font-size: 12px;
    min-height: 35px;
    line-height: 35px;
}

.teacher-schedule-item:hover {
    background-color: #f5f5f5;
    border-color: #d9d9d9;
}

/* 禁用状态的样式 */
.recommended-schedule-item.disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.recommended-schedule-item.disabled:hover {
    transform: none;
    box-shadow: none;
}

.recommended-schedule-item.disabled .schedule-content {
    cursor: not-allowed;
}

/* 推荐课时项目hover效果 */
.recommended-schedule-item:hover {
    background-color: #f5f5f5;
    border-color: #d9d9d9;
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 课程已排时间项目hover效果 */
.schedule-item:hover {
    background-color: #f5f5f5;
    border-color: #d9d9d9;
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 统一滚动条样式 */
.scrollable-content::-webkit-scrollbar,
.schedule-content-scrollable::-webkit-scrollbar,
.teacher-schedule-content-scrollable::-webkit-scrollbar {
    width: 6px;
}

.scrollable-content::-webkit-scrollbar-track,
.schedule-content-scrollable::-webkit-scrollbar-track,
.teacher-schedule-content-scrollable::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.scrollable-content::-webkit-scrollbar-thumb,
.schedule-content-scrollable::-webkit-scrollbar-thumb,
.teacher-schedule-content-scrollable::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

.scrollable-content::-webkit-scrollbar-thumb:hover,
.schedule-content-scrollable::-webkit-scrollbar-thumb:hover,
.teacher-schedule-content-scrollable::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}

/* 阶段标题样式 */
.stage-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 10px 0 5px 0;
    font-weight: bold;
}

/* 复制按钮样式 */
.copy-btn {
    margin-left: 8px;
    color: #1890ff;
    cursor: pointer;
    font-size: 14px;
    opacity: 0.7;
    transition: all 0.2s ease;
    flex-shrink: 0;
}

.copy-btn:hover {
    opacity: 1;
    color: #40a9ff;
    transform: scale(1.1);
}

.schedule-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.schedule-content {
    flex: 1;
}

.week-day-tag {
    display: inline-block;
    margin-right: 8px;
    font-size: 14px;
    color: #1890ff;
}
</style>
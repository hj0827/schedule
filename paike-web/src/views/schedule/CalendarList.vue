<template>
    <FullCalendar ref="fullCalendar" class='demo-app-calendar' :options='calendarOptions'>
        <template v-slot:eventContent='arg'>
            <div class="text-ellipsis">
                <b>{{ arg.event.extendedProps.dateTime }} {{ arg.timeText }}</b>
                <br />
                <b>《{{ arg.event.extendedProps.courseName }}》 {{ formatLessonName(arg.event.extendedProps.lessonName) }}</b>
                <br />
                <b>{{ arg.event.extendedProps.courseType }}</b>
                <br />
                <b>{{ arg.event.extendedProps.teacherName }} {{ arg.event.extendedProps.roomName }}</b>
            </div>
        </template>
    </FullCalendar>
    <!-- 编辑日历 -->
    <CalendarEdit ref="editRef" @upSuccess="upSuccess"></CalendarEdit>
</template>

<script lang="ts" setup>
import CalendarEdit from './CalendarEdit.vue'
import { onMounted, reactive, ref } from 'vue'
import { getScheduleListForCalendarApi, updateCalendarApi } from '@/api/schedule/schedule'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin, { DateClickArg } from '@fullcalendar/interaction'
// import { CalendarOptions, EventClickArg, EventDropArg, EventResizeArg } from '@fullcalendar/core/index.js'
import { CalendarOptions, EventClickArg, EventDropArg } from '@fullcalendar/core';
type EventResizeArg = any;
import { EditType } from '@/type/BaseEnum'
import { ReMoveParm } from '@/api/schedule/ScheduleType'
import dayjs, { Dayjs } from 'dayjs'
import useTable from '@/composable/schedule/useTable'
import { message } from 'ant-design-vue'

// 编辑弹窗的ref属性
const editRef = ref()
// const { rolePage, tableList, columns, tableHeight, listParm, getList } = useTable()
// 日历的ref属性
const fullCalendar = ref()

// 上周
const prevWeekCustomClick = () => {
    fullCalendar.value.getApi().prev()
}
//下周
const nextWeekCustomClick = () => {
    fullCalendar.value.getApi().next()
}
// 今天
const tadayCustomCLick = () => {
    fullCalendar.value.getApi().today();
    fullCalendar.value.getApi().getDate();
}
//上月
const prevMonthCustomClick = () => {
    fullCalendar.value.getApi().prev();
}
//下月
const nextMonthCustomClick = () => {
    fullCalendar.value.getApi().next()
}
//本月
const tbisMonthCustomClick = () => {
    fullCalendar.value.getApi().today();
    fullCalendar.value.getApi().getDate;
}

// 日历新增点击事件
const addClick = (selectInfo: DateClickArg) => {
    console.log(selectInfo)
    const clickedDate = dayjs(selectInfo.date)
    const clickData = {
        dateTime: clickedDate.format('YYYY-MM-DD'),
        beginTime: clickedDate.format('HH:mm'),
        duration: 60 // 默认时长60分钟
    }
    editRef.value.editCalender(EditType.ADD, clickData)
}
// 日历点击编辑事件
const editClick = (clickInfo: EventClickArg) => {
    console.log("editClick 数据:", JSON.stringify(clickInfo.event.extendedProps, null, 2));

    editRef.value.editCalender(EditType.EDIT, clickInfo.event.extendedProps);
}

// 移动参数
const removeParm = reactive<any>({
    id: '',
    beginTime: '',
    endTime: '',
    dateTime: '',
    duration: '',
    courseType: '', // 考期
})
// 移动事件
const removeClic = async (info: EventDropArg) => {
    console.log("移动事件：" + JSON.stringify(info.event.extendedProps, null, 2));
    console.log("移动事件id："+info.event.id)
    const targetDate = dayjs(info.event.start).format('YYYY-MM-DD');

    // 检查 calendarOptions.events 是否存在且为数组
    const targetEvents = Array.isArray(calendarOptions.events)
        ? calendarOptions.events.filter(event =>
            event.extendedProps && // 确保 extendedProps 存在
            dayjs(event.start as any).format('YYYY-MM-DD') === targetDate &&
            info.event.id != event.id
        )
        : [];
    // 打印目标事件的 extendedProps
    targetEvents.forEach(event => {
        console.log("目标事件：" + JSON.stringify(event.extendedProps, null, 2));
        console.log("目标id"+event.id)
    });
    // 检查目标位置是否有相同类型的课程且时间段是否有冲突
    const hasSameCourseTypeAndTimeConflict = targetEvents.some(event =>
        event.extendedProps?.courseType === info.event.extendedProps?.courseType &&
        dayjs(event.start as any).isBefore(dayjs(info.event.end)) &&
        dayjs(event.end as any).isAfter(dayjs(info.event.start))
    );

    if (hasSameCourseTypeAndTimeConflict) {
        message.warning('目标位置已有相同类型的课程且时间段冲突，无法移动');
        info.revert(); // 回滚事件到原始位置
        return; // 阻止移动
    }

    removeParm.id = info.event.id;
    removeParm.dateTime = targetDate;
    removeParm.beginTime = dayjs(info.event.start).format('HH:mm:ss');
    removeParm.endTime = dayjs(info.event.end).format('HH:mm:ss');

    // 更新数据
    let res = await updateCalendarApi(removeParm) as any;
    if (res.code == 200) {
        // 刷新日历
        getScheduleList();
    } else {
        // 如果更新失败，回滚事件
        info.revert();
        message.error('更新失败，请重试');
    }
}

// 日历属性
const calendarOptions = reactive({
    plugins: [
        dayGridPlugin,
        timeGridPlugin,
        interactionPlugin
    ],
    // 自定义按钮
    customButtons: {
        prevWeekCustom: {
            text: '上周',
            click: function () {
                prevWeekCustomClick();
            }
        },
        nextWeekCustom: {
            text: '下周',
            click: function () {
                nextWeekCustomClick();
            }
        },
        tadayCustom: {
            text: '本周',
            click: function () {
                tadayCustomCLick();
            }
        },
        prevMonthCustom: {
            text: '上月',
            click: function () {
                prevMonthCustomClick();
            }
        },
        nextMonthCustom: {
            text: '下月',
            click: function () {
                nextMonthCustomClick();
            }
        },
        tbisMonthCustom: {
            text: '本月',
            click: function () {
                tbisMonthCustomClick();
            }
        }
    },
    headerToolbar: {
        start: 'timeGridWeek,prevWeekCustom,todayCustom,nextWeekCustom',
        right: 'dayGridMonth,prevMonthCustom,thisMonthCustom,nextMonthCustom',
        center: 'title'
    },
    // 修改headerToolbar的文字
    buttonText: {
        today: '今天',
        month: '月视图',
        week: '周视图',
        day: '日',
        list: '周列表'
    },
    // 设置日历的高度
    contentHeight: window.innerHeight - 260,
    // 默认为那个视图（月：dayGridMonth,周：timeGridWeek，日：timeGridDay）
    initialView: 'timeGridWeek',
    // 切换语言
    locale: 'zh-cn',
    // 月视图的显示模式，fixed 固定显示 周高：liquid:高度随周数变化；variable 高度固定
    weekMode: 'liquid',
    // 设置一周中显示的第一天是那天，周日是0，周一是1
    firstDay: 1,
    // 时间轴间距
    slotMinTime: '08:00',
    slotMaxTime: '24:00',
    slotDuration: '00:' + 15,
    slotLabelFormat: {
        hour: 'numeric',
        minute: '2-digit',
        hour12: false
    },
    eventTimeFormat: {
        hour: 'numeric',
        minute: '2-digit',
        hour12: false
    },
    // 日历数据
    events: [],
    // Event是否可以进行（拖动、缩放）修改
    editable: true,
    //是否显示全天插槽
    allDaySlot: false,
    //是否显示当前时间标记
    nowIndicator: true,
    //是否随浏览器窗口大小变化而自动变化
    handleWindowResize: true,
    //日期是否可点击
    navLinks: true,
    //月视图，是否为指定周数高度
    fixedWeekCount: true,
    //月视图，是否显示非本月日期
    showNonCurrentDates: false,
    //设置是否可被单击选中
    selectable: false,
    selectMirror: true,
    dayMaxEvents: true,
    weekends: true,
    //新增事件
    dateClick: addClick,
    eventClick: editClick,
    eventDrop: removeClic,

    // 新增事件拉伸处理函数
    eventResize: async (resizeInfo: EventResizeArg) => {
        console.log(resizeInfo);
        const event = resizeInfo.event;
        const updatedEvent = {
            id: event.id,
            dateTime: dayjs(event.start).format('YYYY-MM-DD'),
            beginTime: dayjs(event.start).format('HH:mm:ss'),
            endTime: dayjs(event.end).format('HH:mm:ss'),
            duration: dayjs(event.end).diff(event.start, 'minute'),
        };
        // 更新数据
        let res = await updateCalendarApi(updatedEvent as any) as any;
        if (res.code == 200) {
            // 刷新日历
            getScheduleList();
        } else {
            // 如果更新失败，回滚事件
            resizeInfo.revert();
            message.error('更新失败，请重试');
        }
    },
} as CalendarOptions)

// 日历获取参数
const calendarParams = reactive({
    roomIdList: [],
    courseIdList: [],
    teacherIdList: [],
    startDate: '',
    endDate: '',
    beginTime: '',
    endTime: '',
    duration: 0,
    roomId: '',
    courseId: '',
    teacherId: ''
    // 不设置分页参数，使用非分页查询获取全部数据
});

// 获取日历数据
const getScheduleList = async (params: any = {}) => {
    // 合并默认参数和传入参数
    const finalParams = { ...calendarParams, ...params };
    console.log("发送请求参数:", JSON.stringify(finalParams, null, 2));
    let res = await getScheduleListForCalendarApi(finalParams) as any;
    console.log("接收到响应:", JSON.stringify(res, null, 2));
    if (res && res.code == 200) {
        // 日历专用API直接返回数组
        const dataList = res.data || [];
        console.log('实际数据列表:', dataList, '数据条数:', dataList.length);

        if (dataList && dataList.length > 0) {
        // 清空数据
        calendarOptions.events = [];
        // 用于存储分组后的数据
        const groupedData: { [key: string]: any[] } = {};

        // 循环数据
        for (let i = 0; i < dataList.length; i++) {
            if (dataList[i]) {
                let obj = {
                    id: '',
                    title: '',
                    start: '',
                    end: '',
                    backgroundColor: '',
                    className: '',
                    extendedProps: {} as any,
                    courseType: '',
                };

                // 获取 courseColor 的信息
                let color = dataList[i].courseColor;
                let classes = dataList[i].courseColor;
                obj.backgroundColor = color;
                obj.className = classes;

                // 设置 obj 的其他属性
                obj.id = dataList[i].id;
                obj.title = `${dataList[i].courseName} ${dataList[i].teacherName} ${dataList[i].roomName} ${dataList[i].roomAddress}`;
                obj.start = `${dataList[i].dateTime} ${dataList[i].beginTime}`;
                obj.end = `${dataList[i].dateTime} ${dataList[i].endTime}`;
                obj.extendedProps = dataList[i];
                obj.courseType = dataList[i].courseType;

                // 分组
                const key = `${dataList[i].courseType}-${dataList[i].courseName}`;
                if (!groupedData[key]) {
                    groupedData[key] = [];
                }
                groupedData[key].push(obj);
            } else {
                console.warn(`Item at index ${i} is undefined.`);
            }
        }

        // 为每个分组内的事件分配课时编号
        for (const key in groupedData) {
            const group = groupedData[key];
            group.forEach((item, index) => {
                item.extendedProps.lessonNumber = index + 1;
                if (Array.isArray(calendarOptions.events)) {
                    calendarOptions.events.push(item);
                } else {
                    console.error('calendarOptions.events is not an array');
                }
            });
        }
        } else {
            console.log('没有数据，清空事件数组');
            // 没有数据时也要清空事件数组，但不显示警告
        }
    } else {
        console.error('API响应错误:', res);
        calendarOptions.events = [];
    }
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

// 新增、编辑成功后刷新数据
const upSuccess = () => {
    getScheduleList();
};

onMounted(() => {
    getScheduleList();
});

defineExpose({
    getScheduleList
});
</script>

<style lang="scss" scoped>
.text-ellipsis {
    white-space: nowrap;
    /* 强制文本不换行 */
    overflow: hidden;
    /* 隐藏超出部分的内容 */
    text-overflow: ellipsis;
    /* 当文本超出容器宽度时，用...代替超出部分 */
}

.text-ellipsis>b {
    margin-top: 5px;
}
</style>
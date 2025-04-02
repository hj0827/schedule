<template>
    <FullCalendar ref="fullCalendar" class='demo-app-calendar' :options='calendarOptions'>
        <template v-slot:eventContent='arg'>
            <div class="text-ellipsis">
                <b>《{{ arg.event.extendedProps.courseName }}》</b>
                <br />
                <b>{{ arg.event.extendedProps.teacherName }}</b>
                <br />
                <b>{{ arg.event.extendedProps.roomName }}</b>
            </div>
        </template>
    </FullCalendar>

    <a-modal :visible="editModalVisible" destroyOnClose="true" @cancel="onClose">
        <template #footer>
        </template>
        <!-- 内容 -->
        <div style="overflow-y: auto;overflow-x: initial;">
            <h2>课程信息</h2>
            <div v-if="eventData">
                <p>ID: {{ eventData.id }}</p>
                <p>课程名称: {{ eventData.courseName }}</p>
                <p>教师姓名: {{ eventData.teacherName }}</p>
                <p>日期：{{ eventData.dateTime }}</p>
                <p>开始时间: {{ eventData.beginTime }}</p>
                <p>结束时间: {{ eventData.endTime }}</p>
                <p>房间ID: {{ eventData.roomId }}</p>
                <p>房间名称: {{ eventData.roomName }}</p>
            </div>
        </div>
    </a-modal>
</template>

<script lang="ts" setup>
import {DocumentTitle} from '@/type/BaseEnum'
import { onMounted, reactive, ref } from 'vue'
import { getScheduleListApi, updateCalendarApi } from '@/api/schedule/schedule'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin, { DateClickArg } from '@fullcalendar/interaction'
import { CalendarOptions, EventClickArg } from '@fullcalendar/core/index.js'
import dayjs, { Dayjs } from 'dayjs'

// 用于存储点击获取到的数据
const eventData = ref({
    id: '',
    dateTime: '',
    roomId: '',
    teacherId: '',
    courseId: '',
    beginTime: '',
    endTime: '',
    duration: 0,
    courseName: '',
    teacherName: '',
    roomName: ''
})

// 控制编辑弹窗是否可见的响应式数据
const editModalVisible = ref(false);
// 关闭弹窗
// 关闭弹窗
const onClose = () => {
    editModalVisible.value = false;
};

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
    fullCalendar.value.getapi().today();
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

// 日历点击编辑事件
// 日历点击编辑事件
const editClick = (clickInfo: EventClickArg) => {
    const eventTitle = clickInfo.event.extendedProps;
    console.log(eventTitle);

    // 补充缺失的属性
    const completeEventData = {
        id: eventTitle.id || '',
        dateTime: eventTitle.dateTime || '',
        roomId: eventTitle.roomId || '',
        teacherId: eventTitle.teacherId || '',
        courseId: eventTitle.courseId || '',
        beginTime: eventTitle.beginTime || '',
        endTime: eventTitle.endTime || '',
        duration: eventTitle.duration || 0,
        courseName: eventTitle.courseName || '',
        teacherName: eventTitle.teacherName || '',
        roomName: eventTitle.roomName || ''
    };

    // 将获取到的数据存储到eventData中用于展示
    eventData.value = completeEventData;

    // 设置编辑弹窗可见
    editModalVisible.value = true;
}


// 移动参数
const removeParm = reactive<any>({
    id: '',
    beginTime: '',
    endTime: '',
    dateTime: '',
})
// 移动事件
const removeClic = async (item: EventClickArg) => {
    console.log(item)
    removeParm.id = item.event.id;
    removeParm.dateTime = dayjs(item.event.start).format('YYYY-MM-DD');
    removeParm.beginTime = dayjs(item.event.start).format('HH:mm:ss');
    removeParm.endTime = dayjs(item.event.end).format('HH:mm:ss');
    // 更新数据
    let res = await updateCalendarApi(removeParm) as any
    if (res.code == 200) {
        // 刷新日历
        getScheduleList()
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
    contentHeight: window.innerHeight -200,
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
    editable: false,
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
    // dateClick: addClick,
    eventClick: editClick,
    eventDrop: removeClic
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
    duration: 0
})

// 获取日历数据
const getScheduleList = async () => {
    let res = await getScheduleListApi(calendarParams) as any;
    if (res && res.code == 200 && res.data.length > 0) {
        console.log("数据1")
        console.log(res.data)
        // 清空数据
        calendarOptions.events = []
        // 循环数据
        for (let i = 0; i < res.data.length; i++) {
            // 确保 res.data[i] 存在
            if (res.data[i]) {
                let obj = {
                    id: '',
                    title: '',
                    start: '',
                    end: '',
                    backgroundColor: '',
                    className: '',
                    // 编辑时数据
                    extendedProps: {}
                };

                console.log("数据3");
                console.log(res.data);

                // 获取 courseColor 的信息
                let color = res.data[i].courseColor;
                console.log("color", color)
                let classes = res.data[i].courseColor;
                console.log("classes", classes)
                obj.backgroundColor = color;
                obj.className = classes;

                // 设置 obj 的其他属性
                obj.id = res.data[i].id;
                obj.title = `${res.data[i].courseName} ${res.data[i].teacherName} ${res.data[i].roomName} ${res.data[i].roomAddress}`;
                obj.start = `${res.data[i].dateTime} ${res.data[i].beginTime}`;
                obj.end = `${res.data[i].dateTime} ${res.data[i].endTime}`;
                obj.extendedProps = res.data[i];

                // 将 obj 添加到事件数组
                calendarOptions.events.push(obj);
            } else {
                console.warn(`Item at index ${i} is undefined.`);
            }
        }

        console.log("数据2");
        console.log(calendarOptions.events);
    }

}

onMounted(() => {
    getScheduleList()
    document.title = DocumentTitle.index;
})

defineExpose({
    getScheduleList
})
</script>

<style lang="scss" scoped>
.text-ellipsis {
    white-space: nowrap; /* 强制文本不换行 */
    overflow: hidden; /* 隐藏超出部分的内容 */
    text-overflow: ellipsis; /* 当文本超出容器宽度时，用...代替超出部分 */
}

</style>

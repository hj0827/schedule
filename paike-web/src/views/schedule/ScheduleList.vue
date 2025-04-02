<template>
    <LoadingOverlay :visible="loading" />
    <a-form layout="inline" style="margin-bottom: 14px;">
        <a-form-item label="课程名称">
            <a-input v-model:value="listParm.courseName" placeholder="请输入课程名称"></a-input>
        </a-form-item>
        <a-form-item label="考期">
            <a-input v-model:value="listParm.courseType" placeholder="请输入考期"></a-input>
        </a-form-item>
        <a-form-item label="教师">
            <a-input v-model:value="listParm.teacherName" placeholder="请输入教师名称"></a-input>
        </a-form-item>
        <a-button @click="searchBtn" class="margin-left">
            <template #icon>
                <SearchOutlined />
            </template>
            搜索
        </a-button>
        <a-button @click="resetBtn" type="danger" class="margin-left">
            <template #icon>
                <reload-outlined />
            </template>
            重置
        </a-button>
        <a-button v-permission="['sys:scheduleList:export']" @click="exportExcel" type="primary" class="margin-left">
            <template #icon>
                <file-excel-outlined />
            </template>
            导出课时
        </a-button>
        
        <a-button v-permission="['sys:schedule:delete']" type="danger" @click="deleteSelectedUsers"
            style="margin-right: 10px;" class="margin-left">
            <template #icon>
                <delete-outlined />
            </template>
            删除选中
        </a-button>
    </a-form>
    <!-- 表格列表 -->
    <a-table :dataSource="tableList.list" :columns="columns" :pagination="rolePage" bordered
        :scroll="{ y: tableHeight }" :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: 'checkbox' }"
        :rowKey="(record: ScheduleModel) => record.id">
        <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'courseType'">
                <a-tag v-if="record.courseType === '0'" color="green">春季</a-tag>
                <a-tag v-if="record.courseType === '1'" color="blue">秋季</a-tag>
            </template>
            <template v-if="column.key === 'courseColor'">
                <div v-if="record.courseColor"
                    :style="{ height: '20px', width: '100%', backgroundColor: record.courseColor.substring(record.courseColor.indexOf('-') + 1, record.courseColor.length) }">
                </div>
            </template>
            <template v-if="column.key === 'action'">
                <a-button v-permission="['sys:course:edit']" @click="editBtn(record)" type="primary">
                    <template #icon>
                        <edit-outlined />
                    </template>
                    编辑
                </a-button>
                <a-button v-permission="['sys:schedule:delete']" @click="deleteBtn(record)" style="margin-left: 10px;"
                    type="danger">
                    <template #icon>
                        <delete-outlined />
                    </template>
                    删除
                </a-button>
            </template>
        </template>
    </a-table>
    <!-- 新增、编辑弹窗 -->
    <EditSchedule ref="addRef" @refreshList="getList" />
</template>

<script lang="ts" setup>
import LoadingOverlay from '@/components/loadingOverlay.vue';
import * as XLSX from 'xlsx';
import { onMounted, reactive, ref } from 'vue'
import EditSchedule from './EditSchedule.vue';
import useTable from '@/composable/schedule/useTable';
import useSchedule from '@/composable/schedule/useSchedule';
import { DocumentTitle } from '@/type/BaseEnum';

// 表格相关的操作
const { rolePage, tableList, columns, tableHeight, listParm, getList, loading } = useTable()

// 新增、编辑
const selectedRowKeys = ref<string[]>([]);
const onSelectChange = (selectedKeys: string[]) => {
    selectedRowKeys.value = selectedKeys;
};

const { editBtn, addRef, searchBtn, resetBtn, deleteBtn, deleteSelectedUsers } = useSchedule(getList, listParm, selectedRowKeys)

// 表单绑定的对象
const addModel = reactive({
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
})

// 导出Excel方法
const exportExcel = () => {
    // 定义表头
    const headers = [
        { key: 'courseType', title: '考期' },
        { key: 'majorName', title: '专业名称' },
        { key: 'courseName', title: '课程名称' },
        { key: 'durationName', title: '课时名称' },
        { key: 'teacherName', title: '教师名称' },
        { key: 'dateTime', title: '上课日期' },
        { key: 'beginTime', title: '上课时间' },
        { key: 'endTime', title: '下课时间' },
        { key: 'startDate', title: '开课日期' },
        { key: 'duration', title: '时长' },
        { key: 'courseYear', title: '学年' },
        { key: 'roomName', title: '直播间名称' },
        { key: 'roomAddress', title: '直播间地址' },
        { key: 'courseColor', title: '课程颜色' },
    ];

    // 创建一个包含表头和数据的数组
    const data = [headers.map(header => header.title)];

    // 使用 Map 进行分组
    const groupedData = new Map<string, any[]>();

    tableList.list.forEach(item => {
        const key = `${item.courseType}-${item.courseName}`;
        if (!groupedData.has(key)) {
            groupedData.set(key, []);
        }
        groupedData.get(key)!.push(item);
    });

    // 遍历每个分组
    groupedData.forEach((items, key) => {
        const courseName = items[0].courseName;

        items.forEach((item, index) => {
            const durationName = `${courseName}课时${index + 1}`;

            // 格式化 beginTime 和 endTime
            const formattedBeginTime = formatTime(item.beginTime);
            const formattedEndTime = formatTime(item.endTime);
            const startDate = durationName + "：" + item.dateTime + "【" + formattedBeginTime + "-" + formattedEndTime + "】";
            const row = headers.map(header => {
                switch (header.key) {
                    case 'durationName':
                        return durationName;
                    case 'beginTime':
                        return formattedBeginTime;
                    case 'endTime':
                        return formattedEndTime;
                    case 'startDate':
                        return startDate;
                    default:
                        return item[header.key];
                }
            });
            data.push(row);
        });
    });

    // 将数据转换为工作表
    const worksheet = XLSX.utils.aoa_to_sheet(data);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');
    XLSX.writeFile(workbook, 'schedule_list.xlsx');
}

// 格式化时间函数
const formatTime = (time: string): string => {
    const [hours, minutes] = time.split(':').map(Number);
    const formattedHours = hours.toString().padStart(2, '0');
    const formattedMinutes = minutes.toString().padStart(2, '0');
    return `${formattedHours}:${formattedMinutes}`;
}

onMounted(() => {
    document.title = DocumentTitle.ScheduleList; // 设置页面标题
})
</script>

<style scoped lang="scss">
.select-width {
    width: 120px;
}

.margin-left {
    margin-left: 10px;
}

:deep(.ant-table-pagination){
    margin: 5px;
}

.ant-table-pagination {
    margin: 5px;
}


</style>
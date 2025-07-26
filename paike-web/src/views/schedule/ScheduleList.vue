<template>
<LoadingOverlay :visible="loading || loading1" />
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
        :rowKey="(record: ScheduleModel) => record.id" :expandedRowKeys="expandedRowKeys" @expandedRowsChange="handleExpandedRowsChange">
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
import { ScheduleModel } from '@/api/schedule/ScheduleType';

// 表格相关的操作
const { rolePage, tableList, columns, tableHeight, listParm, getList, loading, expandedRowKeys } = useTable()

// 处理表格行展开/折叠事件
const handleExpandedRowsChange = (keys: string[]) => {
  expandedRowKeys.value = keys;
};

// 新增、编辑
const selectedRowKeys = ref<string[]>([]);
const onSelectChange = (selectedKeys: string[]) => {
    selectedRowKeys.value = selectedKeys;
};

const {loading1,editBtn, addRef, searchBtn, resetBtn, deleteBtn, deleteSelectedUsers } = useSchedule(getList, listParm, selectedRowKeys)

// Function to recursively flatten the tree data
const flattenData = (nodes: any[], result: any[] = []) => {
    nodes.forEach(node => {
        // Add the current node to the result list
        // Make a shallow copy to avoid modifying the original data structure
        const itemToAdd = { ...node };
        // Remove the children property if it exists, as it's not needed for the flattened list
        delete itemToAdd.children;
        result.push(itemToAdd);

        // If the node has children, recursively process them
        if (node.children && Array.isArray(node.children)) {
            flattenData(node.children, result);
        }
    });
    return result;
};

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
        { key: '课程分类', title: '课程分类' },
        { key: 'courseType', title: '第一考期' },
        { key: '学期', title: '学期' },
        { key: 'courseName', title: '课程名称' },
        { key: '课程id', title: '课程id' },
        { key: '阶段名称', title: '阶段名称' },
        { key: 'lessonName', title: '课时名称' },
        { key: '上课方式', title: '上课方式' },
        { key: '回放有效期', title: '回放有效期' },
        { key: 'teacherName', title: '教师' },
        { key: 'dateTime', title: '上课日期' },
        { key: 'beginTime', title: '上课时间' },
        { key: 'endTime', title: '下课时间' },
        { key: '视频VIDS（上课方式为硬盘推流时填写）', title: '视频VIDS（上课方式为硬盘推流时填写）' },
        { key: '腾讯云回放链接（上课方式为录播时填写）', title: '腾讯云回放链接（上课方式为录播时填写）' },
        { key: '答疑区话题', title: '答疑区话题' },
        { key: '上课基数', title: '上课基数' },
        { key: '聊天室', title: '聊天室' },
        { key: '私聊', title: '私聊' },
        { key: '是否启用', title: '是否启用' },
        { key: '课后打卡', title: '课后打卡' },
        { key: '课后打卡说明', title: '课后打卡说明' },
        { key: '发布圈子', title: '发布圈子' },
        { key: '绑定话题', title: '绑定话题' },
        { key: 'majorName', title: '专业名称' },
        { key: 'startDate', title: '开课日期（用于贴到在线表）' },
        { key: 'duration', title: '时长' },
        { key: 'courseYear', title: '学年' },
        { key: 'roomName', title: '直播间名称' },
        // { key: 'roomAddress', title: '直播间地址' },
        // { key: 'courseColor', title: '课程颜色' },
    ];

    // Flatten the table list to include all parent and child nodes
    const allItemsToExport = flattenData(tableList.list);

    // 对数据进行排序：教师、课程名称、第一考期、上课日期、上课时间、下课时间
    allItemsToExport.sort((a, b) => {
        // 1. 按教师名称排序
        if (a.teacherName !== b.teacherName) {
            return (a.teacherName || '').localeCompare(b.teacherName || '');
        }

        // 2. 按课程名称排序
        if (a.courseName !== b.courseName) {
            return (a.courseName || '').localeCompare(b.courseName || '');
        }

        // 3. 按第一考期排序（春季=0，秋季=1）
        if (a.courseType !== b.courseType) {
            const aType = a.courseType === '0' ? '春季' : (a.courseType === '1' ? '秋季' : a.courseType || '');
            const bType = b.courseType === '0' ? '春季' : (b.courseType === '1' ? '秋季' : b.courseType || '');
            return aType.localeCompare(bType);
        }

        // 4. 按上课日期排序
        if (a.dateTime !== b.dateTime) {
            return (a.dateTime || '').localeCompare(b.dateTime || '');
        }

        // 5. 按上课时间排序
        if (a.beginTime !== b.beginTime) {
            return (a.beginTime || '').localeCompare(b.beginTime || '');
        }

        // 6. 按下课时间排序
        return (a.endTime || '').localeCompare(b.endTime || '');
    });

    // 创建一个包含表头和数据的数组
    const data = [headers.map(header => header.title)];

    // Use the flattened list directly
    allItemsToExport.forEach((item, index) => {
        // Ensure lessonName is used if available, otherwise default
        const durationName = item.lessonName;
        // const durationName = `${courseName}课时${index + 1}`;
        // 格式化 beginTime 和 endTime
        const formattedBeginTime = formatTime(item.beginTime);
        const formattedEndTime = formatTime(item.endTime);

        // Construct the startDate string (adjust format if needed)
         const startDate = item.dateTime && formattedBeginTime && formattedEndTime
                          ? `${durationName}：${item.dateTime}【${formattedBeginTime}-${formattedEndTime}】`
                          : durationName; // Fallback if date/times are missing

        // 构造每一行的数据并填充默认值
        const row = headers.map(header => {
            switch (header.key) {
                case '课程分类':
                    return '常规课';
                case '上课方式':
                    return '直播';
                case '回放有效期':
                    return '17520';
                case '上课基数':
                    return '0';
                case '聊天室':
                    return '启用';
                case '私聊':
                    return '禁用';
                case '是否启用':
                    return '是';
                case '课后打卡':
                    return '启用';
                case '课后打卡说明':
                    return '课后打卡';
                case '发布圈子':
                    return '自考圈';
                case '答疑区话题':
                    // 使用teacherDesc + "答疑专区"
                    return item.teacherDesc ? item.teacherDesc + '答疑专区' : '';
                 case 'lessonName': // Map data to the '课时名称' header
                    // 构成：课程名称+课时名称
                    const courseName = item.courseName || '';
                    const lessonName = item.lessonName || durationName || '';
                    return courseName && lessonName ? `${courseName}${lessonName}` : (lessonName || courseName);
                case 'beginTime':
                    return formattedBeginTime;
                case 'endTime':
                    return formattedEndTime;
                case 'startDate':
                    return startDate;
                 case 'courseType':
                    // Map courseType '0' to '春季', '1' to '秋季' for export
                    return item.courseType === '0' ? '春季' : (item.courseType === '1' ? '秋季' : item.courseType || '');
                case '阶段名称':
                    // 使用stage_table表的description字段
                    return item.stageDescription || '';
                default:
                    return item[header.key] || ''; // 默认为空字符串
            }
        });
        data.push(row);
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
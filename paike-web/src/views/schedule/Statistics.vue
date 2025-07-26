<template>
    <div class="statistics-container">
        <a-form layout="inline" style="margin-bottom: 15px;">
            <!-- 教师 -->
            <a-form-item label="教师">
                <a-select 
                    v-model:value="listParm.teacherId" 
                    show-search 
                    placeholder="请选择教师" 
                    style="width: 300px;"
                    :options="teacherOptions" 
                    :filter-option="filterTeacheroption" 
                    allowClear 
                    mode="multiple"
                    :max-tag-count="3"
                    :max-tag-placeholder="(count: number) => `+${count}个教师`"
                >
                    <template #maxTagPlaceholder="{ omittedValues }">
                        <span>...还有{{ omittedValues.length }}个教师</span>
                    </template>
                </a-select>
            </a-form-item>
            <!-- 日期范围 -->
            <a-form-item label="日期范围">
                <a-range-picker 
                    v-model:value="dateRange"
                    :format="dateFormat"
                    @change="onDateChange"
                    allowClear
                />
            </a-form-item>
            <a-button style="margin-right: 10px;" @click="searchData">
                <template #icon>
                    <search-outlined />
                </template>
                搜索
            </a-button>
            <a-button type="primary" @click="exportToExcel">
                <template #icon>
                    <download-outlined />
                </template>
                导出Excel
            </a-button>
        </a-form>

        <!-- 表格 -->
        <a-table 
            :dataSource="tableData" 
            :columns="columns"
            :loading="loading"
            :scroll="{ x: 800 }"
            bordered>
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'totalHours'">
                    <span style="color: #1890ff; font-weight: bold;">{{ record.totalHours }}</span>
                </template>
                <template v-if="column.key === 'courseDetails'">
                    <a-button type="link" @click="showCourseDetails(record.courseDetails)">
                        查看课程详情
                    </a-button>
                </template>
                <template v-if="column.key === 'action'">
                    <a-button type="link" @click="showDetails(record)">查看课时详情</a-button>
                </template>
            </template>
        </a-table>

        <!-- 详情弹窗 -->
        <a-modal
            v-model:visible="detailsVisible"
            :title="'课时详情 - ' + (currentTeacher?.teacherName || '')"
            width="800px"
            @cancel="closeDetails"
            class="details-modal"
        >
            <a-table
                :dataSource="currentTeacherDetails"
                :columns="detailColumns"
                :pagination="{ 
                    pageSize: 10,
                    size: 'small',
                    showSizeChanger: false
                }"
                bordered
            >
            </a-table>
            <template #footer>
                <a-button type="primary" @click="exportTeacherDetails">
                    导出当前教师详情
                </a-button>
            </template>
        </a-modal>

        <!-- 课程详情弹窗 -->
        <a-modal
            v-model:visible="courseDetailsVisible"
            title="课程详情"
            width="600px"
            @cancel="() => courseDetailsVisible = false"
            class="course-details-modal"
        >
            <a-table
                :dataSource="currentCourseDetails"
                :columns="courseDetailColumns"
                :pagination="false"
                bordered
            >
            </a-table>
            <template #footer>
                <a-button type="primary" @click="() => courseDetailsVisible = false">
                    关闭
                </a-button>
            </template>
        </a-modal>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, reactive } from 'vue';
import { DocumentTitle } from '@/type/BaseEnum';
import useselect from '../../composable/schedule/useSelect';
import useTable from '@/composable/schedule/useTable';
import { getScheduleInfoApi } from '@/api/schedule/schedule';
import dayjs from 'dayjs';
import * as XLSX from 'xlsx';
import ExcelJS from 'exceljs';
import { DownloadOutlined, SearchOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { StatisticsQueryParams } from '../../api/schedule/ScheduleType';

const { listParm, getList } = useTable();
const { teacherOptions, filterTeacheroption } = useselect(getList, listParm);

// 日期范围和格式
const dateRange = ref<[dayjs.Dayjs, dayjs.Dayjs] | null>(null);
const dateFormat = 'YYYY-MM-DD';
const loading = ref(false);

// 表格数据
const tableData = ref<any[]>([]);

// 详情弹窗相关
const detailsVisible = ref(false);
const currentTeacher = ref<any>(null);
const currentTeacherDetails = ref<any[]>([]);

// 课程详情弹窗相关
const courseDetailsVisible = ref(false);
const currentCourseDetails = ref<any[]>([]);

// 详情表格列定义
const detailColumns = [
    {
        title: '日期',
        dataIndex: 'date',
        key: 'date',
        width: 120,
    },
    {
        title: '时间',
        dataIndex: 'time',
        key: 'time',
        width: 180,
    },
    {
        title: '课时',
        dataIndex: 'course',
        key: 'course',
    }
];

// 课程详情表格列定义
const courseDetailColumns = [
    {
        title: '课程名称',
        dataIndex: 'courseName',
        key: 'courseName',
        width: 200
    },
    {
        title: '课时数',
        dataIndex: 'hours',
        key: 'hours',
        width: 100,
        align: 'center'
    }
];

// 主表格列定义
const columns = [
    {
        title: '教师姓名',
        dataIndex: 'teacherName',
        key: 'teacherName',
        fixed: 'left',
        width: 120,
    },
    {
        title: '总课时数',
        dataIndex: 'totalHours',
        key: 'totalHours',
        width: 100,
        sorter: (a: any, b: any) => a.totalHours - b.totalHours,
    },
    {
        title: '课程详情',
        dataIndex: 'courseDetails',
        key: 'courseDetails',
        ellipsis: true,
        width: 200,
    },
    {
        title: '课时详情',
        key: 'action',
        fixed: 'right',
        width: 200,
    }
];

// 添加展开行的状态管理
const expandedRows = ref<string[]>([]);

// 切换展开状态
const toggleExpand = (teacherName: string) => {
    const index = expandedRows.value.indexOf(teacherName);
    if (index === -1) {
        expandedRows.value.push(teacherName);
    } else {
        expandedRows.value.splice(index, 1);
    }
};

// 显示详情
const showDetails = (record: any) => {
    currentTeacher.value = record;
    currentTeacherDetails.value = record.details;
    detailsVisible.value = true;
};

// 关闭详情
const closeDetails = () => {
    detailsVisible.value = false;
    currentTeacher.value = null;
    currentTeacherDetails.value = [];
};

// 导出当前教师详情
const exportTeacherDetails = async () => {
    if (!currentTeacher.value || !currentTeacherDetails.value?.length) return;

    const teacher = currentTeacher.value;

    // 从课时数据中提取涉及的月份
    const monthsSet = new Set<string>();
    currentTeacherDetails.value.forEach(detail => {
        const month = dayjs(detail.date).format('M'); // 获取月份，不带前导零
        monthsSet.add(month);
    });

    // 将月份转换为数组并排序
    const months = Array.from(monthsSet).sort((a: string, b: string) => parseInt(a) - parseInt(b));

    // 生成月份字符串
    const monthsStr = months.map(month => `${month}月`).join('、');

    const filename = `${teacher.teacherName}-${monthsStr}教师教学工作量.xlsx`;

    // 创建工作簿和工作表
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('课时详情');

    // 设置列宽
    worksheet.columns = [
        { width: 8 },   // 序号
        { width: 12 },  // 专职教师
        { width: 8 },   // 项目
        { width: 30 },  // 教学工作
        { width: 12 },  // 教学方式
        { width: 15 },  // 日期
        { width: 8 }    // 课时
    ];

    // 第一行：标题
    const titleRow = worksheet.getRow(1);
    titleRow.getCell(1).value = '教研与教学部专职老师教学工作量明细表';
    worksheet.mergeCells('A1:G1');
    titleRow.getCell(1).alignment = { horizontal: 'center', vertical: 'middle' };
    titleRow.getCell(1).font = { name: '宋体', size: 11, bold: true };
    titleRow.height = 20; // 设置行高为20磅

    // 第二行：制表日期
    const currentDate = dayjs().format('YYYY-MM-DD');
    const dateRow = worksheet.getRow(2);
    dateRow.getCell(1).value = `制表日期: ${currentDate}`;
    worksheet.mergeCells('A2:G2');
    dateRow.getCell(1).alignment = { horizontal: 'right', vertical: 'middle' }; // 改为右对齐
    dateRow.getCell(1).font = { name: '宋体', size: 11 };
    dateRow.height = 20; // 设置行高为20磅

    // 第三行：表头
    const headerRow = worksheet.getRow(3);
    const headers = ['序号', '专职教师', '项目', '教学工作', '教学方式', '日期', '课时'];
    headers.forEach((header, index) => {
        const cell = headerRow.getCell(index + 1);
        cell.value = header;
        cell.alignment = { horizontal: 'center', vertical: 'middle' };
        cell.font = { name: '宋体', size: 11, bold: true };
    });
    headerRow.height = 20; // 设置行高为20磅

    // 对数据进行排序：先按教学工作排序，再按日期排序
    const sortedDetails = [...currentTeacherDetails.value].sort((a, b) => {
        const courseWorkA = `${a['课程']}${a.course}`;
        const courseWorkB = `${b['课程']}${b.course}`;

        // 先按教学工作排序
        if (courseWorkA !== courseWorkB) {
            return courseWorkA.localeCompare(courseWorkB, 'zh-CN');
        }

        // 教学工作相同时，按日期排序
        return a.date.localeCompare(b.date);
    });

    // 计算总课时
    let totalHours = 0;
    let currentRow = 4;

    // 数据行
    sortedDetails.forEach((detail, index) => {
        const courseWork = `${detail['课程']}${detail.course}`;
        const dataRow = worksheet.getRow(currentRow);

        dataRow.getCell(1).value = index + 1;
        dataRow.getCell(2).value = ''; // 专职教师列为空（已在表头显示）
        dataRow.getCell(3).value = '自考';
        dataRow.getCell(4).value = courseWork;
        dataRow.getCell(5).value = '直播';
        dataRow.getCell(6).value = detail.date;
        dataRow.getCell(7).value = 1.0; // 课时格式为数字1.0

        // 设置单元格样式
        for (let col = 1; col <= 7; col++) {
            const cell = dataRow.getCell(col);
            cell.alignment = { horizontal: 'center', vertical: 'middle' };
            cell.font = { name: '宋体', size: 11 };
        }

        // 设置课时列为数字格式
        const hoursCell = dataRow.getCell(7);
        hoursCell.numFmt = '0.0'; // 设置数字格式为一位小数

        dataRow.height = 20; // 设置行高为20磅

        totalHours += 1;
        currentRow++;
    });

    // 合并专职教师列（从第4行到数据结束行，保留第3行表头）
    if (currentTeacherDetails.value.length > 0) {
        worksheet.mergeCells(`B4:B${currentRow - 1}`);
        const teacherCell = worksheet.getCell('B4');
        teacherCell.value = teacher.teacherName;
        teacherCell.alignment = { horizontal: 'center', vertical: 'middle' };
        teacherCell.font = { name: '宋体', size: 11, bold: true };
    }

    // 合计行
    const totalRow = worksheet.getRow(currentRow);
    totalRow.getCell(1).value = '合计';
    totalRow.getCell(7).value = parseFloat(totalHours.toFixed(1)); // 转换为数字格式
    worksheet.mergeCells(`A${currentRow}:F${currentRow}`);

    // 设置合计行样式
    for (let col = 1; col <= 7; col++) {
        const cell = totalRow.getCell(col);
        cell.alignment = { horizontal: 'center', vertical: 'middle' };
        cell.font = { name: '宋体', size: 11 };
    }

    // 设置合计行课时列为数字格式
    const totalHoursCell = totalRow.getCell(7);
    totalHoursCell.numFmt = '0.0'; // 设置数字格式为一位小数

    totalRow.height = 20; // 设置行高为20磅

    currentRow++;

    // 审批行
    const approvalRow = worksheet.getRow(currentRow);
    approvalRow.getCell(1).value = '审批：                          审核：                          制表人：             ';
    worksheet.mergeCells(`A${currentRow}:G${currentRow}`);
    approvalRow.getCell(1).alignment = { horizontal: 'center', vertical: 'middle' };
    approvalRow.getCell(1).font = { name: '宋体', size: 11 };
    approvalRow.height = 20; // 设置行高为20磅

    // 设置所有单元格边框
    for (let row = 1; row <= currentRow; row++) {
        for (let col = 1; col <= 7; col++) {
            const cell = worksheet.getCell(row, col);
            cell.border = {
                top: { style: 'thin' },
                left: { style: 'thin' },
                bottom: { style: 'thin' },
                right: { style: 'thin' }
            };
        }
    }

    // 导出文件
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
};

// 导出所有数据
const exportToExcel = () => {
    if (!tableData.value?.length) return;
    
    const filename = `教师课时统计_${dayjs().format('YYYYMMDD')}.xlsx`;
    
    // 准备概览数据
    const summaryData = tableData.value.map(item => ({
        '教师姓名': item.teacherName,
        '总课时数': item.totalHours,
        '课程详情': item.courseDetails,
    }));

    // 准备课时详情数据
    const courseDetailsData = tableData.value.flatMap(teacher => {
        // 解析课程详情字符串
        const courseDetails = teacher.courseDetails.split('、').map((detail: string) => {
            const [courseName, count] = detail.split(': ');
            return {
                '教师姓名': teacher.teacherName,
                '课程名称': courseName,
                '课时数': parseInt(count),
            };
        });
        return courseDetails;
    });
    
    // 准备详细数据
    const detailsData = tableData.value.flatMap(teacher =>
        teacher.details.map((detail: any) => ({
            '教师姓名': teacher.teacherName,
            '日期': detail.date,
            '时间': detail.time,
            '课程名称': detail['课程'],
            '课时名称': detail.course,
        }))
    );
    
    const wb = XLSX.utils.book_new();
    
    // 添加概览sheet
    if (summaryData.length > 0) {
        const wsSummary = XLSX.utils.json_to_sheet(summaryData);
        XLSX.utils.book_append_sheet(wb, wsSummary, '课时统计概览');
        wsSummary['!cols'] = [
            { wch: 15 }, // 教师姓名
            { wch: 10 }, // 总课时数
            { wch: 50 }, // 课程详情
        ];
    }

    // 添加课程统计sheet
    if (courseDetailsData.length > 0) {
        const wsCourseDetails = XLSX.utils.json_to_sheet(courseDetailsData);
        XLSX.utils.book_append_sheet(wb, wsCourseDetails, '课程统计明细');
        wsCourseDetails['!cols'] = [
            { wch: 15 }, // 教师姓名
            { wch: 30 }, // 课程名称
            { wch: 10 }, // 课时数
        ];
    }
    
    // 添加详情sheet
    if (detailsData.length > 0) {
        const wsDetails = XLSX.utils.json_to_sheet(detailsData);
        XLSX.utils.book_append_sheet(wb, wsDetails, '详细课时信息');
        wsDetails['!cols'] = [
            { wch: 15 }, // 教师姓名
            { wch: 15 }, // 日期
            { wch: 15 }, // 时间
            { wch: 30 }, // 课程名称
            { wch: 30 }, // 课时名称
        ];
    }
    
    XLSX.writeFile(wb, filename);
};

// 查询参数
const queryParams = reactive<StatisticsQueryParams>({
    courseName: '',
    courseType: '',
    teacherName: '',
    startDate: '',
    endDate: '',
    teacherId: '',
    courseId: '',
    lessonName: ''
});

// 日期范围变化处理
const onDateChange = (dates: [dayjs.Dayjs, dayjs.Dayjs] | null) => {
    if (dates) {
        queryParams.startDate = dates[0].format(dateFormat);
        queryParams.endDate = dates[1].format(dateFormat);
    } else {
        queryParams.startDate = '';
        queryParams.endDate = '';
    }
};

// 处理数据
const processData = (data: any[]) => {
    const teacherStats = new Map<string, any>();

    // 获取日期范围
    const startDate = dayjs(listParm.startDate);
    const endDate = dayjs(listParm.endDate);

    data.forEach(item => {
        const { teacherName, courseName, dateTime, beginTime, endTime, lessonName } = item;
        
        // 检查日期是否在范围内
        const currentDate = dayjs(dateTime);
        if (currentDate.isBefore(startDate) || currentDate.isAfter(endDate)) {
            return; // 跳过不在日期范围内的数据
        }
        
        if (!teacherStats.has(teacherName)) {
            teacherStats.set(teacherName, {
                teacherName,
                totalHours: 0,
                courses: new Map<string, Set<string>>(),
                timeSlots: new Set<string>(),
                details: []
            });
        }

        const teacherData = teacherStats.get(teacherName)!;
        const timeSlotKey = `${dateTime} ${beginTime}-${endTime}`;
        
        // 只有当这个时间段还没被计算过时才增加课时
        if (!teacherData.timeSlots.has(timeSlotKey)) {
            teacherData.totalHours++;
            teacherData.timeSlots.add(timeSlotKey);
            
            // 统计课程
            if (!teacherData.courses.has(courseName)) {
                teacherData.courses.set(courseName, new Set());
            }
            teacherData.courses.get(courseName)!.add(timeSlotKey);
            
            // 记录详情
            teacherData.details.push({
                date: dateTime,
                time: `${beginTime}-${endTime}`,
                course: `${courseName}${lessonName}`,
                '日期': dateTime,
                '时间': `${beginTime}-${endTime}`,
                '课程': courseName
            });
        }
    });

    // 转换为表格数据格式
    return Array.from(teacherStats.values()).map(stats => {
        // 按日期排序详情
        stats.details.sort((a: any, b: any) => {
            const dateCompare = a.date.localeCompare(b.date);
            if (dateCompare !== 0) return dateCompare;
            return a.time.localeCompare(b.time);
        });

        return {
            teacherName: stats.teacherName,
            totalHours: stats.totalHours,
            courseDetails: (Array.from(stats.courses.entries()) as [string, Set<string>][])
                .map((entry: [string, Set<string>]) => `${entry[0]}: ${entry[1].size}课时`)
                .join('、'),
            details: stats.details
        };
    }).sort((a, b) => b.totalHours - a.totalHours); // 默认按总课时降序排序
};

// 搜索数据
const searchData = async () => {
    loading.value = true;
    try {
        // 处理日期范围
        if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
            queryParams.startDate = dateRange.value[0].format(dateFormat);
            queryParams.endDate = dateRange.value[1].format(dateFormat);
        } else {
            queryParams.startDate = '';
            queryParams.endDate = '';
        }

        // 处理教师ID，支持多选
        if (Array.isArray(listParm.teacherId) && listParm.teacherId.length > 0) {
            queryParams.teacherId = listParm.teacherId.join(',');
        } else if (listParm.teacherId) {
            queryParams.teacherId = String(listParm.teacherId);
        } else {
            queryParams.teacherId = '';
        }

        // console.log("搜索参数", queryParams);
        const res = await getScheduleInfoApi(queryParams) as any;
        console.log("搜索结果1", res);
        
        if (res && res.code === 200) {
            if (!res.data || res.data.length === 0) {
                message.info('未找到符合条件的数据');
                tableData.value = [];
                return;
            }
            tableData.value = processData(res.data);
            if (tableData.value.length === 0) {
                message.info('未找到符合条件的数据');
            }
        } else {
            message.error(res?.msg || '获取数据失败');
            tableData.value = [];
        }
    } catch (error: any) {
        console.error('获取数据失败:', error);
        if (error.code === 'ERR_NETWORK') {
            message.error('网络连接失败，请检查网络连接或联系管理员');
        } else {
            message.error(error.message || '获取数据失败，请稍后重试');
        }
        tableData.value = [];
    } finally {
        loading.value = false;
    }
};

// 显示课程详情
const showCourseDetails = (details: string) => {
    // 将字符串格式的课程详情转换为表格数据
    const detailsArray = details.split('、').map(item => {
        const [courseName, hoursStr] = item.split(': ');
        const hours = hoursStr.replace('课时', '');
        return {
            courseName,
            hours
        };
    });
    currentCourseDetails.value = detailsArray;
    courseDetailsVisible.value = true;
};

onMounted(() => {
    document.title = DocumentTitle.statistics;
    
    try {
        // 初始化时不设置日期范围，直接查询所有数据
        searchData();
    } catch (error) {
        console.error('初始化失败:', error);
        message.error('页面初始化失败，请刷新重试');
    }
});
</script>

<style scoped lang="scss">
.statistics-container {
    padding-top: 0px;
    
    :deep(.ant-table-wrapper) {
        .ant-spin-nested-loading {
            .ant-spin-container {
                .ant-table {
                    .ant-table-container {
                        .ant-table-body {
                            max-height: 500px;
                            overflow-y: hidden;
                        }
                        
                        .ant-table-tbody {
                            .ant-table-row {
                                height: 54px;
                                
                                .ant-table-cell {
                                    padding: 8px;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    :deep(.details-modal) {
        .ant-modal-body {
            padding: 12px;
            height: 480px;
            
            .ant-table-wrapper {
                height: 100%;
                
                .ant-table {
                    height: 100%;
                    
                    .ant-table-container {
                        .ant-table-content {
                            .ant-table-tbody {
                                .ant-table-row {
                                    height: 48px;
                                    
                                    .ant-table-cell {
                                        padding: 8px;
                                        white-space: nowrap;
                                        min-width: 0;
                                        
                                        &:nth-child(2) {  // 时间列
                                            font-family: monospace;  // 使用等宽字体
                                            letter-spacing: -0.5px;  // 稍微调整字间距
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        .ant-pagination {
            margin: 16px 0 0;
        }
    }

    :deep(.ant-table-cell) {
        white-space: pre-line;
        word-break: break-all;
    }

    :deep(.course-details-modal) {
        .ant-modal-body {
            padding: 12px;
            
            .ant-table-wrapper {
                .ant-table {
                    .ant-table-container {
                        .ant-table-content {
                            .ant-table-tbody {
                                .ant-table-row {
                                    height: 48px;
                                    
                                    .ant-table-cell {
                                        padding: 8px;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>
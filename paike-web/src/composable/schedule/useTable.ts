// 业务处理 将可以复用的业务放在一块
import { nextTick, onMounted, reactive, ref, computed } from "vue";
import { getScheduleInfoApi, getScheduleListApi } from "@/api/schedule/schedule";
import { ListParm } from "../../api/schedule/ScheduleType";

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

// 定义 API 返回的数据结构
interface ApiResponse {
    code: number;
    data?: any;
    msg?: string;
}

export default function useTable() {
    // 获取表格数据
    const tableHeight = ref(0);
    //   表格数据
    const tableList = reactive({
        list: [] as any[]
    });

    // 控制默认展开的行
    const expandedRowKeys = ref<string[]>([]);

    // 表格的列
    const columns = [
        {
            title: '课程名称',
            dataIndex: 'courseName',
            key: 'courseName',
            align: 'center'
        },
        {
            title: '课时名称',
            dataIndex: 'lessonName',
            key: 'lessonName',
            align: 'center',

        },
        {
            title: '专业名称',
            dataIndex: 'majorName',
            key: 'majorName',
            align: 'center'
        },
        {
            title: '考期',
            dataIndex: 'courseType',
            key: 'courseType',
            align: 'center',
            width: 220
        },
        {
            title: '远智课程ID',
            dataIndex: 'yuanzhiCourseId',
            key: 'yuanzhiCourseId',
            align: 'center',
            width: 120
        },
        {
            title: '教师',
            dataIndex: 'teacherName',
            key: 'teacherName',
            align: 'center',
            width: 80
        },
        {
            title: '上课日期',
            dataIndex: 'dateTime',
            key: 'dateTime',
            align: 'center',
            width: 110
        },
        {
            title: '上课时间',
            dataIndex: 'beginTime',
            key: 'beginTime',
            customRender: ({ text }: { text: string }) => {
                if (!text) return '--';
                try {
                    const [hours, minutes] = text.split(':');
                    return `${hours.padStart(2, '0')}:${minutes}`;
                } catch (error) {
                    console.warn('无效的上课时间格式:', text);
                    return text || '--';
                }
            },
            width: 90,
            align: 'center'
        },
        {
            title: '下课时间',
            dataIndex: 'endTime',
            key: 'endTime',
            customRender: ({ text }: { text: string }) => {
                if (!text) return '--';
                try {
                    const [hours, minutes] = text.split(':');
                    return `${hours.padStart(2, '0')}:${minutes}`;
                } catch (error) {
                    console.warn('无效的下课时间格式:', text);
                    return text || '--';
                }
            },
            align: 'center',
            width: 90,
        },
        {
            title: '直播间',
            dataIndex: 'roomName',
            key: 'roomName',
            align: 'center',
            width: 90,
        },
        {
            title: '操作',
            align: 'center',
            key: 'action',
            width: 220,
        },
    ];

    // 分页
    const rolePage = reactive({
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '40', '50', '500'],
        showTotal: (total: number) => `共有${total}条数据`,
        onChange: (current: number, size: number) => {
            listParm.currentPage = current;
            listParm.pageSize = size;
            rolePage.current = current;
            rolePage.pageSize = size;
            getList();
        }
    });

    // 列表查询的参数
    const listParm = reactive<ListParm>({
        roomIdList: [],
        courseIdList: [],
        teacherIdList: [],
        startDate: '',
        endDate: '',
        beginTime: '',
        endTime: '',
        duration: '',
        courseName: '',
        courseType: '',
        currentPage: 1,
        teacherName: '',
        delId: '',
        teacherId: '',
        roomId: '',
        lessonName: '',
    });

    // 加载状态
    const loading = ref(false);

    // Define the structure of schedule items
    interface ScheduleItem {
        dateTime: string;
        beginTime: string;
        endTime: string;
        // Add other properties of item if known, otherwise use 'any'
        [key: string]: any;
    }

    // Declare roomScheduleData and teacherScheduleData
    const roomScheduleData = ref<ScheduleItem[]>([]);
    const teacherScheduleData = ref<ScheduleItem[]>([]);

    // Refs to control the visibility of schedule details
    const showRoomScheduleDetails = ref(false);
    const showTeacherScheduleDetails = ref(false);

    // Functions to toggle visibility
    const toggleRoomScheduleDetails = () => {
        showRoomScheduleDetails.value = !showRoomScheduleDetails.value;
    };
    const toggleTeacherScheduleDetails = () => {
        showTeacherScheduleDetails.value = !showTeacherScheduleDetails.value;
    };

    // 教室已排时间去重（按日期+时间段）
    const uniqueRoomScheduleData = computed(() => {
        const map = new Map<string, ScheduleItem>();
        roomScheduleData.value.forEach((item: ScheduleItem) => {
            const key = `${item.dateTime}_${item.beginTime}_${item.endTime}`;
            if (!map.has(key)) {
                map.set(key, item);
            }
        });
        return Array.from(map.values());
    });

    // 教师已排时间去重（按日期+时间段）
    const uniqueTeacherScheduleData = computed(() => {
        const map = new Map<string, ScheduleItem>();
        teacherScheduleData.value.forEach((item: ScheduleItem) => {
            const key = `${item.dateTime}_${item.beginTime}_${item.endTime}`;
            if (!map.has(key)) {
                map.set(key, item);
            }
        });
        return Array.from(map.values());
    });

    // 转换为树形结构
    const transformToScheduleTree = (data: any[]): any[] => {
        if (!Array.isArray(data)) {
            console.error('transformToScheduleTree: 输入不是数组');
            return [];
        }

        if (data.length === 0) {
            console.log('transformToScheduleTree: 输入数组为空');
            return [];
        }

        try {
            const map = new Map();
            const roots: any[] = [];

            // First level: Group by courseName
            data.forEach(item => {
                if (!item || typeof item !== 'object') {
                    console.warn('无效的数据项:', item);
                    return;
                }

                const courseKey = item.courseName || 'Unknown Course';
                if (!map.has(courseKey)) {
                    map.set(courseKey, {
                        key: `course-${courseKey}`,
                        title: courseKey,
                        courseName: courseKey,
                        courseType: item.courseType || '',
                        children: new Map()
                    });
                    roots.push(map.get(courseKey));
                }

                // Second level: Group by teacherName
                const courseGroup = map.get(courseKey);
                const teacherKey = item.teacherName || 'Unknown Teacher';
                if (!courseGroup.children.has(teacherKey)) {
                    courseGroup.children.set(teacherKey, {
                        key: `${courseKey}-${teacherKey}`,
                        title: teacherKey,
                        teacherName: teacherKey,
                        children: new Map()
                    });
                }

                // Third level: Group by dateTime
                const teacherGroup = courseGroup.children.get(teacherKey);
                const dateKey = item.dateTime || 'Unknown Date';
                if (!teacherGroup.children.has(dateKey)) {
                    teacherGroup.children.set(dateKey, {
                        key: `${courseKey}-${teacherKey}-${dateKey}`,
                        title: dateKey,
                        dateTime: dateKey,
                        children: []
                    });
                }

                // Fourth level: Add time slots
                const dateGroup = teacherGroup.children.get(dateKey);
                const lessonPrefix = formatLessonName(item.lessonName);
                const timeSlot = {
                    key: item.id || `time-${Date.now()}-${Math.random()}`,
                    title: lessonPrefix ? `${lessonPrefix}：${item.beginTime || '00:00'} - ${item.endTime || '00:00'}` : `${item.beginTime || '00:00'} - ${item.endTime || '00:00'}`,
                    ...item
                };
                dateGroup.children.push(timeSlot);
            });

            // Convert Maps to arrays for the tree structure
            const processChildren = (node: any) => {
                if (node.children instanceof Map) {
                    node.children = Array.from(node.children.values()).map(child => {
                        return processChildren(child);
                    });
                }
                return node;
            };

            const result = roots.map(root => processChildren(root));
            console.log('树形结构转换完成，根节点数量:', result.length);
            return result;

        } catch (error) {
            console.error('转换树形结构时出错:', error);
            if (error instanceof Error) {
                console.error('错误详情:', error.message);
                console.error('错误堆栈:', error.stack);
            }
            return [];
        }
    };

    // 表格数据查询
    const getList = async () => {
        loading.value = true;
        try {
            let res = await getScheduleInfoApi(listParm) as ApiResponse;
            console.log('API响应数据:', JSON.stringify(res, null, 2)); // 完整打印响应数据

            if (!res) {
                console.warn('API 响应为空');
                tableList.list = [];
                rolePage.total = 0;
                return;
            }

            if (res.code !== 200) {
                console.warn('API 响应状态码不是 200:', res.code);
                tableList.list = [];
                rolePage.total = 0;
                return;
            }

            // 验证并提取数据
            let records: any[] = [];
            let totalCount = 0;

            if (!res.data) {
                console.warn('API 响应中没有 data 字段');
                tableList.list = [];
                rolePage.total = 0;
                return;
            }

            if (Array.isArray(res.data)) {
                records = res.data;
                totalCount = records.length; // 如果是数组格式，总数就是数组长度
                console.log('数据是数组格式，长度:', records.length);
            } else if (typeof res.data === 'object') {
                if (Array.isArray(res.data.records)) {
                    records = res.data.records;
                    totalCount = res.data.total || records.length; // 优先使用后端返回的total
                    console.log('数据在 records 字段中，长度:', records.length, '总数:', totalCount);
                } else if (Array.isArray(res.data.list)) {
                    records = res.data.list;
                    totalCount = res.data.total || records.length; // 优先使用后端返回的total
                    console.log('数据在 list 字段中，长度:', records.length, '总数:', totalCount);
                } else {
                    console.warn('在响应中未找到有效的数据数组');
                    tableList.list = [];
                    rolePage.total = 0;
                    return;
                }
            } else {
                console.warn('响应数据格式不正确');
                tableList.list = [];
                rolePage.total = 0;
                return;
            }

            // 验证记录数组的有效性
            if (!Array.isArray(records)) {
                console.warn('提取的记录不是数组');
                tableList.list = [];
                rolePage.total = 0;
                return;
            }

            if (records.length === 0) {
                console.log('记录数组为空');
                tableList.list = [];
                rolePage.total = 0;
                return;
            }

            // 验证记录中的必要字段
            const validRecords = records.filter(record => {
                if (!record || typeof record !== 'object') {
                    console.warn('无效的记录:', record);
                    return false;
                }
                // del_id is not a required field for all records
                const requiredFields = ['courseName', 'teacherName', 'dateTime', 'beginTime', 'endTime'];
                const missingFields = requiredFields.filter(field => !record[field]);
                if (missingFields.length > 0) {
                    console.warn(`记录缺少必要字段 ${missingFields.join(', ')}:`, record);
                    return false;
                }
                return true;
            });

            if (validRecords.length === 0) {
                console.warn('没有有效的记录数据');
                tableList.list = [];
                rolePage.total = 0;
                return;
            }

            console.log('有效记录数:', validRecords.length);
            console.log('第一条记录示例:', JSON.stringify(validRecords[0], null, 2));

            // --- Start: Add sorting logic ---
            validRecords.sort((a, b) => {
                // Sort by courseName
                const courseNameCompare = (a.courseName || '').localeCompare(b.courseName || '');
                if (courseNameCompare !== 0) {
                    return courseNameCompare;
                }
                // Then by courseType
                const courseTypeCompare = (a.courseType || '').localeCompare(b.courseType || '');
                if (courseTypeCompare !== 0) {
                    return courseTypeCompare;
                }
                // Then by teacherName
                const teacherNameCompare = (a.teacherName || '').localeCompare(b.teacherName || '');
                if (teacherNameCompare !== 0) {
                    return teacherNameCompare;
                }
                // Then by dateTime
                const dateTimeCompare = (a.dateTime || '').localeCompare(b.dateTime || '');
                if (dateTimeCompare !== 0) {
                    return dateTimeCompare;
                }
                // Then by beginTime
                const beginTimeCompare = (a.beginTime || '').localeCompare(b.beginTime || '');
                if (beginTimeCompare !== 0) {
                    return beginTimeCompare;
                }
                // Finally by endTime
                return (a.endTime || '').localeCompare(b.endTime || '');
            });
            // --- End: Add sorting logic ---

            // --- Start: Add tree transformation logic based on del_id ---
            const groupedByDelId = new Map<string, any[]>();
            const topLevelNodes: any[] = [];

            validRecords.forEach(record => {
                if (record.delId) {
                    if (!groupedByDelId.has(record.delId)) {
                        groupedByDelId.set(record.delId, []);
                    }
                    groupedByDelId.get(record.delId)!.push(record);
                } else {
                    topLevelNodes.push(record);
                }
            });

            const treeData: any[] = [];

            // Process groups with delId
            const defaultExpandedKeys: string[] = []; // Temporary array to collect keys

            groupedByDelId.forEach((items, delId) => {
                if (items.length > 1) {
                    // Use the first item as the parent node
                    const parentNode = items[0];
                    // Ensure the parent node has a key property matching the table's rowKey
                    parentNode.key = parentNode.id; // Use the original id as the key

                    // The rest are children
                    const childrenNodes = items.slice(1);
                    parentNode.children = childrenNodes;
                    // Add a flag to identify parent nodes if necessary
                    parentNode.isParent = true;
                    treeData.push(parentNode);
                    defaultExpandedKeys.push(parentNode.key); // Add parent key to expanded list
                } else {
                    // If a delId group has only one item, treat it as a top-level node
                    treeData.push(items[0]);
                }
            });

            // Add records without delId as top-level nodes
            treeData.push(...topLevelNodes);

            tableList.list = treeData;
            // For tree data, use the total from backend response for correct pagination
            // Don't use validRecords.length as it includes child nodes which breaks pagination
            rolePage.total = totalCount;

            // Set the default expanded row keys
            expandedRowKeys.value = defaultExpandedKeys;

            // --- End: Add tree transformation logic based on del_id ---

        } catch (error) {
            console.error('获取数据失败:', error);
            if (error instanceof Error) {
                console.error('错误详情:', error.message);
                console.error('错误堆栈:', error.stack);
            }
            tableList.list = [];
            rolePage.total = 0;
        } finally {
            loading.value = false;
        }
    };

    const handleCourseChange = async (params: any) => {
        console.log(`selected ${params}`);
        listParm.courseId = params;
        try {
            let res = await getScheduleListApi(listParm) as ApiResponse;
            if (res && res.code === 200) {
                // 返回查询到的数据
                return res.data;
            }
        } catch (error) {
            console.error('获取课程排程信息失败', error);
        }
        return [];
    };

    onMounted(() => {
        // 表格数据查询
        getList();
        // 计算表格高度
        nextTick(() => {
            tableHeight.value = window.innerHeight - 300;
        });
    });

    return {
        tableHeight,
        tableList,
        rolePage,
        listParm,
        columns,
        getList,
        loading,
        handleCourseChange,
        expandedRowKeys,
        // Expose new refs and functions
        uniqueRoomScheduleData,
        uniqueTeacherScheduleData,
        showRoomScheduleDetails,
        toggleRoomScheduleDetails,
        showTeacherScheduleDetails,
        toggleTeacherScheduleDetails,
        // also expose the data refs if they are populated and used directly
        roomScheduleData,
        teacherScheduleData
    };
}
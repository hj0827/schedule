// 业务处理 将可以复用的业务放在一块
import { nextTick } from "vue";
import { getScheduleInfoApi,getScheduleListApi } from "@/api/schedule/schedule";
import { onMounted, reactive, ref } from "vue";

// 定义 API 返回的数据结构
interface ApiResponse {
    code: number;
    data?: any;
    message?: string;
}


export default function useTable() {
    // 获取表格数据
    const tableHeight = ref(0);
    //   表格数据
    const tableList = reactive({
        list: [] as any[]
    });
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
            dataIndex: 'durationName',
            key: 'durationName',
            align: 'center'
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
                const [hours, minutes] = text.split(':');
                return `${hours.padStart(2, '0')}:${minutes}`;
            },
            width: 90,
            align: 'center'
        },
        {
            title: '下课时间',
            dataIndex: 'endTime',
            key: 'endTime',
            customRender: ({ text }: { text: string }) => {
                const [hours, minutes] = text.split(':');
                return `${hours.padStart(2, '0')}:${minutes}`;
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
    const listParm = reactive({
        courseName: '',
        courseType: '',
        currentPage: 1,
        pageSize: 10,
        roomIdList: [],
        courseIdList: [],
        teacherIdList: [],
        startDate: '',
        endDate: '',
        beginTime: '',
        endTime: '',
        roomId: '',
        courseId: '',
        teacherId: '',
        teacherName: '',
    });

    // 加载状态
    const loading = ref(false);

    // 动态添加 durationName 字段的函数
    const addDurationName = (data: any[]) => {
        const groupedData = new Map<string, any[]>();

        data.forEach(item => {
            const key = `${item.courseType}-${item.courseName}`;
            if (!groupedData.has(key)) {
                groupedData.set(key, []);
            }
            groupedData.get(key)!.push(item);
        });

        groupedData.forEach((items, key) => {
            const courseName = items[0].courseName;

            items.forEach((item, index) => {
                item.durationName = `${courseName}课时${index + 1}`;
            });
        });

        return data;
    };
    // 表格数据查询
    const getList = async () => {
        loading.value = true; // 显示加载动画
        try {
            console.log("搜索" + JSON.stringify(listParm));
            let res = await getScheduleInfoApi(listParm) as ApiResponse;
            if (res && res.code === 200) {
                // 将查询到的数据赋值给表格数据
                tableList.list = res.data;
                // 动态添加 durationName 字段
                tableList.list = addDurationName(tableList.list);
                // 分页总条数
                rolePage.total = res.data.total;
            }
        } finally {
            loading.value = false; // 隐藏加载动画
        }
    };

    // 点击相应课程时显示已经排好的课程（根据课程id来发送请求）
    // const handleCourseChange = async(params: any) => {
    //     console.log(`selected ${params}`);
    //     listParm.courseId = params;
    //     let res = await getScheduleListApi(listParm) as any;
    //     console.log(res);
    // };

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
        handleCourseChange
    };
}
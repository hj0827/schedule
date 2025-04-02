// 业务处理 将可以复用的业务放在一块
import { nextTick } from "vue";
import { getListApi } from "../../api/course/course";
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
            key: 'courseName'
        },
        {
            title: '专业名称',
            dataIndex: 'majorName',
            key: 'majorName'
        },
        {
            title: '考期',
            dataIndex: 'courseType',
            key: 'courseType'
        },
        {
            title: '学年',
            dataIndex: 'courseYear',
            key: 'courseYear',
            width: 65
        },
        {
            title: '合并上课',
            dataIndex: 'isMergeClasses',
            key: 'isMergeClasses',
            width: 90
        },
        {
            title: '背景色',
            dataIndex: 'courseColor',
            key: 'courseColor',
            width: 75
        },
        {
            title: '操作',
            align: 'center',
            key: 'action',
            width: 220
        },
    ];

    // 分页
    const rolePage = reactive({
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '40', '50'],
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
        currentPage: rolePage.current,
        pageSize: rolePage.pageSize
    });

    // 表格数据查询
    const getList = async () => {
        let res = await getListApi(listParm) as ApiResponse;
        if (res && res.code === 200) {
            console.log(res);
            // 将查询到的数据赋值给表格数据
            tableList.list = transformToTree(res.data.records);
            // 分页总条数
            rolePage.total = res.data.total;
        }
    };

    // 转换为树形结构
    const transformToTree = (data: any[]): any[] => {
        const map = new Map();
        const roots: any[] = [];

        data.forEach(item => {
            map.set(item.courseId, { ...item, children: [] });
        });

        data.forEach(item => {
            if (item.parentCourseId) {
                const parent = map.get(item.parentCourseId);
                if (parent) {
                    parent.children.push(map.get(item.courseId));
                } else {
                    // 如果找不到父节点，将当前节点视为顶级节点
                    roots.push(map.get(item.courseId));
                }
            } else {
                roots.push(map.get(item.courseId));
            }
        });

        // 删除所有节点中的空 children 属性
        const cleanTree = (nodes: any[]) => {
            return nodes.map(node => {
                if (node.children.length === 0) {
                    delete node.children; // 删除空的 children 属性
                } else {
                    node.children = cleanTree(node.children);
                }
                return node;
            });
        };

        return cleanTree(roots);
    };

    onMounted(() => {
        // 表格数据查询
        getList();
        // 计算表格高度
        nextTick(() => {
            tableHeight.value = window.innerHeight - 310;
        });
    });

    return {
        tableHeight,
        tableList,
        rolePage,
        listParm,
        columns,
        getList
    };
}
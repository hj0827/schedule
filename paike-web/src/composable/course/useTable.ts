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
    // 控制展开行的状态
    const expandedRowKeys = ref<string[]>([]);
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
            title: '远智课程ID',
            dataIndex: 'yuanzhiCourseId',
            key: 'yuanzhiCourseId',
            width: 120
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
        currentPage: rolePage.current,
        pageSize: rolePage.pageSize
    });

    // 表格数据查询
    const getList = async () => {
        let res = await getListApi(listParm) as ApiResponse;
        if (res && res.code === 200) {
            console.log(res);
            
            // 获取原始记录总数（不包括子记录）
            const originalTotal = res.data.total;
            
            // 将查询到的数据赋值给表格数据
            const treeData = transformToTree(res.data.records);
            tableList.list = treeData;
            
            // 设置要展开的行
            expandedRowKeys.value = res.data.records
                .filter((item: any) => item.parentCourseId === null || item.parentCourseId === '')
                .map((item: any) => item.courseId);
                
            // 使用原始总数而不是转换后的树形结构长度
            rolePage.total = originalTotal;
        }
    };

    // 转换为树形结构
    const transformToTree = (data: any[]): any[] => {
        // 先创建一个Map来存储所有节点
        const map = new Map();
        const roots: any[] = [];

        // 第一遍循环，将所有节点添加到Map中
        data.forEach(item => {
            map.set(item.courseId, { ...item, children: [] });
        });

        // 第二遍循环，构建树形结构
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

    // 处理展开/收起
    const onExpand = (expanded: boolean, record: any) => {
        if (expanded) {
            expandedRowKeys.value.push(record.courseId);
        } else {
            const index = expandedRowKeys.value.indexOf(record.courseId);
            if (index > -1) {
                expandedRowKeys.value.splice(index, 1);
            }
        }
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
        getList,
        expandedRowKeys,
        onExpand
    };
}
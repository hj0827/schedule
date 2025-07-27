// 表格
import { nextTick, onMounted, reactive, ref } from "vue";
import { ListParm } from "../../api/teacher/TeacherType";
import { getListApi } from "../../api/teacher/teacher";
export default function useTable() {
    // 定义表格高度
    const tableHeight = ref(0)
    // 定义获取数据的参数
    const listParm = reactive<ListParm>({
        currentPage: 1,
        pageSize: 10,
        teacherName: ''
    })
    // 定义表格数据
    const tableList = reactive({
        list: []
    })
    // 定义表格的列
    const columns = [
        {
            title: '教师姓名',
            key: 'teacherName',
            dataIndex: 'teacherName'
        },
        {
            title: '教师编号',
            key: 'teacherNum',
            dataIndex: 'teacherNum'
        },
        {
            title: '备注',
            key: 'teacherDesc',
            dataIndex: 'teacherDesc'
        },
        {
            title: '身份证号',
            key: 'idCard',
            dataIndex: 'idCard'
        },
        {
            title: '手机号码',
            key: 'phone',
            dataIndex: 'phone'
        },
        {
            title: '开户行',
            key: 'bankName',
            dataIndex: 'bankName'
        },
        {
            title: '开户市',
            key: 'bankCity',
            dataIndex: 'bankCity'
        },
        {
            title: '支行',
            key: 'bankBranch',
            dataIndex: 'bankBranch'
        },
        {
            title: '银行账号',
            key: 'bankAccount',
            dataIndex: 'bankAccount'
        },
        {
            title: '课酬标准',
            key: 'salaryStandard',
            dataIndex: 'salaryStandard'
        },
        {
            title: '教师类型',
            key: 'teacherType',
            dataIndex: 'teacherType'
        },
        {
            title: '操作',
            key: 'action',
            align: 'center',
            width: 220
        }
    ]

    // 定义分页对象
    const rolePage = reactive({
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '40', '50'],
        showTotal: (total: number) => `共有${total}条数据`,
        // 页数改变时触发
        onChange: (current: number, size: number) => {
            listParm.currentPage = current
            listParm.pageSize = size
            rolePage.current = current
            rolePage.pageSize = size
            getList()
        }
    })

    interface ApiResponse {
        code: number;
        data?: any;
        message?: string;
    }

    // 获取表格数据
    const getList = async () => {
        let res = await getListApi(listParm) as ApiResponse
        if (res && res.code == 200) {
            console.log(res)
            // 设置表格数据
            tableList.list = res.data.records;
            // 设置分页的总条数
            rolePage.total = res.data.total;
        }
    }

    // 搜索
    const searchBtn = () => {
        rolePage.current = 1; 
        listParm.currentPage = 1; 
        getList()
        sessionStorage.setItem('savedteacher', JSON.stringify(listParm));
    }
    // 重置
    const resetBtn = () => {
        listParm.teacherName = ''
        listParm.currentPage = 1
        getList()
        sessionStorage.removeItem('savedteacher');
    }
    onMounted(() => {
        getList()
        nextTick(() => {
            tableHeight.value = window.innerHeight - 310
        })
    })

    return {
        listParm,
        tableList,
        columns,
        rolePage,
        getList,
        tableHeight,
        resetBtn,
        searchBtn
    }
}
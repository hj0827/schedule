// 教室列表
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ListParm } from '../../api/classroom/RoomType'
import { getListApi } from '../../api/classroom/classroom'
interface ApiResponse {
    code: number;
    data?: any;
    msg?: string;
}
export default function useTable() {
    // 定义表格高度
    const tableHeight = ref(0)
    // 定义表格的数据
    const tableList = reactive({
        list: []
    })
    // 定义表格的列
    const columns = [
        {
            title: '直播间名称',
            key: 'roomName',
            dataIndex: 'roomName'
        },
        {
            title: '直播间地址',
            key: 'roomAddress',
            dataIndex: 'roomAddress'
        },
        {
            title: '操作',
            key: 'action',
            align: 'center',
            width: 220
        },
    ]
    // 搜索\分页等数据域
    const rolePage = reactive({
        total: 0,
        current: 1,
        showSizeChanger: true,
        pageSize: 10,
        name: '',
        pageSizeOptions: ['10', '20', '30', '40'],
        showTotal: (total: number) => `共有${total}条数据`,
        // 页数改变时触发
        onChange: (current: number, size: number) => {
            rolePage.current = current
            rolePage.pageSize = size
            listParm.currentPage = current
            listParm.pageSize = size

            getList()

        }
    })

    // 列表查询的参数
    const listParm = reactive<ListParm>({
        pageSize: 10,
        currentPage: 1,
        roomName: ''
    })

    // 列表查询
    const getList = async () => {
        let res = await getListApi(listParm) as ApiResponse
        if (res && res.code === 200) {
            // 设置表格数据
            tableList.list = res.data.records;
            // 设置分页总条数
            rolePage.total = res.data.total;

        }
    }
    // 搜索
    const searchBtn = () => {
        rolePage.current = 1; 
        listParm.currentPage = 1; 
        getList()
        sessionStorage.setItem('classroomSearchParm', JSON.stringify(listParm));
    }
    // 重置
    const resetBtn = () => {
        listParm.roomName = ''
        listParm.currentPage = 1
        getList()
        sessionStorage.removeItem('classroomSearchParm');
    }


    onMounted(() => {
        getList()
        nextTick(() => {
            // 计算表格高度
            tableHeight.value = window.innerHeight - 310
        })
    })
    return {
        tableHeight,
        tableList,
        columns,
        rolePage,
        listParm,
        getList,
        resetBtn,
        searchBtn

    }
}
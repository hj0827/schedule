import { getListApi } from '@/api/role/role'
import { RoleListParm } from '@/api/role/RoleType'
import { nextTick } from 'vue'
import { onMounted, reactive, ref } from 'vue'


export default function useTable() {
    // 表格的高度
    const tableHeight = ref(0)
    // 表格数据
    const tableList = reactive({
        list: []
    })

    
    // 表格的列
    const columns = [
        {
            title: '角色名称',
            dataIndex: 'roleName',
            key: 'roleName'
        },
        {
            title: '角色描述',
            dataIndex: 'roleDesc',
            key: 'roleDesc'

        },
        {
            title: '操作',
            dataIndex: 'action',
            key: 'action',
            width:340
        }
    ]
    // 表格的分页
    // 分页
    const rolePage = reactive({
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '40', '50'],
        showTotal: (total: number) => `共有${total}条数据`,
        // 页容量改变时触发
        // 页数改变时触发
        onChange: (current: number, size: number) => {
            listParm.currentPage = current
            listParm.pageSize = size
            rolePage.current = current
            rolePage.pageSize = size
            getList()
        }
    })

    // 列表参数
    const listParm = reactive<RoleListParm>({
        pageSize: 10,
        currentPage: 1,
        roleName: ''
    })

    // 获取列表
    const getList = async () => {
        let res = await getListApi(listParm) as any
        if (res && res.code === 200) {
            // 把数据赋值给表格
            tableList.list = res.data.records
        }
    }
    // 搜索按钮
    const searchBtn = () => {
        getList()
    }
    // 重置
    const resetBtn = () => {
        listParm.currentPage = 1
        listParm.roleName = ''
        getList()
    }
    onMounted(() => {
        getList()
        nextTick(() => {
            // 计算表格的高度
            tableHeight.value = window.innerHeight - 300
        })
    })
    return {
        tableHeight,
        tableList,
        columns,
        listParm,
        rolePage,
        getList,
        resetBtn,
        searchBtn
    }
}
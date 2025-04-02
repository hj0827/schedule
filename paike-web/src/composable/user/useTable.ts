
import { ref, reactive, onMounted } from "vue";
import { getListApi } from "../../api/user/user";

export default function userTable() {
    // 表格高度
    const tableHeight = ref(0)
    // 表格列
    const columns = [
        {
            title: '姓名',
            dataIndex: 'name',
            key: 'name'
        },
        {
            title: '电话',
            dataIndex: 'phone',
            key: 'phone'
        },
        {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email'
        },
        {
            title: '性别',
            dataIndex: 'sex',
            key: 'sex'
        },
        {
            title: '登录账户',
            dataIndex: 'username',
            key: 'username'
        },
        {
            title: '操作',
            dataIndex: 'action',
            key: 'action',
            align: 'center',
            width: 220
        }
    ]
    // 表格数据
    const tableList = reactive({
        list: []
    })
    // 表单列表参数
    const listParm = reactive<any>({
        pageSize: 10,
        currentPage: 1,
        phone: '',
        name: '',
    })
    // 分页对象
    const page = reactive({
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        pageSizeOptions: [10, 20, 30, 40],
        showQuickJumper: true,
        showTotal: (total: number) => `共有${total} 条数据`,
        onChange: (current: number, size: number) => {
            listParm.currentPage = current;
            listParm.pageSize = size;
            page.current = current;
            page.pageSize = size;
            getList();
        }
    })
    // 获取表格的数据
    const getList = async () => {
        const res = await getListApi(listParm) as any
        if (res && res.code == 200) {
            // 设置表格数据
            tableList.list = res.data.records;
            // 设置分页数据
            page.total = res.data.total;
        }
    }
    const rolePage = reactive({
        current: 1,
    });
    // 搜索
    const searchBtn = () => {
        rolePage.current = 1; 
        listParm.currentPage = 1; 
        getList()
        sessionStorage.setItem('searchState', JSON.stringify(listParm));
    }
    // 重置
    const resetBtn = () => {
        listParm.phone = ''
        listParm.name = ''
        listParm.currentPage = 1
        getList()
        sessionStorage.removeItem('searchState');
    }
    
    // 刷新表格
    const refresh = () => {
        getList()
    }

    // 选中的行
    const selectedRowKeys = ref<string[]>([]);
    const onSelectChange = (selectedKeys: string[]) => {
        selectedRowKeys.value = selectedKeys;
    };

    onMounted(() => {
        getList();
        // 计算表格的高度
        tableHeight.value = window.innerHeight - 310
    })

    return {
        tableHeight,
        tableList,
        listParm,
        page,
        getList,
        columns,
        searchBtn,
        resetBtn,
        refresh,
        selectedRowKeys,
        onSelectChange
    }
}
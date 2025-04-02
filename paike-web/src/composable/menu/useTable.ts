import { getListApi } from "@/api/menu/menu" 
import { onMounted, reactive,nextTick,ref,h } from "vue"
import { CaretRightOutlined,CaretDownOutlined } from "@ant-design/icons-vue";

export default function useTable(){
    // 表格数据
    const tableList = reactive({
        list:[]
    });
    // 表格高度
    const tableHeight = ref(0)
    // 表格的列
    const columns = [
        {
            title:'菜单名称',
            dataIndex:'title',
            key:'title',
            width:180
        },
        {
            title:'权限字段',
            dataIndex:'code',
            key:'code',
            width:180
        },
        {
            title:'图标',
            dataIndex:'icon',
            key:'icon'
        },
        {
            title:'上级菜单',
            dataIndex:'parentName',
            key:'parentName'
        },
        {
            title:'菜单类型',
            dataIndex:'type',
            key:'type'
        },
        {
            title:'路由名称',
            dataIndex:'name',
            key:'name'
        },
        {
            title:'路由地址',
            dataIndex:'path',
            key:'path'
        },
        {
            title:'组件路径',
            dataIndex:'url',
            key:'url'
        },
        {
            title:'操作',
            dataIndex:'action',
            key:'action',
            align:'center',
            width:230 
        },
    ]


    // 获取表格数据
    const getList = async()=>{
        let res = await getListApi() as any
        if(res && res.code == 200){
            tableList.list = res.data
        }
    }

    // 自定义展开图标
    const expandIcon = (props:any) => {
        const { expanded, onExpand,record } = props;
        if(expanded){
            return h(CaretDownOutlined, {
                class:'iconClass',
                onClick:(event:Event)=>{
                    onExpand(record, event);
                }
            });
        }else{
            return h(CaretRightOutlined, {
                class:'iconClass',
                onClick:(event:Event)=>{
                    onExpand(record, event);
                }
            });
        }
    }
    // 刷新表格数据
    const refresh = ()=>{
        getList()
    }

    onMounted(()=>{
        getList()
        nextTick(()=>{
        // 表格高度计算
        tableHeight.value = window.innerHeight - 250;
        })
    })
    return{
        tableList,
        tableHeight,
        getList,
        columns,
        expandIcon,
        refresh
    }
}
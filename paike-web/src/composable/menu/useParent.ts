import { TreeProps } from 'ant-design-vue';
import { onMounted, reactive, ref } from 'vue'
import { getParentListApi } from '@/api/menu/menu';
import { TreeNode } from '@/api/menu/MenuType';
export default function useParent(){
    const showLine = ref<boolean>(true);
    // 定义树形数据
    const treeData = ref<TreeProps['treeData']>([]);
    // 树属性配置
    const fieldNames = reactive({
        children: 'children',
        title: 'title',
        key: 'menuId'
    })
    // 获取树的数据
    const getTreeData = async () => {
        const res = await getParentListApi() as any;
        if(res.code === 200){
            treeData.value = res.data;
        }
    }
    // 选中的数据类型
    const selectNode = reactive<TreeNode>({
        parentId: '',
        parentName: ''
    })
    const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
        // console.log('keys:', selectedKeys);
        // console.log('info:', info);
        // selectNode.parentId = selectedKeys[0] as string;
        // selectNode.parentName = info.node.title;
        selectNode.parentId = info.node.menuId;
        selectNode.parentName = info.node.title;
    }

    onMounted(() => {
        getTreeData()
    })
    return{
        showLine,
        treeData,
        fieldNames,
        onSelect,
        selectNode,
        getTreeData
    }
}
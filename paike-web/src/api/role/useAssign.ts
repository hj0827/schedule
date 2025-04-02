import { getAssingShowApi } from "./role"
import { ref, reactive } from "vue"
import type { TreeProps } from "ant-design-vue"
import { TreeParm } from "./RoleType"


export default function useAssign() {
    // 定义树的ref属性
    const assignTree = ref()
    // 是否显示连接线
    const showLine = ref<boolean>(true)
    // 回显的数据
    const checkedKeys = ref<number[]>([])
    // 树属性配置
    const fieldNames = reactive({
        children: 'children',
        title: 'title',
        key: 'menuId'
    })
    // 定义树的数据
    const treeData = ref<TreeProps['treeData']>([])
    // 获取树的数据
    const getTreeData = async (parm: TreeParm) => {
        let res = await getAssingShowApi(parm) as any
        if (res && res.code === 200) {
            // 设置树的数据
            treeData.value = res.data.menuList;
            // 设置选中的数据
            checkedKeys.value = res.data.checkList;
        }
        // 如果有回显的数据
        if (checkedKeys.value.length > 0) {
            let newArr: number[] = [];
            checkedKeys.value.forEach((item) => {
                checked(item, treeData.value, newArr)
            })
            checkedKeys.value = newArr;
        }
    }
    // 查询回显的数据
    const checked = (id: string | number, data: any, newArr: any) => {
        data.forEach((item: any) => {
            if (item.menuId == id) {
                if (item.children && item.children.length == 0) {
                    newArr.push(item.menuId)
                }
            } else {
                if (item.children && item.children.length != 0) {
                    checked(id, item.children, newArr)
                }
            }

        })
    }
    return {
        assignTree,
        showLine,
        checkedKeys,
        fieldNames,
        treeData,
        getTreeData
    }
}